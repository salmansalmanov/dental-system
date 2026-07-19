package com.salman.dentalsystem.service.abstraction;

import com.salman.dentalsystem.model.dto.response.XrayImageResponse;
import com.salman.dentalsystem.model.enums.XrayAgent;
import com.salman.dentalsystem.result.DataResult;
import org.springframework.web.multipart.MultipartFile;

public interface XrayImageService {
    DataResult<XrayImageResponse> uploadXrayImage(XrayAgent agentId, MultipartFile file);
}
