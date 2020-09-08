package com.qw.photo.work

import com.qw.photo.agent.IContainer
import com.qw.photo.callback.GetImageCallBack
import com.qw.photo.functions.DisposeBuilder
import com.qw.photo.pojo.DisposeResult

/**
 *Author: 思忆
 *Date: Created in 2020/9/8 3:29 PM
 */
class DisposeWorker(handler: IContainer) :
    BaseWorker<DisposeBuilder, DisposeResult>(handler) {
    override fun start(callBack: GetImageCallBack<DisposeResult>) {
    }
}