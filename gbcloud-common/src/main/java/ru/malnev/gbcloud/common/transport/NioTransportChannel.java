package ru.malnev.gbcloud.common.transport;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import ru.malnev.gbcloud.common.config.CommonConfig;
import ru.malnev.gbcloud.common.logging.CommonLogger;
import ru.malnev.gbcloud.common.messages.IMessage;

import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

@Nio
@Interceptors(CommonLogger.class)
public class NioTransportChannel implements ITransportChannel
{
    private static final int MAGIC = 114576;

    private static final int READ_BUFFER_CAPACITY = 1024 * 1024;

    private static final int MAXIMUM_OBJECT_SIZE = 1024 * 1024 - 8;

    private static final long READ_QUANTUM = 100;

    private static final long MESSAGE_TIMEOUT = 2000;

    public static class ObjectTooLargeException extends Exception {}

    private final ByteBuffer readBuffer = ByteBuffer.allocate(READ_BUFFER_CAPACITY);

    @Getter
    @Setter
    private SocketChannel socketChannel;

    @Override
    @SneakyThrows
    public synchronized void sendMessage(@NotNull IMessage message)
    {
        try
        {
            //Сериализуем объект
            final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(message);
            objectOutputStream.close();

            //Выясняем размер сериализованного объекта и проверяем его
            final int objectSize = byteArrayOutputStream.toByteArray().length;
            if (objectSize >= MAXIMUM_OBJECT_SIZE) throw new ObjectTooLargeException();

            //Выделяем байт-буфер для отправки
            final ByteBuffer messageBuffer = ByteBuffer.allocate(8 + objectSize);

            //Записываем магическое число и размер отправляемого объекта
            messageBuffer.putInt(MAGIC);
            messageBuffer.putInt(objectSize);

            //Здесь происходит копирование данных из буфера, в который объект был сериализован
            //в байт-буфер для отправки.
            messageBuffer.put(byteArrayOutputStream.toByteArray());
            messageBuffer.flip();
            socketChannel.write(messageBuffer);
        }
        catch (IOException e)
        {
            close();
        }

    }

    //данная функция блокирует вызывающий поток до тех пор пока из канала не
    //будет вычитано заданное количество байт или не наступит таймаут.
    //Фактически может быть вычитано больше байт, чем запрашивалось.
    //Данное решение является довольно неудачным, поскольку позволяет одному клиенту достаточно надолго
    //"захватить" поток, который обрабатывает весь сетевой ввод сервера.
    private int readBytes(final int bytesToRead) throws IOException, RemoteEndGone, CorruptedDataReceived
    {
        //переводим буфер в режим записи
        readBuffer.compact();

        //пытаемся дописать в буфер требуемое количество байт
        int bytesRead = 0;
        final long startTime = System.currentTimeMillis();
        final long quantum = READ_QUANTUM;
        while(bytesRead < bytesToRead)
        {
            bytesRead += socketChannel.read(readBuffer);
            if(bytesRead < 0)
            {
                socketChannel.close();
                throw new RemoteEndGone();
            }
            if(bytesRead >= bytesToRead) break;

            try
            {
                Thread.sleep(quantum);
            }
            catch (InterruptedException e)
            {
                break;
            }
            final long delta = System.currentTimeMillis() - startTime;
            if(delta > MESSAGE_TIMEOUT) break;
        }

        //если дописать буфер хотя бы до требуемого размера не получилось, завершаем.
        if (bytesRead < bytesToRead)
        {
            throw new CorruptedDataReceived();
        }

        //в буфере есть требуемое количество байт, переходим обратно в режим чтения.
        readBuffer.flip();

        return bytesRead;
    }

    @NotNull
    @Override
    @SneakyThrows
    public synchronized IMessage readMessage()
    {
        // В начале выполнения данного метода буфер находится в режиме записи.
        try
        {
            //переводим буфер в режим чтения
            readBuffer.flip();

            //если для считывания из буфера доступно меньше 8-ми байт, пытаемся
            //дописать в буфер еще данные из канала.
            if (readBuffer.remaining() < 8) readBytes(8 - readBuffer.remaining());

            //читаем из буфера по 4 байта пока не найдем магическое число
            int magic = 0;
            while (magic != MAGIC)
            {
                try
                {
                    magic = readBuffer.getInt();
                }
                catch (Exception e)
                {
                    //если при чтении дошли до конца буфера и не нашли магическое число, завершаем.
                    throw new CorruptedDataReceived();
                }
            }

            //если магическое число нашли слишком близко к концу буфера,
            //попытаемся еще раз дописать данные из канала
            if (readBuffer.remaining() < 4) readBytes(4 - readBuffer.remaining());

            //читаем из буфера размер ожидаемого объекта
            final int objectSize = readBuffer.getInt();

            //если он оказался слишком большим - завершаем
            if (objectSize >= MAXIMUM_OBJECT_SIZE) throw new ObjectTooLargeException();

            //столько байт нужно дописать в буфер из канала для того чтобы десериализовать
            //ожидаемый объект
            final int bytesToRead = objectSize - readBuffer.remaining();

            //дописываем
            if (bytesToRead > 0) readBytes(bytesToRead);

            //запрашиваем фактический массив байт, управляемый байтбуфером, и конструируем поверх него ObjectInputStream
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(readBuffer.array(), readBuffer.position(), objectSize);
            final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            try
            {
                //пытаемся десериализовать из ObjectInputStream объект реализующий IMessage
                final Object receivedObject = objectInputStream.readObject();
                if (receivedObject instanceof IMessage)
                {
                    //получилось
                    return (IMessage) receivedObject;
                }
            }
            catch (Exception e)
            {
                //объект не был десериализован
                throw new CorruptedDataReceived();
            }
            finally
            {
                //как бы ни закончилось чтение объекта, убираем из буфера обработанные данные
                readBuffer.position(objectSize);
            }

            //объект был прочитан, но он принадлежит к неправильному классу
            throw new CorruptedDataReceived();
        }
        finally
        {
            //В конце выполнения метода независимо от исхода убираем из буфера
            //обработанные данные и переводим его в режим записи
            readBuffer.compact();
        }
    }

    @Override
    public String getRemoteAddress()
    {
        if (!isConnected()) return null;
        return socketChannel.socket().getInetAddress().toString();
    }

    @Override
    public boolean isConnected()
    {
        if (socketChannel == null) return false;
        return socketChannel.isConnected();
    }

    @Override
    public void close() throws IOException
    {
        if (socketChannel != null)
            socketChannel.close();
    }
}
