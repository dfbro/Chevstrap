package com.chevstrap.rbx.Enums.FlagPresets;

public enum RenderingMode {
    Automatic("Automatic"),
    OpenGL("OpenGL"),
    Vulkan("Vulkan");

    private final String displayName;

    RenderingMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
