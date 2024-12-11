package com.qimoju.jurpc.server;

import com.qimoju.jurpc.RpcApplication;
import com.qimoju.jurpc.config.RpcConfig;
import com.qimoju.jurpc.model.RpcRequest;
import com.qimoju.jurpc.model.RpcResponse;
import com.qimoju.jurpc.registry.LocalRegistry;
import com.qimoju.jurpc.serializer.JdkSerializer;
import com.qimoju.jurpc.serializer.Serializer;
import com.qimoju.jurpc.serializer.SerializerFactory;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        /**
         * 处理HTTP请求
         * 1. 反序列化请求为对象，并从请求对象中获取参数。
         * 2. 根据服务名称从本地注册器中获取到对应的服务实现类。
         * 3. 通过反射机制调用方法，得到返回结果。
         * 4. 对返回结果进行封装和序列化，并写入到响应中。
         *
         * @param request HTTP请求对象
         */
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 记录日志
        System.out.println("Received request：" + request.method() + " " + request.uri());

        // 异步处理HTTP请求
        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            RpcRequest rpcRequest = null;
            try {
                rpcRequest = serializer.deserialize(bytes, RpcRequest.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // 构造响应结果对象
            RpcResponse rpcResponse = new RpcResponse();
            // 如果请求为null直接返回
            if (rpcRequest == null) {
                rpcResponse.setMessage("Request is null");
                doResponse(request, rpcResponse, serializer);
                return;
            }

            try {
                // 获取要调用的服务实现类，通过反射调用
                Class<?> implClass = LocalRegistry.get(rpcRequest.getServiceName());
                Method method = implClass.getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcRequest.getArgs());
                // 封装返回结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage("Error:" + e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request, rpcResponse, serializer);
        });
    }

    /**
     * 响应HTTP请求
     *
     * @param request     HTTP请求对象
     * @param rpcResponse RPC响应对象
     * @param serializer  序列化器
     */
    void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // 序列化
            byte[] serialized = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }

}
