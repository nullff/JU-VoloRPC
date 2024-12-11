package com.qimoju.consumer;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.qimoju.common.model.User;
import com.qimoju.common.service.UserService;
import com.qimoju.jurpc.model.RpcRequest;
import com.qimoju.jurpc.model.RpcResponse;
import com.qimoju.jurpc.serializer.JdkSerializer;
import com.qimoju.jurpc.serializer.Serializer;

/**
 * 静态代理
 */
public class UserServiceProxy implements UserService {
    /**
     * 重写getUser方法以获取用户信息
     * 本方法通过RPC方式调用远程UserService服务中的getUser方法来获取用户信息
     *
     * @param user 用户对象，作为远程方法调用的参数
     * @return 返回从远程服务获取的用户对象，如果调用失败或发生异常，则返回null
     */
    @Override
    public User getUser(User user) {
        //指定序列化器
        Serializer serializer = new JdkSerializer();
        //构建RpcRequest对象，准备发请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(UserService.class.getName())
                .methodName("getUser")
                .parameterTypes(new Class<?>[]{User.class})
                .args(new Object[]{user})
                .build();
        try {
            //序列化请求对象为字节数组，以便传输
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            byte[] result;
            //发送HTTP POST请求到指定地址，并获取响应结果
            try (HttpResponse httpresponse = HttpRequest.post("http://localhost:8080")
                        .body(bodyBytes)
                        .execute()) {
                result = httpresponse.bodyBytes();
            }
            //反序列化响应结果为RpcResponse对象
            RpcResponse rpcResponse = serializer.deserialize(result, RpcResponse.class);
            //返回响应数据中的用户对象
            return (User) rpcResponse.getData();
        } catch (Exception e) {
            //异常处理：打印异常信息
            e.printStackTrace();
        }
        //如果发生异常或请求失败，返回null
        return null;
    }
}
