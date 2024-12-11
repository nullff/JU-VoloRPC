package com.qimoju.jurpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Kryo 序列化器
 *
 */
public class KryoSerializer implements Serializer {
    /**
     * kryo 线程不安全，使用 ThreadLocal 保证每个线程只有一个 Kryo
     */
    private static final ThreadLocal<Kryo> KRYO_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        // 设置动态动态序列化和反序列化类，不提前注册所有类（可能有安全问题）
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    /**
     * 序列化对象为字节数组
     * 该方法使用Kryo库来序列化给定的对象
     * Kryo是一个快速、高效的对象图形序列化框架，适用于Java、Scala和其他JVM语言
     * 它特别适合于网络传输或对象持久化，因为它比Java自带的序列化机制更快、更紧凑
     *
     * @param obj 待序列化的对象，可以是任何类型
     * @return 返回序列化后的字节数组
     */
    @Override
    public <T> byte[] serialize(T obj) {
        // 创建一个字节数组输出流，用于存储序列化后的字节数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 创建一个Output对象，封装字节数组输出流，用于输出序列化后的数据
        Output output = new Output(byteArrayOutputStream);
        // 使用Kryo实例（通过KRYO_THREAD_LOCAL线程局部变量获取）将对象写入到Output中
        KRYO_THREAD_LOCAL.get().writeObject(output, obj);
        // 关闭Output流，释放资源
        output.close();
        // 将字节数组输出流中的数据转换为字节数组并返回
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * 反序列化方法，将字节数组转换为指定类型的对象
     * 该方法使用Kryo库进行反序列化，适用于高性能和低内存消耗的场景
     *
     * @param bytes 字节数组，表示待反序列化的数据
     * @param classType 需要反序列化的目标类类型
     * @param <T> 泛型参数，表示目标类类型
     * @return 反序列化后的对象实例
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) {
        // 创建一个ByteArrayInputStream，用于将字节数组转换为输入流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 创建一个Kryo Input对象，用于读取输入流中的数据
        Input input = new Input(byteArrayInputStream);
        // 使用线程本地变量存储的Kryo实例进行对象读取，以避免多线程共享同一个Kryo实例导致的线程安全问题
        T result = KRYO_THREAD_LOCAL.get().readObject(input, classType);
        // 关闭Input对象，释放与之关联的资源
        input.close();
        // 返回反序列化后的对象实例
        return result;
    }
}
