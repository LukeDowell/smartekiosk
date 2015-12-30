package com.nerdery.smartecarte.dcb;

import java.util.HashMap;
import java.util.Map;

public enum DcbCommand {
	COMMAND_GET_ALL_DEVICE_STATE(Code.COMMAND_GET_ALL_DEVICE_STATE, false),
	COMMAND_SET_DEVICE_STATE(Code.COMMAND_SET_DEVICE_STATE, false),
	
	COMMAND_GET_IP_MAC_ADDRESS(Code.COMMAND_CODE_GET_IP_MAC_ADDRESS, true),
	COMMAND_SET_IP_MAC_ADDRESS(Code.COMMAND_SET_IP_MAC_ADDRESS, true),
	
	COMMAND_GET_DCB_PARAMETERS(Code.COMMAND_GET_DCB_PARAMETERS, false),
	COMMAND_GET_DCB_COLUMN_NUMBER(Code.COMMAND_GET_DCB_COLUMN_NUMBER, false),
	COMMAND_GET_DCB_INFO(Code.COMMAND_GET_DCB_INFO, false);
	
	static private class Code {
		static public final Byte COMMAND_GET_ALL_DEVICE_STATE	= 0x01;
		static public final Byte COMMAND_SET_DEVICE_STATE	= 0x02;

		/*
		 * send 14H (broadcast)
		 * return 14H IP1 IP2 IP3 IP4 MAC1 MAC2 MAC3 MAC4 MAC5 MAC6 CHK------success 
		 * return 0x14,0FF------Failed 
		 */
		static public final Byte COMMAND_CODE_GET_IP_MAC_ADDRESS = (byte) 0x14;
		
		/*
		 * send 14H
		 * return 0x06, 0x00------success 
		 * return 0x06, 0x81------CHK failed
		 */
		static public final Byte COMMAND_SET_IP_MAC_ADDRESS = (byte) 0x06;
		
		/*
		 * send 0BH
		 * return 0x0B, start number, cabinet type, box quantity, box configuration ------success
		 * return 0x0B, 0x81 ------CHK failed
		 */
		static public final Byte COMMAND_GET_DCB_PARAMETERS = 0x0B;

		/*
		 * send 0CH
		 * return 0x0C, manufacturer, production date, batch number, part number, hardware version, firmware version ------success
		 * return 0x0C, 0x81 ------CHK failed
		 */
		static public final Byte COMMAND_GET_DCB_INFO = 0x0C;

		/*
		 * send 0CH
		 * return 0x18, column number ------success 
		 * return 0x18, 0xFF ------column failed
		 * return 0x18, 0x81 ------CHK failed
		 */
		static public final Byte COMMAND_GET_DCB_COLUMN_NUMBER = 0x18;
		
	}
	
	static private Map<Byte, DcbCommand> map = new HashMap<Byte, DcbCommand>();
	
	static {
		map.put(Code.COMMAND_GET_ALL_DEVICE_STATE, COMMAND_GET_ALL_DEVICE_STATE);
		map.put(Code.COMMAND_SET_DEVICE_STATE, COMMAND_SET_DEVICE_STATE);
		map.put(Code.COMMAND_CODE_GET_IP_MAC_ADDRESS, COMMAND_GET_IP_MAC_ADDRESS);
		map.put(Code.COMMAND_GET_DCB_PARAMETERS, COMMAND_GET_DCB_PARAMETERS);
		map.put(Code.COMMAND_GET_DCB_COLUMN_NUMBER, COMMAND_GET_DCB_COLUMN_NUMBER);
		map.put(Code.COMMAND_GET_DCB_INFO, COMMAND_GET_DCB_INFO);
	}
	
	private final byte code;
	private final boolean broadcast;

	DcbCommand(byte code, boolean broadcast) {
		this.code = code;
		this.broadcast = broadcast;
	}

	public byte getCode() {
		return code;
	}
	
	public boolean isBroadcast() {
		return broadcast;
	}
	
	static public DcbCommand valueOf(Byte code) {
		return map.get(code);
	}
}
