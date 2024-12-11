package com.qimoju.jurpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC响应
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse implements Serializable {
    /**
     * 响应数据，表示远程调用的结果数据。
     */
    private Object data;

    /**
     * 数据类型，表示响应数据的类型。
     */
    private Class<?> dataType;

    /**
     * 消息，表示远程调用的状态信息或描述。
     */
    private String message;

    /**
     * 异常，表示远程调用过程中发生的异常信息。
     */
    private Exception exception;
}

