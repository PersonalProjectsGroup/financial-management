package br.com.lordecaio.finamgmt.common.util;

import java.security.SecureRandom;
import java.util.UUID;

public final class UUIDUtils {

	private static final SecureRandom RANDOM = new SecureRandom();
	private static long lastTimestamp = -1L;
	private static long sequence = 0L;
	private static final long MAX_SEQUENCE = 0xFFFL; // 12 bits

	private UUIDUtils() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static synchronized UUID nextV7() {
		long now = System.currentTimeMillis();

		if (now <= lastTimestamp) {
			now = lastTimestamp;
			sequence++;
			if (sequence > MAX_SEQUENCE) {
				now++;
				lastTimestamp = now;
				sequence = 0;
			}
		} else {
			lastTimestamp = now;
			sequence = RANDOM.nextLong() & 0x3FFL;
		}

		long ts48 = now & 0xFFFFFFFFFFFFL;
		long high = (ts48 << 16) | 0x7000L | (sequence & MAX_SEQUENCE);

		long low = 0x8000000000000000L | (RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL);

		return new UUID(high, low);
	}
}