package com.smartecarte.dcb.test;

import org.junit.Assert;
import org.junit.Test;

import com.smartecarte.dcb.DcbCommand;
import com.smartecarte.dcb.DcbPacket;

public class DcbPacketTest {

	@Test
	public void xorCheckTest() {
		Assert.assertArrayEquals(new byte[] { 0x02, 0x14, 0x16 },  new DcbPacket(DcbCommand.COMMAND_GET_IP_MAC_ADDRESS).getMessagePacket());
	}

}
