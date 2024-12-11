package com.qimoju.jurpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 */
public class ConfigUtils {

    /**
     * 根据指定的前缀加载配置，并返回配置对象
     * 此方法是重载方法，允许调用者不指定默认值
     *
     * @param <T> 配置对象的类型
     * @param clazz 配置对象的类
     * @param prefix 配置的前缀
     * @return 返回加载的配置对象
     */
    public static <T> T loadConfig(Class<T> clazz,String prefix) {
        return loadConfig(clazz,prefix,"");
    }
    /**
     * 加载配置文件并转换为指定类型的配置对象
     * 该方法根据环境参数动态决定加载的配置文件，然后将配置文件中的属性转换为指定类型的配置对象
     * 主要用于简化配置文件的加载和处理，提高代码的可维护性和可读性
     *
     * @param clazz 配置对象的类类型，指定需要转换的目标类型
     * @param prefix 配置项的前缀，用于筛选配置文件中的相关属性
     * @param environment 环境参数，如"dev"、"prod"等，用于指定特定环境的配置文件
     * @return 返回指定类型的配置对象，包含从配置文件中读取的属性
     */
    public static <T> T loadConfig(Class<T> clazz,String prefix,String environment) {
        // 构建配置文件名，格式为"application-环境.properties"
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        // 加载配置文件
        Props props = new Props(configFileBuilder.toString());
        // 将配置文件中的属性转换为指定类型的配置对象
        return props.toBean(clazz,prefix);
    }
}
