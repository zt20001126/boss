package com.boss.matching.infra.storage;

import com.boss.matching.config.AppProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Local filesystem storage adapter for development deployments.
 */
@Component
public class LocalStorageAdapter implements StorageAdapter {
    private final Path root;

    /**
     * Creates a LocalStorageAdapter instance.
     * @param properties input value
     */
    public LocalStorageAdapter(AppProperties properties) {
        this.root = Path.of(properties.getStorage().getLocalRoot());
    }

    /** {@inheritDoc} */
    @Override
    public String save(String path, byte[] content) {
        try {
            Path target = root.resolve(path).normalize();
            Files.createDirectories(target.getParent());
            Files.write(target, content);
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save local file", e);
        }
    }
}
