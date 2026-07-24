package com.salman.dentalsystem.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "xray_images")
public class XrayImage extends BaseEntity {
    private String storedFileName;
    private String thumbnailFileName;
    private String originalFileName;
    private String contentType;
    private long fileSize;
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    private Appointment appointment;
}
