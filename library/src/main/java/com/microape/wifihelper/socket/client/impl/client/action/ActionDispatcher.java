package com.microape.wifihelper.socket.client.impl.client.action;

import com.microape.wifihelper.socket.core.iocore.interfaces.IPulseSendable;
import com.microape.wifihelper.socket.core.iocore.interfaces.ISendable;
import com.microape.wifihelper.socket.core.iocore.interfaces.IStateSender;
import com.microape.wifihelper.socket.core.pojo.OriginalData;
import com.microape.wifihelper.socket.core.utils.SLog;
import com.microape.wifihelper.socket.client.sdk.client.ConnectionInfo;
import com.microape.wifihelper.socket.client.sdk.client.OkSocketOptions;
import com.microape.wifihelper.socket.client.sdk.client.action.ISocketActionListener;
import com.microape.wifihelper.socket.client.sdk.client.connection.IConnectionManager;
import com.microape.wifihelper.socket.interfaces.basic.AbsLoopThread;
import com.microape.wifihelper.socket.interfaces.common_interfacies.dispatcher.IRegister;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

import static com.microape.wifihelper.socket.core.iocore.interfaces.IOAction.ACTION_PULSE_REQUEST;
import static com.microape.wifihelper.socket.core.iocore.interfaces.IOAction.ACTION_READ_COMPLETE;
import static com.microape.wifihelper.socket.core.iocore.interfaces.IOAction.ACTION_WRITE_COMPLETE;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_CONNECTION_FAILED;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_CONNECTION_SUCCESS;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_DISCONNECTION;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_READ_THREAD_SHUTDOWN;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_READ_THREAD_START;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_WRITE_THREAD_SHUTDOWN;
import static com.microape.wifihelper.socket.client.sdk.client.action.IAction.ACTION_WRITE_THREAD_START;


/**
 * 状态机
 * Created by didi on 2018/4/19.
 */
public class ActionDispatcher implements IRegister<ISocketActionListener, IConnectionManager>, IStateSender {
    /**
     * 线程回调管理Handler
     */
    private static final DispatchThread HANDLE_THREAD = new DispatchThread();

    /**
     * 事件消费队列
     */
    private static final LinkedBlockingQueue<ActionBean> ACTION_QUEUE = new LinkedBlockingQueue();

    static {
        //启动分发线程
        HANDLE_THREAD.start();
    }

    /**
     * 行为回调集合
     */
    private volatile Vector<ISocketActionListener> mResponseHandlerList = new Vector<>();
    /**
     * 连接信息
     */
    private volatile ConnectionInfo mConnectionInfo;
    /**
     * 连接管理器
     */
    private volatile IConnectionManager mManager;


    public ActionDispatcher(ConnectionInfo info, IConnectionManager manager) {
        mManager = manager;
        mConnectionInfo = info;
    }

    @Override
    public IConnectionManager registerReceiver(final ISocketActionListener socketResponseHandler) {
        if (socketResponseHandler != null) {
            synchronized (mResponseHandlerList) {
                if (!mResponseHandlerList.contains(socketResponseHandler)) {
                    mResponseHandlerList.add(socketResponseHandler);
                }
            }
        }
        return mManager;
    }

    @Override
    public IConnectionManager unRegisterReceiver(ISocketActionListener socketResponseHandler) {
        mResponseHandlerList.remove(socketResponseHandler);
        return mManager;
    }

