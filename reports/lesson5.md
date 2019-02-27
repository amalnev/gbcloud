## Отчет по домашнему заданию к уроку №5.

### Выполненные работы.

1. Учтены замечания Code Review к организации клиент-серверного взаимодействия на java.nio. 
2. Добавлена функциональность передачи файлов (команды **get** и **put**).
3. Добавлена функциональность создания директорий в облаке (команда **rmkdir**).
4. Добавлена функциональность удаления объектов в облаке (команда **rrm**).

### Вопросы
Прошу повторно посмотреть код класса **common.transport.NioTransportChannel** после исправления. Задача данного класса - отправка и получение сериализованных POJO-объектов через **java.nio.SocketChannel** в асинхронном режиме ввода-вывода.
```@Nio
      @Interceptors(CommonLogger.class)
      public class NioTransportChannel implements ITransportChannel
      {
          //Пересылаемые и принимаемые пакеты имеют следующий формат:
          //1. int magic - магическое число.
          //2. int objectSize - размер сериализованного объекта IMessage.
          //3. байты сериализованного объекта IMessage.
      
          private static final int MAGIC = 114576;
      
          private static final int READ_BUFFER_CAPACITY = 10 * 1024 * 1024;
      
          private static final int HEADER_SIZE = 8;
      
          private static final int MAXIMUM_OBJECT_SIZE = READ_BUFFER_CAPACITY - HEADER_SIZE;
      
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
                  final ByteBuffer messageBuffer = ByteBuffer.allocate(HEADER_SIZE + objectSize);
      
                  //Записываем магическое число и размер отправляемого объекта
                  messageBuffer.putInt(MAGIC);
                  messageBuffer.putInt(objectSize);
      
                  //Здесь происходит копирование данных из буфера, в который объект был сериализован
                  //в байт-буфер для отправки. К сожалению, без такого копирования не обойтись. Если
                  //записать "заголовок" (магию + размер объекта) в отдельный буфер и отправить последовательно
                  //сначала буфер заголовка, а потом буфер объекта, то нет гарантии что данные уйдут в сеть
                  //именно в таком порядке. Поэтому весь отправляемый пакет должен быть сформирован в одном байт-буфере.
                  messageBuffer.put(byteArrayOutputStream.toByteArray());
                  messageBuffer.flip();
                  socketChannel.write(messageBuffer);
              }
              catch (IOException e)
              {
                  close();
                  throw e;
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
                  if (readBuffer.remaining() < HEADER_SIZE) readBytes(HEADER_SIZE - readBuffer.remaining());
      
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
                  try
                  {
                      //пытаемся десериализовать из ObjectInputStream объект реализующий IMessage
                      final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
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
                      readBuffer.position(readBuffer.position() + objectSize);
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
          public int getMTU()
          {
              //return 10240;
              return (int)(0.75f * MAXIMUM_OBJECT_SIZE);
          }
      
          @Override
          public void close() throws IOException
          {
              if (socketChannel != null)
                  socketChannel.close();
          }
      }
```