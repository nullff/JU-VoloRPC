package com.qimoju.jurpc.fault.tolerant;

import com.qimoju.jurpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * 静默处理异常 - 容错策略
 *
 */
@Slf4j
public class FailSafeTolerantStrategy implements TolerantStrategy {

    /**
     * 重写doTolerant方法以处理异常情况
     * 该方法主要用于在出现异常时进行静默处理，避免程序中断执行
     *
     * @param context 包含异常处理上下文信息的映射，用于提供异常处理所需的额外信息
     * @param e       发生的异常对象，用于记录和处理异常信息
     * @return 返回一个空的RpcResponse对象作为异常处理的结果，确保调用方可以收到响应
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        // 记录异常信息，同时避免对调用方产生影响
        log.info("静默处理异常", e);
        // 返回空的响应对象，作为异常处理的默认行为
        return new RpcResponse();
    }
}

