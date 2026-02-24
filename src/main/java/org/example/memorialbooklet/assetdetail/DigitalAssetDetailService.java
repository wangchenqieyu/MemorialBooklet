package org.example.memorialbooklet.assetdetail;

import org.example.memorialbooklet.pedigree.mybatis.type.DigitalAssetDetailDto;

public interface DigitalAssetDetailService {
    DigitalAssetDetailDto createDigitalAssetDetail(Long fileId, String description);
    DigitalAssetDetailDto updateDigitalAssetDetail(Long fileId, String description);
    DigitalAssetDetailDto getDigitalAssetDetailByFileId(Long fileId);
    String deleteDigitalAssetDetail(Long fileId);
}
