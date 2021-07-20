package com.lzr.server.distributed;

import com.lzr.constants.ServerConstants;
import com.lzr.zk.CuratorZKclient;
import lombok.Data;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.RetryNTimes;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * counter for online num
 *
 * Author: zirui liu
 * Date: 2021/7/20
 */
@Data
public class OnlineCounter {
    private static final String PATH = ServerConstants.COUNTER_PATH;

    private CuratorFramework client = null;

    private static OnlineCounter singletonInstance = null;

    DistributedAtomicLong distributedAtomicLong = null;

    private Long curValue;

    public static OnlineCounter getInstance() {
        if (null == singletonInstance) {
            singletonInstance = new OnlineCounter();
            singletonInstance.client = CuratorZKclient.instance.getClient();
            singletonInstance.init();
        }
        return singletonInstance;
    }

    private void init() {
        distributedAtomicLong = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 30));
    }

    private OnlineCounter() { }

    /**
     * incr
     *
     * @return true / false
     */
    public boolean increment() {
        boolean result = false;
        AtomicValue<Long> val = null;
        try {
            val = distributedAtomicLong.increment();
            result = val.succeeded();
            System.out.println("old cnt: " + val.preValue()
                    + "  new cnt: " + val.postValue()
                    + "  result: " + val.succeeded());
            curValue = val.postValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * derr
     *
     * @return true / false
     */
    public boolean decrement() {
        boolean result = false;
        AtomicValue<Long> val = null;
        try {
            val = distributedAtomicLong.decrement();
            result = val.succeeded();
            System.out.println("old cnt: " + val.preValue()
                    + "  new cnt: " + val.postValue()
                    + "  result: " + val.succeeded());
            curValue = val.postValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
