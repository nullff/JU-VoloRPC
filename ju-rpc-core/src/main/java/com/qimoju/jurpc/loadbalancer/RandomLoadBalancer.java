package com.qimoju.jurpc.loadbalancer;

import com.qimoju.jurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 随机负载均衡器
 * Java 自带的 Random 类实现
 *
 */
public class RandomLoadBalancer implements LoadBalancer {

    private final Random random = new Random();

    /**
     * 重写 select 方法，用于从服务列表中选择一个服务
     * 此方法实现了服务的随机选择逻辑，当列表为空时返回 null，以简化调用方的处理逻辑
     * 当列表中只有一个服务时直接返回，避免不必要的随机选择操作，提高效率
     *
     * @param requestParams 请求参数，本方法中未使用，但保留参数以符合接口要求或未来可能的扩展需要
     * @param serviceMetaInfoList 服务元信息列表，从中选择一个服务
     * @return ServiceMetaInfo 返回选中的服务元信息，如果列表为空则返回 null
     */
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        // 获取服务列表的大小，用于后续的条件判断和随机选择
        int size = serviceMetaInfoList.size();

        // 如果服务列表为空，直接返回 null，表示没有可用的服务
        if (size == 0) {
            return null;
        }

        // 只有 1 个服务，不用随机
        if (size == 1) {
            return serviceMetaInfoList.get(0);
        }

        // 当服务列表有多个服务时，使用随机数选择一个服务返回
        // 这里使用随机选择是为了平衡负载，确保每个服务都有被选中的机会
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}

