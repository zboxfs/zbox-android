package io.zbox.fs;

public enum FileType {
    FILE(0),
    DIR(1);

    private final int id;

    FileType(int id) { this.id = id;  }
    public int getValue() { return id;  }
}

