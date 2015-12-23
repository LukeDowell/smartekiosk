package com.nerdery.smartecarte.dcb;

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
public class DcbResponsePacket {
	private final DcbCommand command;
	private final byte[] data;
	private final byte check;
	private final byte[] buffer;
	
	public DcbResponsePacket(DcbCommand command, byte[] data) {
		int length = 1 + data.length + 1;
		this.command = command;
		this.data = data;
		this.check = xor(command.getCode(), data);
		this.buffer = new byte[length];
		
		buffer[0] = command.getCode();
		System.arraycopy(data, 0, buffer, 1, data.length);
		buffer[buffer.length - 1] = check;
	}

	public DcbResponsePacket(DcbCommand DcbCommand) {
		this(DcbCommand, new byte[0]);
	}

	public DcbCommand getCommand() {
		return command;
	}

	public byte[] getData() {
		return data;
	}

	public byte getCheck() {
		return check;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	private byte xor(byte code, byte[] data) {
		byte check = code;
		
		for (byte b : data) {
			check ^= b;
		}
		
		return check;
	}

	@Override
	public String toString() {
		return "DcbCommandPacket [command=" + command + ", data=" + data
				+ ", check=" + check + ", buffer=" + buffer  + "]";
	}
}
