package com.nerdery.smartecarte.model;

import com.nerdery.smartecarte.Poller;
import com.nerdery.smartecarte.dcb.DcbCommand;
import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import com.nerdery.smartecarte.dcb.DcbDevice;
import com.nerdery.smartecarte.network.Multiplexer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ldowell on 12/28/15.
 */
public class Context {

    private static final Logger logger = LoggerFactory.getLogger(Context.class);

    private static Context instance;

    private Multiplexer multiplexer;

    private List<DcbDevice> devices;

    public static synchronized Context getInstance() {
        if(instance == null) {
            instance = new Context();
        }
        return instance;
    }

    private Context() {

        // Initialize the dcb list
        devices = new LinkedList<>();

        // Create our multiplexer
        multiplexer = new Multiplexer();

        // Create the thread pool
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        // Fire up the server
        executorService.execute(() -> {
            try {
                multiplexer.multiplex("localhost", 30001);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // Fire off the polling service
        executorService.execute(new Poller(multiplexer));
    }

    public Multiplexer getMultiplexer() {
        return multiplexer;
    }
}
