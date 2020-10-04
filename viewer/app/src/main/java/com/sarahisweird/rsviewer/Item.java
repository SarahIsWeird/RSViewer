package com.sarahisweird.rsviewer;

public class Item {
    public final String name;
    public final String label;
    public final long size;
    public final int maxSize;
    public final int damage;

    public Item(String name, String label, long size, int maxSize, int damage) {
        this.name = name;
        this.label = label;
        this.size = size;
        this.maxSize = maxSize;
        this.damage = damage;
    }
}
