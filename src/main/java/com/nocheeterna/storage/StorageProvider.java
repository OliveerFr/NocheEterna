package com.nocheeterna.storage;

import java.util.Map;
import java.util.UUID;

public interface StorageProvider {
    void load(Map<UUID, Double> target);
    void save(Map<UUID, Double> source);
    String getName();
}
