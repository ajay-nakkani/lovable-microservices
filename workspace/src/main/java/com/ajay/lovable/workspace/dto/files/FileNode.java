package com.ajay.lovable.workspace.dto.files;

import org.jetbrains.annotations.NotNull;

public record FileNode(String path) {

    @NotNull
    @Override
    public String toString() {
        return path;
    }
}
