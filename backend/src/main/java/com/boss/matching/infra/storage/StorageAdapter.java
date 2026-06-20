package com.boss.matching.infra.storage;

/**
 * Defines the storage abstraction for saving uploaded or generated binary content.
 */
public interface StorageAdapter {
    String save(String path, byte[] content);
}
