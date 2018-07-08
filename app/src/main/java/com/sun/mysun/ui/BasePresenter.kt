package com.sun.mysun.ui

import android.support.annotation.CallSuper
import java.util.*


open class BasePresenter<V : PresentableView> {

    private val deferredActions = LinkedList<(V) -> Unit>()
    private var view: V? = null

    @CallSuper
    open fun bind(view: V) {
        this.view = view
        applyDeferredActionsToView(view)
    }

    @CallSuper
    open fun unbind() {
        this.view = null
    }

    protected fun applyToView(action: (V) -> Unit): Boolean {
        if (null == view) {
            return false
        }
        action.invoke(view as V)
        return true
    }

    protected fun postToView(action: (V) -> Unit) {
        if (null == view) {
            enqueueAction(action)
            return
        }
        action.invoke(view as V)
    }

    private fun enqueueAction(action: (V) -> Unit) {
        deferredActions.offer(action)
    }

    private fun applyDeferredActionsToView(view: V) {
        var action = deferredActions.poll()
        while (null != action) {
            action.invoke(view)
            action = deferredActions.poll()
        }
    }
}
