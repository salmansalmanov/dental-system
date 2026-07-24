package com.salman.dentalsystem.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class XrayImageResponse {
    private UUID id;
    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private String imageUrl;
    private String thumbnailUrl;
}
