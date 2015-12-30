package com.nerdery.smartecarte.network;


import com.nerdery.smartecarte.dcb.DcbCommandPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by ldowell on 12/23/15.
 */
public class Multiplexer extends Observable implements Transmitter {

    /** Logger */
    private static final Logger logger = LoggerFactory.getLogger(Multiplexer.class);

    /** How long the selector should wait before moving on */
    public static final long TIMEOUT = 1000;

    /** A queue of all outgoing messages we wish to send */
    private Queue<Transmit> outgoingQueue = new ConcurrentLinkedQueue<>();

    /**
     * @param key
     *      The selection key that contains the channel we are gonna write to
     */
    private void read(SelectionKey key) throws IOException {

        // Pull out the channel we are going to read from
        DatagramChannel channel = (DatagramChannel) key.channel();

        // Create a recepticle to store the received data in
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Dump the data into the buffer and store the sender
        SocketAddress source = channel.receive(buffer);

        // Pull the data out of the buffer
        byte[] data = Arrays.copyOf(buffer.array(), buffer.position());

        logger.debug("read - message received: {} content: {}", source, data);

        // Notify everyone who cares with the packaged information
        setChanged();
        notifyObservers(new Transmit(source, data));
    }

    /**
     * @param key
     *      The selection key that contains the channel we are gonna write to
     * @throws IOException
     */
    private void write(SelectionKey key) throws IOException {

        // Double check for s and g's
        if(!outgoingQueue.isEmpty()) {

            // Pull out the next transmit
            Transmit transmit = outgoingQueue.remove();

            // Pull out the contents
            SocketAddress destination = transmit.getDestination();
            byte[] data = transmit.getData();

            // Grab the channel
            DatagramChannel channel = (DatagramChannel) key.channel();

            logger.debug("write - sending message to {}", destination);

            // Send it off
            channel.send(ByteBuffer.wrap(data), destination);
        }
    }

    /**
     * @param hostname
     *      The hostname of this (the receiving) machine
     * @param port
     *      The port to listen/send on
     * @throws IOException
     */
    public void multiplex(String hostname, int port) throws IOException {

        // Get and open a datagram channel
        DatagramChannel channel = DatagramChannel.open();

        channel.socket().setBroadcast(true);

        // Bind the channel to the provided host and port
        channel.bind(new InetSocketAddress(hostname, port));

        // We don't want to wait on the channel, we want to wait on the selector
        channel.configureBlocking(false);

        // Create the selector
        Selector selector = Selector.open();

        // Register the selector with the channel
        channel.register(selector, SelectionKey.OP_READ);

        // For     evv     errrrrrrr
        while(!Thread.currentThread().isInterrupted()) {
            try {

                // Grab all the keybs we can do shit with
                int numKeys = selector.select(TIMEOUT);

                // If we have keys...
                if(numKeys > 0) {

                    // Grab the iterator containing all the selected keys
                    Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();

                    // While there are keys left in the iterator..
                    while(selectedKeys.hasNext()) {

                        // Pull out the key
                        SelectionKey key = selectedKeys.next();

                        // And remove it from the iterator
                        selectedKeys.remove();

                        // Read on the key if possible
                        if(key.isReadable()) {
                            read(key);
                        }

                        // Write on the key if possible
                        if(key.isWritable()) {
                            write(key);
                            // Set the selector to read only again
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                }

                // After we have checked the keys, check to see if we have any messages queued
                if(!outgoingQueue.isEmpty()) {
                    channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                }

            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void transmit(SocketAddress dest, byte[] data) {
        outgoingQueue.add(new Transmit(dest, data));
    }

    /**
     * http://stackoverflow.com/questions/11165852/java-singleton-and-synchronization
     */
    private static volatile Multiplexer instance;
    private static final Object lock = new Object();

    public static Multiplexer getInstance() {
        Multiplexer result = instance;
        if(result == null) {
            synchronized (lock) {
                result = instance;
                if(result == null) {
                    result = new Multiplexer();
                    instance = result;
                }
            }
        }
        return result;
    }

    private Multiplexer() {}

}
