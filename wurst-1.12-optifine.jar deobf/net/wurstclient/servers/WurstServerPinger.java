// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.servers;

import java.net.UnknownHostException;
import net.minecraft.client.network.ServerPinger;
import org.apache.logging.log4j.LogManager;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.atomic.AtomicInteger;

public class WurstServerPinger
{
    private static final AtomicInteger threadNumber;
    public static final Logger logger;
    public ServerData server;
    private boolean done;
    private boolean failed;
    
    static {
        threadNumber = new AtomicInteger(0);
        logger = LogManager.getLogger();
    }
    
    public WurstServerPinger() {
        this.done = false;
        this.failed = false;
    }
    
    public void ping(final String ip) {
        this.ping(ip, 25565);
    }
    
    public void ping(final String ip, final int port) {
        this.server = new ServerData("", String.valueOf(ip) + ":" + port, false);
        new Thread("Wurst Server Connector #" + WurstServerPinger.threadNumber.incrementAndGet()) {
            @Override
            public void run() {
                final ServerPinger pinger = new ServerPinger();
                try {
                    WurstServerPinger.logger.info("Pinging " + ip + ":" + port + "...");
                    pinger.ping(WurstServerPinger.this.server);
                    WurstServerPinger.logger.info("Ping successful: " + ip + ":" + port);
                }
                catch (final UnknownHostException e) {
                    WurstServerPinger.logger.info("Unknown host: " + ip + ":" + port);
                    WurstServerPinger.access$0(WurstServerPinger.this, true);
                }
                catch (final Exception e2) {
                    WurstServerPinger.logger.info("Ping failed: " + ip + ":" + port);
                    WurstServerPinger.access$0(WurstServerPinger.this, true);
                }
                pinger.clearPendingNetworks();
                WurstServerPinger.access$1(WurstServerPinger.this, true);
            }
        }.start();
    }
    
    public boolean isStillPinging() {
        return !this.done;
    }
    
    public boolean isWorking() {
        return !this.failed;
    }
    
    public boolean isOtherVersion() {
        return this.server.version != 47;
    }
    
    static /* synthetic */ void access$0(final WurstServerPinger wurstServerPinger, final boolean failed) {
        wurstServerPinger.failed = failed;
    }
    
    static /* synthetic */ void access$1(final WurstServerPinger wurstServerPinger, final boolean done) {
        wurstServerPinger.done = done;
    }
}
