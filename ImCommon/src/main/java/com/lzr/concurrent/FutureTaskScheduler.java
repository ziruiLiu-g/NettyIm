package com.lzr.concurrent;

import com.lzr.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * FutureTaskScheduler
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Slf4j
public class FutureTaskScheduler
{
    static ThreadPoolExecutor mixPool = null;

    static {
        mixPool = ThreadUtil.getMixedTargetThreadPool();
    }

    private FutureTaskScheduler()
    {

    }

    /**
     * 添加任务
     *
     * @param executeTask
     */


    public static void add(Runnable executeTask) {
        mixPool.submit(()->{ executeTask.run(); });
    }

}
