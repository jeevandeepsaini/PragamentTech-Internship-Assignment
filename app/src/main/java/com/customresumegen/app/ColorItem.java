package com.customresumegen.app;
public class ColorItem {
    private String name;
    private String hexCode;

    public ColorItem(String name, String hexCode) {
        this.name = name;
        this.hexCode = hexCode;
    }

    public String getName() {
        return name;
    }

    public String getHexCode() {
        return hexCode;
    }
}