package com.smartecarte.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Multiplexer implements Transmitter {
	protected final static Logger logger = LoggerFactory.getLogger(Multiplexer.class);
	
	static private long TIMEOUT	= 1000;
	
	private class Transmit {
		private final SocketAddress destination;
		private final byte[] data;
	
		public Transmit(SocketAddress destination, byte[] data) {
			this.destination = destination;
			this.data = data;
		}

		public SocketAddress getDestination() {
			return destination;
		}

		public byte[] getData() {
			return data;
		}

		@Override
		public String toString() {
			return "Transmit [destination=" + destination + ", data=" + Arrays.toString(data) + "]";
		}
	}
	
    private Queue<Transmit> queue = new ConcurrentLinkedQueue<Transmit>();
    private Vector<ReceiveEventHandler> handlers = new Vector<ReceiveEventHandler>();

	private boolean loop = true;

    public synchronized void addEventHandler(ReceiveEventHandler handler) {
        if (handler == null)
            throw new NullPointerException();
        if (!handlers.contains(handler)) {
        	handlers.addElement(handler);
        }
    }

    public synchronized void removeEventHandler(ReceiveEventHandler handler) {
    	handlers.removeElement(handler);
    }

    public void notifyHandlers(SocketAddress source, byte[] data) {
        /*
         * a temporary array buffer, used as a snapshot of the state of
         * current handlers.
         */
    	ReceiveEventHandler[] _handlers;

        synchronized (this) {
            /* We don't want the handler doing callbacks into
             * arbitrary code while holding its own Monitor.
             * The code where we extract each handler from
             * the Vector and store the state of the handler
             * needs synchronization, but notifying handlers
             * does not (should not).  The worst result of any
             * potential race-condition here is that:
             * 1) a newly-added handler will miss a
             *   notification in progress
             * 2) a recently unregistered handler will be
             *   wrongly notified when it doesn't care
             */
        	_handlers = (ReceiveEventHandler[]) handlers.toArray(new ReceiveEventHandler[handlers.size()]);
        }

        for (ReceiveEventHandler handler : _handlers) {
        	handler.handle(source, data);
        }
    }

    private void read(SelectionKey key) throws IOException {
		logger.debug("read - key: {}", key);
		
		DatagramChannel channel = (DatagramChannel) key.channel();
		logger.debug("read - channel: {}", channel);
		
		ByteBuffer buffer = ByteBuffer.allocate(1024);
	
		SocketAddress source = channel.receive(buffer);
		logger.debug("read - source: {}", source);
		
		byte[] data = Arrays.copyOf(buffer.array(), buffer.position());
		logger.debug("read - data: {}", data);

		notifyHandlers(source, data);
	}

	private void write(SelectionKey key) throws IOException {
		logger.debug("write - key: {}", key);
		
        if (!queue.isEmpty()) {
        	Transmit transmit = queue.remove();
    		logger.debug("write - transmit: {}", transmit);
    		
    		SocketAddress destination = transmit.getDestination();
        	byte[] data = transmit.getData();
	        
			ByteBuffer buffer = ByteBuffer.allocate(data.length);
			buffer.clear();
			buffer.put(data);
			buffer.flip();
	        
	        DatagramChannel channel = (DatagramChannel) key.channel();
	        int bytesSent = channel.send(buffer, destination);
	        logger.debug("write - bytesSent: {}", bytesSent);
        }
	}

    private void multiplex(String hostname, int port) throws IOException {
	    logger.debug("multiplex - hostname: {}  port: {}", hostname, port);

	    DatagramChannel channel = DatagramChannel.open();
	    logger.debug("multiplex - channel: {}", channel);
	    
		channel.bind(new InetSocketAddress(hostname, port));		
		channel.configureBlocking(false);
		
		Selector selector = Selector.open();
	    logger.debug("multiplex - selector: {}", selector);
	    
	    channel.register(selector, SelectionKey.OP_READ);
		
	    while(loop) {
	    	try {
				int numberOfKeys = selector.select(TIMEOUT);
			    logger.debug("multiplex - numberOfKeys: {}", numberOfKeys);

			    if (numberOfKeys != 0) {
				    Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
			    	
					while (selectedKeys.hasNext()) {
						SelectionKey key = (SelectionKey) selectedKeys.next();
					    logger.debug("multiplex - key: {}", key);
						
						selectedKeys.remove();
	
						if (key.isReadable()) {
							read(key);
						} else if (key.isWritable()) {
							write(key);
	                        key.interestOps(SelectionKey.OP_READ);
						}
					}
				}
			    
		        if (!queue.isEmpty()) {
    	        	channel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		        }

			} catch (IOException exception) {
			    logger.error("multiplex - exception: {}", exception.getLocalizedMessage());
			}
	    }
	}
	
	public void start(String hostname, int port) throws IOException {
		loop = true;
		
		multiplex(hostname, port);
	}
	
	public void stop() {
		loop = false;
	}

	@Override
	public void transmit(SocketAddress destination, byte[] data) {
	    logger.debug("transmit - destination: {}  data: {}", destination, data);
		queue.add(new Transmit(destination, data));
	}
}
