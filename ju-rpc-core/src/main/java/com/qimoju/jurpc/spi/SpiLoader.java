package com.qimoju.jurpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.qimoju.jurpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SPI加载器，支持键值对映射
 * 虽然提供了loadAll()方法，但是并不推荐使用，更推荐使用load()方法，按需加载；
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类：接口名 => (key => 实现类)
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 对象实例缓存 （避免重复new），类路径 => 实例对象；单例模式
     */
    private static Map<String, Object> instanceCache = new ConcurrentHashMap<>();

    /**
     * 系统SPI目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";

    /**
     * 用户SPI目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";

    /**
     * SPI扫描目录
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};

    /**
     * 需要动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = Arrays.asList(Serializer.class);

    /**
     * 加载所有SPI（Service Provider Interface）实现类
     * 本方法旨在遍历预定义的类列表，并尝试加载每个类
     * 这对于初始化和发现服务提供者实现非常有用
     */
    public static void loadAll(){
        // 记录开始加载所有SPI实现类的日志
        log.info("加载所有SPI");
        // 遍历待加载的类列表
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            // 加载当前类
            load(aClass);
        }
    }

    /**
     * 加载服务提供者接口（SPI）的实现类
     * 该方法通过扫描指定的资源目录来查找和加载SPI的实现类，并将它们映射到一个Map中
     *
     * @param loadClass 要加载的SPI接口的Class对象
     * @return 包含SPI实现类映射的Map，键是配置文件中的键，值是对应的Class对象
     */
    public static Map<String,Class<?>> load(Class<?> loadClass){
        //记录加载SPI的日志
        log.info("加载SPI:{}",loadClass.getName());
        //初始化一个Map来存储键值对形式的SPI实现类
        Map<String,Class<?>> keyClassMap = new HashMap<>();
        //遍历预定义的资源目录，以加载其中的SPI实现类
        for (String scanDir : SCAN_DIRS) {
            //获取资源目录下所有与loadClass同名的资源文件
            String path = scanDir + loadClass.getName();
            System.out.println("Scanning path: " + path);
            List<URL> resources = ResourceUtil.getResources(path);
            System.out.println("Found resources: " + resources);
            //读取每个资源文件
            for (URL resource : resources) {
                try{
                    //读取资源文件的内容
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    //解析每一行，根据等号分割键值对
                    while ((line = bufferedReader.readLine()) != null){
                        String[] strArray = line.split("=");
                        if (strArray.length > 1){
                            //获取键和值，通过类名加载Class对象，并存入Map中
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                } catch (Exception e){
                    //记录加载SPI时出现的异常
                    log.error("加载SPI异常",e);
                }
            }
        }
        //将加载的SPI实现类映射存入全局的loaderMap中，以便后续使用
        loaderMap.put(loadClass.getName(),keyClassMap);
        //返回包含SPI实现类映射的Map
        return keyClassMap;
    }

    /**
     * 根据给定的类和键获取对应的实例
     * 该方法主要用于实现SPI(Service Provider Interface)机制，通过反射创建实例
     *
     * @param tClass 要实例化的类的Class对象
     * @param key 用于区分不同实现的键
     * @param <T> 泛型参数，表示要返回的实例的类型
     * @return 返回指定类和键对应的实例
     * @throws RuntimeException 如果找不到对应的实现类或实例化失败，则抛出运行时异常
     */
    public static <T> T getInstance(Class<T> tClass, String key) {
        // 获取类的全限定名
        String tClassName = tClass.getName();
        // 从loaderMap中获取对应类名的实现类映射
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        // 如果没有找到对应的实现类映射，抛出异常
        if (keyClassMap == null){
            log.error(String.format("未找到对应的SPI实现类，未加载 %s 类型", tClassName));
            throw new RuntimeException(String.format("未找到对应的SPI实现类，未加载 %s 类型", tClassName));
        }
        // 如果映射中没有指定键的实现类，抛出异常
        if (!keyClassMap.containsKey(key)){
            log.error(String.format("未找到对应的SPI实现类，未找到 %s 类型的 %s 实现类", tClassName, key));
            throw new RuntimeException(String.format("未找到对应的SPI实现类，未找到 %s 类型的 %s 实现类", tClassName, key));
        }
        // 获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        // 从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)){
            try{
                // 如果缓存中没有关于该类型的实例，则尝试重新实例化
                instanceCache.put(implClassName,implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e){
                // 如果实例化失败，记录日志并抛出异常
                String errMsg = String.format("实例化 %s 失败", implClassName);
                log.error(errMsg,e);
                throw new RuntimeException(errMsg,e);
            }
        }
        // 返回实例，由于使用了泛型，这里需要进行强制类型转换
        return (T) instanceCache.get(implClassName);
    }

}
