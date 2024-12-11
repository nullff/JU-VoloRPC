package com.qimoju.jurpc.model;

import com.qimoju.jurpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    /**
     * 服务名称，标识远程服务的唯一标识符。
     */
    private String serviceName;

    /**
     * 方法名称，标识需要调用的服务中的具体方法。
     */
    private String methodName;

    /**
     * 服务版本，标识服务的版本信息。
     */
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;

    /**
     * 参数类型数组，表示方法参数的类型列表。
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数值数组，表示方法调用时传递的具体参数值。
     */
    private Object[] args;
}

