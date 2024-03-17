package com.ankit.vaani.repository;

import com.ankit.vaani.model.Image;

import java.util.List;

public interface ImageRepo {

    void addImage(Image image);
    List<Image> getAllImages();

}
