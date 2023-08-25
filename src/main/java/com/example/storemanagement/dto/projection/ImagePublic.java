package com.example.storemanagement.dto.projection;

import com.example.storemanagement.enity.Image;
import com.example.storemanagement.enity.Product;
import lombok.RequiredArgsConstructor;

public interface ImagePublic {
    Integer getId();

    @RequiredArgsConstructor
    class ImagePublicImpl implements ImagePublic {
        private final Image image;

        @Override
        public Integer getId() {
            return this.image.getId();
        }
    }

    static ImagePublic of(Image image) {
        return new ImagePublicImpl(image);
    }
}
