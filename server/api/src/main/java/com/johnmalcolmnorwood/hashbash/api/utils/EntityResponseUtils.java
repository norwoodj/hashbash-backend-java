package com.johnmalcolmnorwood.hashbash.api.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Objects;
import java.util.Optional;

public class EntityResponseUtils {
    public static <T> ResponseEntity<T> getResponseForGetEntity(Optional<T> object) {
        return object.map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
