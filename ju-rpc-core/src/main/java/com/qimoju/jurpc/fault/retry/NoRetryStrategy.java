package com.qimoju.jurpc.fault.retry;

import com.qimoju.jurpc.model.RpcResponse;

import java.util.concurrent.Callable;

/**
 * 不重试
 */
public class NoRetryStrategy implements RetryStrategy{
    /**
     * 重试机制的实现方法
     * 当远程调用失败时，通过此方法重新尝试调用
     *
     * @param callable 一个 Callable 任务，包含执行远程调用的逻辑
     * @return RpcResponse 远程调用的响应结果
     * @throws Exception 如果调用过程中发生错误，则抛出异常
     */
    @Override
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws Exception {
        return callable.call();
    }
}
