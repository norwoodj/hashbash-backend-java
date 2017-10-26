package com.johnmalcolmnorwood.hashbash.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

public class EntityResponseUtils {
    public static <T> ResponseEntity<T> getResponseForGetEntity(T object) {
        if (Objects.isNull(object)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(object);
    }

    public static ResponseEntity<Void> getResponseForDeleteEntity() {
        URI rainbowTableUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .build()
                .toUri();

        return ResponseEntity.noContent()
                .location(rainbowTableUri)
                .build();
    }

    public static ResponseEntity<Void> getResponseForCreatedEntity(Object objectId) {
        URI rainbowTableUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(objectId)
                .toUri();

        return ResponseEntity.created(rainbowTableUri).build();
    }

}
