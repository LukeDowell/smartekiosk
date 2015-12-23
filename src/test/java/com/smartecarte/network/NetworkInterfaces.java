package com.smartecarte.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetworkInterfaces {

	public static void main(String[] args) throws SocketException {
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
		while(networkInterfaces.hasMoreElements())
		{
		    NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
	        System.out.println(networkInterface.toString());

	        Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
		    while (addresses.hasMoreElements())
		    {
		        InetAddress address = (InetAddress) addresses.nextElement();
		        System.out.println("\t" + address.getHostAddress());
		    }
		}
	}
}
