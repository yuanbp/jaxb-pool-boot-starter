package com.metinform.jaxb;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class UnmarshallerKeyPoolFactory {
    /**
     * 对象池
     */
    private static GenericKeyedObjectPool<String, Unmarshaller> pool;
    /**
     * 对象池的参数设置
     */
    private static final GenericKeyedObjectPoolConfig CONFIG;

    /**
     * 对象池每个key最大实例化对象数
     */
    private final static int MAX_POOL_SIZE = 30;
    /**
     * 对象池每个key最大的闲置对象数
     */
    private final static int IDLE_POOL_SIZE = 3;

    static {
        CONFIG = new GenericKeyedObjectPoolConfig();
        int max = PoolConfig.maxPoolSize <= 0 ? MAX_POOL_SIZE : PoolConfig.maxPoolSize;
        int idle = PoolConfig.idlePoolSize <= 0 ? IDLE_POOL_SIZE : PoolConfig.idlePoolSize;
        CONFIG.setMaxTotalPerKey(max);
        CONFIG.setMaxIdlePerKey(idle);
        /** 支持jmx管理扩展 */
        CONFIG.setJmxEnabled(true);
        CONFIG.setJmxNamePrefix("UnmarshallerPoolProtocol");
        /** 保证获取有效的池对象 */
        CONFIG.setTestOnBorrow(true);
        CONFIG.setTestOnReturn(true);
    }

    /**
     * 从对象池中获取对象
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static Unmarshaller borrowBean(String key) throws Exception {
        if (pool == null) {
            init();
        }
        return pool.borrowObject(key);
    }

    /**
     * 归还对象
     *
     * @param key
     * @param bean
     */
    public static void returnBean(String key, Unmarshaller bean) {
        if (pool == null) {
            init();
        }
        pool.returnObject(key, bean);
    }

    /**
     * 关闭对象池
     */
    public synchronized static void close() {
        if (pool != null && !pool.isClosed()) {
            pool.close();
            pool = null;
        }
    }

    /**
     * 初始化对象池
     */
    private synchronized static void init() {
        if (pool != null) {
            return;
        }
        pool = new GenericKeyedObjectPool<String, Unmarshaller>(new UnmarshallerPooledFactory(), CONFIG);
    }

    /**
     * 对象工厂
     */
    static class UnmarshallerPooledFactory extends BaseKeyedPooledObjectFactory<String, Unmarshaller> {
        /**
         * 创建对象
         *
         * @param key
         * @return
         * @throws Exception
         */
        @Override
        public Unmarshaller create(String key) throws Exception {
            Class clazz = Class.forName(key);
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unMarshaller = context.createUnmarshaller();
            return unMarshaller;
        }

        @Override
        public PooledObject<Unmarshaller> wrap(Unmarshaller value) {
            return new DefaultPooledObject<>(value);
        }

        /**
         * 验证对象是否有效
         *
         * @param key
         * @param p
         * @return
         */
        @Override
        public boolean validateObject(String key, PooledObject<Unmarshaller> p) {
            if (null == p.getObject()) {
                return false;
            }
            return true;
        }

        /**
         * 销毁
         *
         * @param key
         * @param p
         * @throws Exception
         */
        @Override
        public void destroyObject(String key, PooledObject<Unmarshaller> p) throws Exception {
            /** 杀死他 */
            super.destroyObject(key, p);
        }

        @Override
        public void activateObject(String key, PooledObject<Unmarshaller> p) throws Exception {
            super.activateObject(key, p);
        }

        @Override
        public void passivateObject(String key, PooledObject<Unmarshaller> p) throws Exception {
            super.passivateObject(key, p);
        }
    }
}