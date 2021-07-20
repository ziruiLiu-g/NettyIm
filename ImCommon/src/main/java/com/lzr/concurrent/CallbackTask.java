/**
 * Created by 尼恩 at 疯狂创客圈
 */

package com.lzr.concurrent;

/**
 * future task callback interface
 *
 * Author: zirui liu
 * Date: 2021/7/19
 */
public interface CallbackTask<R>
{

    R execute() throws Exception;

    void onBack(R r);

    void onException(Throwable t);
}
