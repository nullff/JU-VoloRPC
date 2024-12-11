package com.qimoju.jurpc.serializer;

import java.io.*;

public class JdkSerializer implements Serializer{
    /**
     * 序列化对象为字节数组
     * 该方法使用Java的ObjectOutputStream将给定的对象序列化为一个字节数组
     * 序列化是将对象转换为字节流的过程，通常用于保存对象的状态或网络传输
     *
     * @param obj 待序列化的对象，可以是任何类型，但必须实现Serializable接口
     * @return 返回序列化后的字节数组
     * @throws IOException 如果序列化过程中发生I/O错误
     */
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        // 创建一个字节数组输出流，用于存储序列化后的字节数据
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // 创建一个对象输出流，用于将对象写入到字节数组输出流中
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        try {
            // 将对象序列化为字节流并写入到outputStream中
            objectOutputStream.writeObject(obj);
            // 获取序列化后的字节数组并返回
            return outputStream.toByteArray();
        } catch (IOException e) {
            // 如果捕获到IOException，将其包装为RuntimeException并抛出
            throw new RuntimeException(e);
        } finally {
            // 关闭对象输出流，释放资源
            objectOutputStream.close();
        }
    }

    /**
     * 反序列化方法，将字节码数据转换为指定类的对象
     * <p>
     * 本方法通过使用Java的ObjectInputStream从字节码数据中读取对象，实现了从字节码到对象的转换
     * 它主要用于在网络传输或数据存储后，将接收到的字节码数据还原为原始对象
     *
     * @param data 要反序列化的字节码数据
     * @param clazz 目标对象的类类型，用于指定反序列化后对象的类型
     * @return 反序列化后的对象实例，类型为clazz指定的类型
     * @throws IOException 如果在反序列化过程中发生I/O错误
     */
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws IOException {
        // 创建一个ByteArrayInputStream，将字节码数据作为输入流
        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        // 创建一个ObjectInputStream，用于从输入流中读取对象
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        try {
            // 从ObjectInputStream中读取对象，并将其转换为指定的类类型
            return (T) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            // 当读取的对象类类型在当前类路径中找不到时，抛出运行时异常
            throw new RuntimeException(e);
        } finally {
            // 关闭ObjectInputStream，释放资源
            objectInputStream.close();
        }
    }
}
