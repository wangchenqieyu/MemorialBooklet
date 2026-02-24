package org.example.memorialbooklet.pedigree.mybatis.type;

import java.time.LocalDateTime;

/**
 * 数字资产详情DTO
 * 关联 digital_asset_details 表
 */
public class DigitalAssetDetailDto {
    // 文件ID，关联digital_legacy_assets的id
    private Long fileId;
    // 文件详细描述信息
    private String description;
    // 详情创建时间
    private LocalDateTime createdAt;

    // 全字段getter/setter
    public Long getFileId() {
        return fileId;
    }

    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
