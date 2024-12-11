package com.qimoju.jurpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.qimoju.jurpc.config.RegistryConfig;
import com.qimoju.jurpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class EtcdRegistry implements Registry{

    private Client client;

    private KV kvClient;

    /**
     * etcd的根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 本地注册节点的key集合，用于维护服务续期
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    /**
     * 服务缓存，用于存储服务元信息，以方便快速查找服务
     */
    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    /**
     * 正在监听的 key 集合
     */
    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();


    /**
     * 初始化注册中心配置
     *
     * @param registryConfig 注册中心的配置信息，包含地址和超时设置等
     */
    @Override
    public void init(RegistryConfig registryConfig) {
        // 根据注册中心配置信息构建客户端
        client = Client.builder()
                       .endpoints(registryConfig.getAddress())
                       .connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                       .build();

        // 获取KV客户端，用于后续的键值对操作
        kvClient = client.getKVClient();

        //开启心跳
        heartBeat();
    }

    /**
     * 注册服务元信息到ETCD
     *
     * 此方法用于在ETCD中注册服务的元信息它通过创建一个带有租约的键值对来实现，
     * 以确保服务的元信息在一段时间后可以自动过期并被删除
     *
     * @param serviceMetaInfo 服务元信息对象，包含服务的相关信息
     * @throws Exception 如果注册过程中发生错误
     */
    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建lease和kv客户端
        Lease leaseClient = client.getLeaseClient();

        //创建一个30秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registryKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对和租约关联，并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        //将节点添加到本地缓存
        localRegisterNodeKeySet.add(registryKey);
    }

    /**
     * 取消注册服务
     * 通过删除存储在ETCD中的服务元信息来实现服务的取消注册
     *
     * @param serviceMetaInfo 服务元信息，包含服务的节点键等信息
     */
    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        // 删除ETCD中对应的服务节点信息，以实现服务的取消注册
        kvClient.delete(ByteSequence.from(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey(), StandardCharsets.UTF_8));

        //从本地缓存中移除节点信息
        localRegisterNodeKeySet.remove(ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey());
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {

        //优先从缓存获取服务
        List<ServiceMetaInfo> cachedServiceMetaInfoList = registryServiceCache.readCache();
        if (!CollUtil.isEmpty(cachedServiceMetaInfoList)){
            return cachedServiceMetaInfoList;
        }

        // 前缀搜索，结尾一定要加“/”
        // 这里通过拼接ETCD_ROOT_PATH和服务键来构建搜索前缀，用于在Etcd中查找服务
        String searchPrefix = ETCD_ROOT_PATH + serviceKey ;

        try {
            // 前缀查询
            // 构建查询选项，设置为前缀查询，以便获取所有以searchPrefix开头的键值对
            GetOption getOption = GetOption.builder().isPrefix(true).build();

            // 使用构建的选项执行前缀查询，并获取查询结果中的所有键值对
            List<KeyValue> keyValues = kvClient.get(
                    ByteSequence.from(searchPrefix, StandardCharsets.UTF_8),
                    getOption
            )
            .get()
            .getKvs();

            // 解析服务信息
            List<ServiceMetaInfo> serviceMetaInfoList = keyValues.stream()
                    .map(keyValue -> {
                        String key = keyValue.getKey().toString(StandardCharsets.UTF_8);
                        // 监听 key 的变化
                        watch(key);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());

            // 写入服务缓存
            registryServiceCache.writeCache(serviceMetaInfoList);
            return serviceMetaInfoList;
        } catch (Exception e) {
            // 如果查询过程中发生异常，抛出运行时异常，便于调用者处理
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    /**
     * 当前节点下线时执行的操作
     *
     * 本方法主要用于在节点下线时，释放所有占用的资源
     * 包括关闭与客户端的连接和键值存储的客户端连接
     */
    @Override
    public void destroy() {
        //todo 销毁时应当主动从etcd中删除节点
        System.out.println("当前节点下线");
        // 释放资源
        // 关闭与客户端的连接
        if (client != null){
            client.close();
        }
        // 关闭键值存储的客户端连接
        if (kvClient != null){
            kvClient.close();
        }
    }

    /**
     * 心跳机制，用于定期续签服务注册信息，以避免服务因长时间未更新状态而被错误地认为已下线
     * 通过定时任务定期检查并更新服务的注册信息，确保服务的活跃状态被正确维护
     */
    @Override
    public void heartBeat() {
        // 定义一个定时任务，每10秒执行一次续签操作
        // 使用Cron表达式灵活定义任务执行时间，这里设置为每10秒执行一次
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                // 遍历本节点所有的 key，对每个服务的注册信息进行续签
                for (String key : localRegisterNodeKeySet) {
                    try {
                        // 从Etcd中获取当前key对应的服务信息
                        List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                                .get()
                                .getKvs();
                        // 该节点已过期（需要重启节点才能重新注册）
                        if (CollUtil.isEmpty(keyValues)) {
                            continue;
                        }
                        // 节点未过期，重新注册（相当于续签）
                        KeyValue keyValue = keyValues.get(0);
                        String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        // 如果续签过程中发生异常，抛出运行时异常，并提供详细错误信息
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });
        // 支持秒级别定时任务，提高时间调度的精确度
        CronUtil.setMatchSecond(true);
        // 启动定时任务调度器，开始执行心跳检测逻辑
        CronUtil.start();
    }

    /**
     * 监听（消费端）
     *
     * @param serviceNodeKey
     */
    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        // 之前未被监听，开启监听
        boolean newWatch = watchingKeySet.add(serviceNodeKey);
        if (newWatch) {
            watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        // key 删除时触发
                        case DELETE:
                            // 清理注册服务缓存
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        default:
                            break;
                    }
                }
            });
        }
    }


}
