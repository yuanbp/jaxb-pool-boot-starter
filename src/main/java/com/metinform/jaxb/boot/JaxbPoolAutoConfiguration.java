package com.metinform.jaxb.boot;

import com.metinform.jaxb.JaxbHelper;
import com.metinform.jaxb.PoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author chieftain
 * @date 2020/3/18 11:06
 */
@Configuration
@ConditionalOnClass({SpringBootApplication.class})
public class JaxbPoolAutoConfiguration {

    @Bean
    public JaxbHelper jaxbHelper() {
        return new JaxbHelper(true, false);
    }

    @Value("${jaxb.pool.max-pool-size:10}")
    public void setMaxPoolSize(int maxPoolSize) {
        PoolConfig.maxPoolSize = maxPoolSize;
    }
    @Value("${jaxb.pool.idle-pool-size:1}")
    public void setIdlePoolSize(int idlePoolSize) {
        PoolConfig.idlePoolSize = idlePoolSize;
    }
}
