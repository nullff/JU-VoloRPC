package com.qimoju.jurpc.server.tcp;


import com.qimoju.jurpc.server.HttpServer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;

public class VertxTcpServer implements HttpServer {

    private byte[] handleRequest (byte[] requestData){
        //在这里编写处理请求的逻辑，根据requestData构造响应数据并返回
        //这里只是一个示例，实际逻辑根据业务需求实现
        return "Hello, client!".getBytes();
    }
    @Override
    public void doStart(int port) {
        // 创建Vertx实例
        Vertx vertx = Vertx.vertx();
        // 创建TCP服务器
        NetServer server = vertx.createNetServer();

        // 设置连接处理程序，每当有新的连接时触发
        server.connectHandler(new TcpServerHandler());

        // 启动TCP服务器并监听指定端口
        server.listen(port, result -> {
            if (result.succeeded()) {
                // 如果服务器成功启动，则打印启动信息
                System.out.println("TCP server started on port " + port);
            } else {
                // 如果服务器启动失败，则打印错误信息
                System.err.println("TCP server failed to start" + result.cause());
            }
        });
    }

}
