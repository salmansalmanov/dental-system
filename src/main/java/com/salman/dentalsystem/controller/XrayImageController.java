package com.salman.dentalsystem.controller;

import com.salman.dentalsystem.model.dto.response.XrayImageResponse;
import com.salman.dentalsystem.model.enums.XrayAgent;
import com.salman.dentalsystem.result.DataResult;
import com.salman.dentalsystem.service.abstraction.XrayImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/xray")
public class XrayImageController {
    private final XrayImageService xrayImageService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<DataResult<XrayImageResponse>> upload(
            @RequestHeader("X-Agent-Id") XrayAgent agentId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(xrayImageService.uploadXrayImage(agentId, file));
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable UUID id) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(xrayImageService.getImageBytes(id));
    }
}
