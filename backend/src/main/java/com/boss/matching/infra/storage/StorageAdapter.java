package com.boss.matching.infra.storage;

public interface StorageAdapter {
    String save(String path, byte[] content);
}
