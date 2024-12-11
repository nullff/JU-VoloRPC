package com.qimoju.jurpc.fault.retry;

import com.github.rholder.retry.*;
import com.qimoju.jurpc.model.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 固定时间间隔 - 重试策略
 *
 */
@Slf4j
public class FixedIntervalRetryStrategy implements RetryStrategy {

    /**
     * 执行重试操作
     *
     * 本方法使用Retryer来执行重试逻辑，主要针对可能抛出异常的调用进行重试
     * 重试策略包括重试间隔和最大重试次数，同时提供了重试监听器以记录重试信息
     *
     * @param callable 一个 Callable 任务，执行可能需要重试的操作
     * @return RpcResponse 类型的结果，表示远程过程调用的响应
     * @throws ExecutionException 如果在执行任务时或重试策略确定不应继续重试时抛出
     * @throws RetryException 如果重试策略确定不应继续重试时抛出
     */
    public RpcResponse doRetry(Callable<RpcResponse> callable) throws ExecutionException, RetryException {
        // 构建一个重试器，用于处理重试逻辑
        Retryer<RpcResponse> retryer = RetryerBuilder.<RpcResponse>newBuilder()
                // 当抛出 Exception 类型的异常时进行重试
                .retryIfExceptionOfType(Exception.class)
                // 设置重试间隔为固定时间，每次重试前等待3秒
                .withWaitStrategy(WaitStrategies.fixedWait(3L, TimeUnit.SECONDS))
                // 设置最大重试次数为3次
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                // 设置重试监听器，用于在每次重试时执行自定义逻辑
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        // 记录重试次数
                        log.info("重试次数 {}", attempt.getAttemptNumber());
                    }
                })
                // 构建重试器
                .build();
        // 使用重试器执行 Callable 任务，并返回结果
        return retryer.call(callable);
    }

}

