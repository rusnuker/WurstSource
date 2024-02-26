// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public class MinPriorityThreadFactory implements ThreadFactory
{
    private static final AtomicInteger poolNumber;
    private final ThreadGroup group;
    private final AtomicInteger threadNumber;
    private final String namePrefix;
    
    static {
        poolNumber = new AtomicInteger(1);
    }
    
    public MinPriorityThreadFactory() {
        this.threadNumber = new AtomicInteger(1);
        final SecurityManager s = System.getSecurityManager();
        this.group = ((s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup());
        this.namePrefix = "pool-min-" + MinPriorityThreadFactory.poolNumber.getAndIncrement() + "-thread-";
    }
    
    @Override
    public Thread newThread(final Runnable r) {
        final Thread t = new Thread(this.group, r, String.valueOf(this.namePrefix) + this.threadNumber.getAndIncrement(), 0L);
        if (t.isDaemon()) {
            t.setDaemon(false);
        }
        if (t.getPriority() != 1) {
            t.setPriority(1);
        }
        return t;
    }
    
    public static ExecutorService newFixedThreadPool() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new MinPriorityThreadFactory());
    }
}
