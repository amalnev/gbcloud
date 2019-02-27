package ru.malnev.gbcloud.common.filesystem;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Set;

public class NioFileReader extends NioFile implements IFileReader
{
    private static final int FILE_BUFFER_CAPACITY = 10 * 1024 * 1024;

    private ByteBuffer fileBuffer = ByteBuffer.allocate(FILE_BUFFER_CAPACITY);

    @Override
    protected Set<StandardOpenOption> getOpenOptions()
    {
        final Set<StandardOpenOption> options = new HashSet<>();
        options.add(StandardOpenOption.READ);
        return options;
    }

    @Override
    public byte[] read(int bytesToRead) throws IOException
    {
        //TODO: Уточнить исключение.
        if(bytesToRead > FILE_BUFFER_CAPACITY) throw new IOException();

        //Изначально буфер находится в режиме записи

        try
        {
            //Переводим буфер в режим чтения
            fileBuffer.flip();

            //если в буфере недостаточно данных - дописываем из файла
            if (fileBuffer.remaining() < bytesToRead)
            {
                //столько байт нужно дописать
                final int bytesNeeded = bytesToRead - fileBuffer.remaining();

                //переводим буфер в режим записи
                fileBuffer.compact();

                int bytesRead = 0;
                while (bytesRead < bytesNeeded)
                {
                    bytesRead += byteChannel.read(fileBuffer);
                    if(bytesRead < 0) break;
                }

                //в данный момент у нас в буфере либо есть запрошенное
                //количество байт, либо файл закончился
                fileBuffer.flip();
            }

            //возвращаем результат
            final int bytesInBuffer = fileBuffer.remaining();
            final int bytesToReturn = bytesInBuffer < bytesToRead ? bytesInBuffer : bytesToRead;
            final byte[] result = new byte[bytesToReturn];
            if(bytesToReturn > 0) fileBuffer.get(result);
            return result;
        }
        finally
        {
            //убираем из буфера отработанные данные, переводим буфер в режим записи
            fileBuffer.compact();
        }
    }
}
