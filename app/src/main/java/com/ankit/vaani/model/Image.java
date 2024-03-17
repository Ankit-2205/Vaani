package com.ankit.vaani.model;

import android.net.Uri;

public class Image {

    private byte[] data;
    private String description;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