    /**
     * 分发收到的响应
     *
     * @param action
     * @param arg
     * @param responseHandler
     */
    private void dispatchActionToListener(String action, Serializable arg, ISocketActionListener responseHandler) {
        switch (action) {
            case ACTION_CONNECTION_SUCCESS: {
                try {
                    responseHandler.onSocketConnectionSuccess(mConnectionInfo, action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_CONNECTION_FAILED: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketConnectionFailed(mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_DISCONNECTION: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketDisconnection(mConnectionInfo, action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_COMPLETE: {
                try {
                    OriginalData data = (OriginalData) arg;
                    responseHandler.onSocketReadResponse(mConnectionInfo, action, data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_READ_THREAD_START:
            case ACTION_WRITE_THREAD_START: {
                try {
                    responseHandler.onSocketIOThreadStart(action);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_COMPLETE: {
                try {
                    ISendable sendable = (ISendable) arg;
                    responseHandler.onSocketWriteResponse(mConnectionInfo, action, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_WRITE_THREAD_SHUTDOWN:
            case ACTION_READ_THREAD_SHUTDOWN: {
                try {
                    Exception exception = (Exception) arg;
                    responseHandler.onSocketIOThreadShutdown(action, exception);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case ACTION_PULSE_REQUEST: {
                try {
                    IPulseSendable sendable = (IPulseSendable) arg;
                    responseHandler.onPulseSend(mConnectionInfo, sendable);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void sendBroadcast(String action, Serializable serializable) {
        OkSocketOptions option = mManager.getOption();
        if (option == null) {
            return;
        }
        OkSocketOptions.ThreadModeToken token = option.getCallbackThreadModeToken();
        if (token != null) {
            ActionBean bean = new ActionBean(action, serializable, this);
            ActionRunnable runnable = new ActionRunnable(bean);
            try {
                token.handleCallbackEvent(runnable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (option.isCallbackInIndependentThread()) {//独立线程进行回调
            ActionBean bean = new ActionBean(action, serializable, this);
            ACTION_QUEUE.offer(bean);
        } else if (!option.isCallbackInIndependentThread()) {//IO线程里进行回调
            synchronized (mResponseHandlerList) {
                List<ISocketActionListener> copyData = new ArrayList<>(mResponseHandlerList);
                Iterator<ISocketActionListener> it = copyData.iterator();
                while (it.hasNext()) {
                    ISocketActionListener listener = it.next();
                    this.dispatchActionToListener(action, serializable, listener);
                }
            }
        } else {
            SLog.e("ActionDispatcher error action:" + action + " is not dispatch");
        }
    }

    @Override
    public void sendBroadcast(String action) {
        sendBroadcast(action, null);
    }

    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        mConnectionInfo = connectionInfo;
    }

    /**
     * 分发线程
     */
    private static class DispatchThread extends AbsLoopThread {
        public DispatchThread() {
            super("client_action_dispatch_thread");
        }

        @Override
        protected void runInLoopThread() throws Exception {
            ActionBean actionBean = ACTION_QUEUE.take();
            if (actionBean != null && actionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = actionBean.mDispatcher;
                synchronized (actionDispatcher.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList<>(actionDispatcher.mResponseHandlerList);
                    Iterator<ISocketActionListener> it = copyData.iterator();
                    while (it.hasNext()) {
                        ISocketActionListener listener = it.next();
                        actionDispatcher.dispatchActionToListener(actionBean.mAction, actionBean.arg, listener);
                    }
                }
            }
        }

        @Override
        protected void loopFinish(Exception e) {

        }
    }

    /**
     * 行为封装
     */
    protected static class ActionBean {
        public ActionBean(String action, Serializable arg, ActionDispatcher dispatcher) {
            mAction = action;
            this.arg = arg;
            mDispatcher = dispatcher;
        }

        String mAction = "";
        Serializable arg;
        ActionDispatcher mDispatcher;
    }

    /**
     * 行为分发抽象
     */
    public static class ActionRunnable implements Runnable {
        private ActionDispatcher.ActionBean mActionBean;

        ActionRunnable(ActionBean actionBean) {
            mActionBean = actionBean;
        }

        @Override
        public void run() {
            if (mActionBean != null && mActionBean.mDispatcher != null) {
                ActionDispatcher actionDispatcher = mActionBean.mDispatcher;
                synchronized (actionDispatcher.mResponseHandlerList) {
                    List<ISocketActionListener> copyData = new ArrayList<>(actionDispatcher.mResponseHandlerList);
                    Iterator<ISocketActionListener> it = copyData.iterator();
                    while (it.hasNext()) {
                        ISocketActionListener listener = it.next();
                        actionDispatcher.dispatchActionToListener(mActionBean.mAction, mActionBean.arg, listener);
                    }
                }
            }
        }
    }

}
