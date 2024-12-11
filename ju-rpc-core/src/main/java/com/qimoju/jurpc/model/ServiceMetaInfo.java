package com.qimoju.jurpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息（注册信息）
 */
@Data
public class ServiceMetaInfo {
    // 服务名
    private String serviceName;
    // 服务版本
    private String serviceVersion = "1.0";
    // 服务地址
    private String serviceHost;
    // 服务端口
    private Integer servicePort;
    // 服务分组(暂未实现)
    private String serviceGroup = "default";

    /**
     * 获取服务的唯一键
     *
     * 服务键是由服务名称和服务版本组成的字符串，格式为"serviceName:serviceVersion"
     * 这种设计允许在大型系统中唯一标识每个服务，尤其是当系统中存在多个版本的同一服务时
     *
     * @return 服务的唯一键
     */
    public String getServiceKey(){
        //后续可以扩展服务分组
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 生成服务节点键
     *
     * 服务节点键是用于唯一标识一个服务节点的字符串，它由服务键、服务版本和服务主机组成，
     * 格式为：{服务键}:{服务版本}:{服务主机}这种格式便于在分布式系统中快速定位和识别特定的服务实例
     *
     * @return 服务节点键，格式为{服务键}:{服务版本}:{服务主机}
     */
    public String getServiceNodeKey(){
        return String.format("%s:%s:%s", getServiceKey(), serviceVersion, serviceHost);
    }

    public String getServiceAddress(){
        if (!StrUtil.contains(serviceHost, "http")){
//            return String.format("http://%s:%s", serviceHost, servicePort);
            //todo http改为TCP记得改这里
            return String.format("%s", serviceHost);
        }
        return String.format("%s", serviceHost );
    }
}
