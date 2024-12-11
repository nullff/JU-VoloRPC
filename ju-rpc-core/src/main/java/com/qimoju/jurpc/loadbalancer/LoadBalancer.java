package com.qimoju.jurpc.loadbalancer;

import com.qimoju.jurpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

/**
 * 负载均衡器接口
 */
public interface LoadBalancer {

    /**
     * 根据参数选择服务
     * @param requestParams
     * @param serviceMetaInfoList
     * @return
     */
    ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList);
}
