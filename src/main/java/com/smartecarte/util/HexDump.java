package com.smartecarte.util;

import java.util.Random;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HexDump {
	private static final int WIDTH = 16;

	private static boolean isAsciiPrintable(char ch) {
		return ch > 31 && ch < 127;
	}

	private static String dump(byte[] bytes, int index, int width) {
		StringBuffer hexidecimal = new StringBuffer();
		StringBuffer character = new StringBuffer();

		hexidecimal.append(String.format("%03x ", index));

		for (int i = 0; i < width; i++) {
			int offset = i + index;

			if (offset < bytes.length) {
				hexidecimal.append(String.format("%02x ", bytes[offset]));

				char c = (char) (bytes[i + index]);
				character.append(String.format("%c", isAsciiPrintable(c) ? c : '.'));
			} else {
				hexidecimal.append(String.format("   "));
				character.append(' ');
			}
		}

		return hexidecimal.append(' ').append(character).toString();
	}

	public static void dump(Logger logger, byte[] bytes, int width) {
		for (int i = 0; i < bytes.length; i += width) {
			logger.debug(dump(bytes, i, width));
		}
	}

	public static void dump(Logger logger, byte[] bytes) {
		dump(logger, bytes, WIDTH);
	}
	
	public static void main(String[] args) {
		Logger logger = LoggerFactory.getLogger(HexDump.class);

		Random random = new Random();
		byte[] bytes = new byte[1024];
		
		IntStream.range(1, bytes.length).forEach(i -> bytes[i] = (byte) random.nextInt(256));
		dump(logger, bytes);
		
		byte[] _bytes = new byte[] { 't', 'e', 's', 't' };
		dump(logger, _bytes);
	}

}
