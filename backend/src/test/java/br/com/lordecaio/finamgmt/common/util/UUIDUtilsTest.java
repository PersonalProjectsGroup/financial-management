package br.com.lordecaio.finamgmt.common.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UUIDUtilsTest {

    @Test
    void constructor_shouldThrowException() throws NoSuchMethodException {
        Constructor<UUIDUtils> constructor = UUIDUtils.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        
        InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
		assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals("Utility class", exception.getCause().getMessage());
    }

    @Test
    void nextV7_shouldReturnNonNullUUID() {
        UUID uuid = UUIDUtils.nextV7();
        assertNotNull(uuid);
    }

    @Test
    void nextV7_shouldReturnVersion7UUID() {
        UUID uuid = UUIDUtils.nextV7();
        assertEquals(7, uuid.version(), "UUID version should be 7");
    }

    @Test
    void nextV7_shouldReturnVariant2UUID() {
        UUID uuid = UUIDUtils.nextV7();
        assertEquals(2, uuid.variant(), "UUID variant should be 2 (RFC 4122)");
    }

    @Test
    void nextV7_shouldGenerateUniqueUUIDs() {
        int count = 10000;
        Set<UUID> uuids = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            uuids.add(UUIDUtils.nextV7());
        }

        assertEquals(count, uuids.size(), "All generated UUIDs should be unique");
    }

    @Test
    void nextV7_shouldGenerateMonotonicUUIDs() {
        UUID first = UUIDUtils.nextV7();
        UUID second = UUIDUtils.nextV7();
        assertTrue(first.compareTo(second) < 0, "UUIDs generated in sequence should be monotonic");
    }

    @Test
    void nextV7_shouldHandleSequenceOverflowInSameMillisecond() {
        int count = 20000; 
        UUID[] uuids = new UUID[count];

        for (int i = 0; i < count; i++) {
            uuids[i] = UUIDUtils.nextV7();
        }

        for (int i = 1; i < count; i++) {
            assertTrue(uuids[i-1].compareTo(uuids[i]) < 0, 
                String.format("UUID at index %d should be greater than previous at index %d", i, i-1));
        }
    }
}
