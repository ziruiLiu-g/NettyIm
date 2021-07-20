package com.lzr.concurrent;

import com.lzr.util.ThreadUtil;
import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * CallbackTaskScheduler
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
@Slf4j
public class CallbackTaskScheduler
{

    static ListeningExecutorService gPool = null;

    static {
        ExecutorService jPool = ThreadUtil.getMixedTargetThreadPool();
        gPool = MoreExecutors.listeningDecorator(jPool);
    }


    private CallbackTaskScheduler() {
    }

    /**
     * 添加任务
     * @param executeTask
     */


    public static <R> void add(CallbackTask<R> executeTask) {


        ListenableFuture<R> future = gPool.submit(new Callable<R>() {
            public R call() throws Exception {

                R r = executeTask.execute();
                return r;
            }

        });

        Futures.addCallback(future, new FutureCallback<R>() {
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });


    }


}
