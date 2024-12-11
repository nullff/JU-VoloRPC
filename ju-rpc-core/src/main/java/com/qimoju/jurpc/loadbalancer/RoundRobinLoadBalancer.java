package com.qimoju.jurpc.loadbalancer;

import com.qimoju.jurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 轮询负载均衡器
 * 使用 JUC 包的 AtomicInteger 实现原子计数器，防止并发冲突问题。
 *
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    /**
     * 当前轮询的下标
     */
    private final AtomicInteger currentIndex = new AtomicInteger(0);

    /**
     * 选择服务的策略方法
     * 该方法根据当前索引和可用服务列表来选择一个服务
     * 主要使用取模算法来实现简单的轮询策略
     *
     * @param requestParams 请求参数，本方法中未使用，但可能在扩展时用于更复杂的逻辑
     * @param serviceMetaInfoList 服务元信息列表，包含当前可用的所有服务
     * @return 返回选中的服务元信息，如果没有可用服务则返回null
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        // 如果服务列表为空，直接返回null
        if (serviceMetaInfoList.isEmpty()) {
            return null;
        }
        // 只有一个服务，无需轮询
        int size = serviceMetaInfoList.size();
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }
        // 使用取模算法进行轮询
        int index = currentIndex.getAndIncrement() % size;
        return serviceMetaInfoList.get(index);
    }
}
