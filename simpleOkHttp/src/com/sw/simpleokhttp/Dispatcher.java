package com.sw.simpleokhttp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Dispatcher {
    private int mMaxRequests;
    private int mMaxRequestsPerHost;
    private ExecutorService mExecutorService;
    private final Deque<Call.AsyncCall> mReadyAsyncCalls = new ArrayDeque<>();
    private final Deque<Call.AsyncCall> mRunningAsyncCalls = new ArrayDeque<>();

    public Dispatcher() {
        this(64, 5);
    }

    public Dispatcher(int maxRequests, int maxRequestsPerHost) {
        mMaxRequests = maxRequests;
        mMaxRequestsPerHost = maxRequestsPerHost;
    }

    public synchronized ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>(), new ThreadFactory() {
                private AtomicInteger integer = new AtomicInteger();

                @Override
                public Thread newThread(Runnable runnable) {
                    return new Thread(runnable, new StringBuilder("Http Client Thread_").append(integer.getAndIncrement()).toString());
                }
            });
        }
        return mExecutorService;
    }

    public void enqueue(Call.AsyncCall asyncCall) {
        if (mRunningAsyncCalls.size() < mMaxRequests && getRunningCallsPerHost(asyncCall) < mMaxRequestsPerHost) {
            mRunningAsyncCalls.add(asyncCall);
            getExecutorService().execute(asyncCall);
        } else {
            mReadyAsyncCalls.add(asyncCall);
        }
    }

    private int getRunningCallsPerHost(Call.AsyncCall asyncCall) {
        int count = 0;
        for (Call.AsyncCall runningAsyncCall : mRunningAsyncCalls) {
            if (runningAsyncCall.getHost().equalsIgnoreCase(asyncCall.getHost())) {
                count++;
            }
        }
        return count;
    }

    public void finished(Call.AsyncCall asyncCall) {
        synchronized (this) {
            mRunningAsyncCalls.remove(asyncCall);
            checkReadyCalls();
        }
    }

    private void checkReadyCalls() {
        if (mRunningAsyncCalls.size() >= mMaxRequests) {
            return;
        }
        if (mReadyAsyncCalls.isEmpty()) {
            return;
        }
        Iterator<Call.AsyncCall> iterator = mReadyAsyncCalls.iterator();
        while (iterator.hasNext()) {
            Call.AsyncCall asyncCall = iterator.next();
            if (getRunningCallsPerHost(asyncCall) < mMaxRequestsPerHost) {
                iterator.remove();
                mRunningAsyncCalls.add(asyncCall);
                mExecutorService.execute(asyncCall);
            }
            if (mRunningAsyncCalls.size() >= mMaxRequests) {
                return;
            }
        }
    }


}
