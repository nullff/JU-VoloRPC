package com.qimoju.jurpc.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Hessian 序列化器
 *
 */
public class HessianSerializer implements Serializer {
    /**
     * 序列化对象为字节数组
     * 该方法使用Hessian协议来序列化对象Hessian是一种轻量级的，跨语言的，无侵入式的远程调用协议
     * 它允许不同语言编写的服务之间进行高效的数据交换
     *
     * @param object 待序列化的对象，可以是任何类型的对象
     * @return 返回序列化后的字节数组
     * @throws IOException 如果序列化过程中发生I/O错误
     */
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        // 创建一个字节流对象，用于存储序列化后的数据
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // 创建一个Hessian输出流对象，用于进行序列化操作
        HessianOutput ho = new HessianOutput(bos);
        // 将对象写入到Hessian输出流中，即进行序列化操作
        ho.writeObject(object);
        // 将字节流转换为字节数组并返回
        return bos.toByteArray();
    }

    /**
     * 反序列化指定字节码数组为指定类型的对象实例
     * 该方法使用Hessian库来解析字节码数组，并将其转换回原始对象的形式
     * Hessian是一种轻量级的远程调用协议，支持跨语言的远程过程调用
     *
     * @param bytes 要反序列化的字节码数组，代表一个序列化的对象
     * @param tClass 指定要反序列化对象的目标类类型
     * @return 返回反序列化后的对象实例，具体类型由tClass参数指定
     * @throws IOException 如果反序列化过程中发生I/O错误
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> tClass) throws IOException {
        // 创建一个ByteArrayInputStream，用于包装字节码数组，使其可以作为输入流使用
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        // 创建一个HessianInput对象，用于解析输入流中的数据
        // HessianInput是Hessian库提供的一个类，用于反序列化对象
        HessianInput hi = new HessianInput(bis);
        // 使用HessianInput对象读取并解析输入流中的数据，将其转换为指定类型的对象实例
        // 这里使用了泛型转换，将解析后的对象强制转换为指定的类型
        return (T) hi.readObject(tClass);
    }
}

