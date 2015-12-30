package com.nerdery.smartecarte.dcb;

import java.util.Arrays;

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
public class DcbCommandPacket {
	private final byte length;
	private final DcbCommand command;
	private final byte[] data;
	private final byte check;
	private final byte[] buffer;
	
	public DcbCommandPacket(DcbCommand command, byte[] data) {
		this.length = (byte) (1 + data.length + 1);
		this.command = command;
		this.data = data;
		this.check = xor(length, command.getCode(), data);
		this.buffer = new byte[length + 1];
		
		buffer[0] = length;
		buffer[1] = command.getCode();
		System.arraycopy(data, 0, buffer, 2, data.length);
		buffer[buffer.length - 1] = check;
	}

	public DcbCommandPacket(DcbCommand DcbCommand) {
		this(DcbCommand, new byte[0]);
	}

	public byte getLength() {
		return length;
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

	private byte xor(byte length, byte code, byte[] data) {
		byte check = (byte) (length ^ code);
		
		for (byte b : data) {
			check ^= b;
		}
		
		return check;
	}

	@Override
	public String toString() {
		return "DcbCommandPacket [length=" + length + ", command=" + command + ", data=" + data
				+ ", check=" + check + ", buffer=" + buffer  + "]";
	}

	/**
	 *
	 * @param buffer
	 * 		The byte array to parse
	 * @return
	 */
	public static DcbCommandPacket parse(byte[] buffer) {
		int length = buffer[0];
		DcbCommand command = DcbCommand.valueOf(buffer[1]);
		byte[] data = Arrays.copyOfRange(buffer, 2, length);
		
		return new DcbCommandPacket(command, data);
	}
}
