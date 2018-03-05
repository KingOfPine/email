package com.lsy.send.email.compoment;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.env.Environment;

/**
 * @author liangsongying
 * @date 2018/3/2 10:41
 * 判断所处环境类
 **/
public class ProfileManager {

    private Environment env;

    public ProfileManager(Environment env) {
        this.env = env;
    }

    /**
     * 是否开发环境
     *
     * @return
     */
    public boolean isDev() {
        return ArrayUtils.contains(env.getActiveProfiles(), "dev");
    }

    /**
     * 是否测试环境
     *
     * @return
     */
    public boolean isTest() {
        return ArrayUtils.contains(env.getActiveProfiles(), "test");

    }

    /**
     * 是否uat环境
     *
     * @return
     */
    public boolean isUat() {
        return ArrayUtils.contains(env.getActiveProfiles(), "uat");
    }

    /**
     * 是否生产环境
     *
     * @return
     */
    public boolean isProduct() {
        return ArrayUtils.contains(env.getActiveProfiles(), "prod");
    }

    /**
     * 返回当前出于什么环境
     *
     * @return
     */
    public String currentEnvironment() {
        if (isDev()) {
            return "开发坏境";
        }
        if (isTest()) {
            return "测试环境";
        }
        if (isUat()) {
            return "uat环境";
        }
        if (isProduct()) {
            return "生产环境";
        }
        {
            return "未知环境";
        }
    }
}
