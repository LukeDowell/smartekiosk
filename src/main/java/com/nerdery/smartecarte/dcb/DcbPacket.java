package com.nerdery.smartecarte.dcb;

import java.util.stream.IntStream;

/*
 * Communication Data format:
 * 
 * Length: 1 byte
 * Code: 1 byte
 * Data: n bytes
 * Check: 1 byte
 * 
 * check is an xor of Length + data1 + data2 + ... + dataN
 * 
 */
public class DcbPacket {
	private DcbCommand command;
	private byte[] data;
	
	public DcbPacket(DcbCommand DcbCommand, byte[] data) {
		this.command = DcbCommand;
		this.data = data;
	}

	public DcbPacket(DcbCommand DcbCommand) {
		this(DcbCommand, new byte[0]);
	}

	public DcbCommand getDcbCommand() {
		return command;
	}

	public void setDcbCommand(DcbCommand DcbCommand) {
		this.command = DcbCommand;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	private byte xor(byte length, byte code, byte[] data) {
		byte check = (byte) (length ^ code);
		
		for (byte b : data) {
			check ^= b;
		}
		
		return check;
	}
	
	public byte[] getMessagePacket() {
		byte[] packet = new byte[1 + 1 + data.length + 1]; // length + code + data + check
		
		byte length = (byte) (1 + data.length + 1);
		packet[0] = length;
		packet[1] = command.getCode();
		System.arraycopy(data, 0, packet, 2, data.length);
		packet[packet.length - 1] = xor(length, command.getCode(), data);
		
		return packet;
	}
	
	static public DcbPacket parse(byte[] packet) {
		byte length = packet[0];
		byte code = packet[1];
		byte[] data = new byte[length - 2];
		System.arraycopy(packet, 2, data, 0, length - 2);
		
		return new DcbPacket(DcbCommand.valueOf(code), data);
	}

	private String toString(byte[] bytes) {
		StringBuffer buffer = new StringBuffer("[");
		
		IntStream.range(0, bytes.length).forEach(i -> buffer.append(String.format("%d (0x%02x)%s", (int) (bytes[i] & 0xFF), bytes[i], i < (bytes.length - 1) ? ", " :"")));
		buffer.append("]");
		
		return buffer.toString();
	}
	
	@Override
	public String toString() {
		return "DcbPacket [DcbCommand=" + command + ", data=" + toString(data) + "]";
	}
}
