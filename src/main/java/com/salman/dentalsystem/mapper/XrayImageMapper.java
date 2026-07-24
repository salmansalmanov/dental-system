package com.salman.dentalsystem.mapper;

import com.salman.dentalsystem.model.dto.response.XrayImageResponse;
import com.salman.dentalsystem.model.entity.XrayImage;
import com.salman.dentalsystem.storage.model.StoredFile;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface XrayImageMapper {
    XrayImage toEntity(StoredFile storedFile);

    XrayImageResponse toResponse(XrayImage xrayImage);
}
