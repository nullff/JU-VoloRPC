package com.qimoju.jurpc.fault.tolerant;

import com.qimoju.jurpc.model.RpcResponse;

import java.util.Map;

/**
 * 快速失败 - 容错策略（立刻通知外层调用方）
 *
 */
public class FailFastTolerantStrategy implements TolerantStrategy {

    /**
     * 重写doTolerant方法以处理容错逻辑
     * 当服务出现错误时，本方法将被调用，用以定义如何处理异常情况
     *
     * @param context 包含上下文信息的映射，用于提供给调用者
     * @param e       服务执行过程中抛出的异常
     * @return 由于本方法直接抛出运行时异常，不返回任何值
     *
     * 注意：本方法的设计目的是增强系统的容错性，通过接收异常并在此基础上进行自定义的错误处理
     * 这里选择直接抛出运行时异常，是为了通知调用者服务出现了未预期的错误，并提供详细的错误信息
     */
    @Override
    public RpcResponse doTolerant(Map<String, Object> context, Exception e) {
        throw new RuntimeException("服务报错", e);
    }
}

