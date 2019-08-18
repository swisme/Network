package com.sw.simpleokhttp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionPool {
    private static final String TAG = "ConnectionPool";
    private long mKeepAliveTime;
    private Deque<HttpConnection> mConnectionPool = new ArrayDeque<>();
    private boolean mCleanupRunning;

    private static final Executor mExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactory() {
        private AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, new StringBuilder("Connection Pool Thread_").append(integer.getAndIncrement()).toString());
            thread.setDaemon(true);// refer OkHttp
            return thread;
        }
    });

    private Runnable mCleanupRunnable = new Runnable() {
        @Override
        public void run() {
            while (true) {
                long currentTime = System.currentTimeMillis();
                long waitTime = cleanup(currentTime);// get next check time
                if (waitTime == -1) {
                    return;//there's no connection in the connection pool
                }
                if (waitTime > 0) {
                    synchronized (ConnectionPool.this) {
                        try {
                            ConnectionPool.this.wait(waitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    };

    public ConnectionPool() {
        this(60L, TimeUnit.SECONDS);
    }

    public ConnectionPool(long keepAliveTime, TimeUnit timeUnit) {
        mKeepAliveTime = timeUnit.toMillis(keepAliveTime);
    }

    public void addConnection(HttpConnection httpConnection) {
        if (!mCleanupRunning) {
            mCleanupRunning = true;
            mExecutor.execute(mCleanupRunnable);
        }
        mConnectionPool.add(httpConnection);
    }

    /**
     * @param currentTime
     * @return next clean time
     */
    private long cleanup(long currentTime) {
        long longestIdleTime = -1;// the longest idle time
        synchronized (this) {
            Iterator<HttpConnection> iterator = mConnectionPool.iterator();
            while (iterator.hasNext()) {
                HttpConnection connection = iterator.next();
                long idleTime = currentTime - connection.mLastUseTime;
                if (idleTime > mKeepAliveTime) {
                    iterator.remove();
                    connection.close();
                    continue;
                }
                if (idleTime > longestIdleTime) {
                    longestIdleTime = idleTime;
                }
            }
            if (longestIdleTime >= 0) {
                return mKeepAliveTime - longestIdleTime;// next clean time
            }
            mCleanupRunning = false;
            return longestIdleTime;
        }
    }

    /**
     * @param host
     * @param port
     * @return reusable connection
     */
    public synchronized HttpConnection getHttpConnection(String host, int port) {
        Iterator<HttpConnection> iterator = mConnectionPool.iterator();
        while (iterator.hasNext()) {
            HttpConnection connection = iterator.next();
            if (connection.isSameAddress(host, port)) {
                iterator.remove();//the connection will be added later if Keep-Alive
                return connection;
            }
        }
        return null;
    }
}
