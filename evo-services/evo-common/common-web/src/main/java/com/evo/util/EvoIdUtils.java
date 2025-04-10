package com.evo.util;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EvoIdUtils {
    public static UUID nextId() {
        return UUID.randomUUID();
    }

    public static List<UUID> nextIds(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> UUID.randomUUID())
                .collect(Collectors.toList());
    }
}
