package org.example.memorialbooklet.mapper;

import org.apache.ibatis.annotations.*;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalAssetDetailDto;

/**
 * 数字资产详情Mapper
 * 关联 digital_asset_details 表
 */
@Mapper
public interface DigitalAssetDetailMapper {

    /**
     * 插入数字资产详情记录
     * 关联主表digital_legacy_assets的id作为file_id，保证外键关联
     */
    @Insert("INSERT INTO digital_asset_details(file_id, description) " +
            "VALUES(#{fileId}, #{description})")
    int insertDigitalAssetDetail(DigitalAssetDetailDto detailDto);

    /**
     * 更新数字资产详情描述
     * 根据file_id（关联主表id）更新描述信息
     */
    @Update("UPDATE digital_asset_details SET description = #{description}, created_at = CURRENT_TIMESTAMP " +
            "WHERE file_id = #{fileId}")
    int updateDigitalAssetDetail(@Param("fileId") Long fileId, @Param("description") String description);

    /**
     * 根据file_id（关联主表id）查询数字资产详情
     */
    @Select("SELECT * FROM digital_asset_details WHERE file_id = #{fileId}")
    @Results({
            @Result(property = "fileId", column = "file_id"),
            @Result(property = "description", column = "description"),
            @Result(property = "createdAt", column = "created_at")
    })
    DigitalAssetDetailDto findByFileId(Long fileId);

    /**
     * 根据file_id（关联主表id）删除数字资产详情
     * 注：主表配置ON DELETE CASCADE时，主表删除会自动删此表数据，此方法用于主动删除场景
     */
    @Delete("DELETE FROM digital_asset_details WHERE file_id = #{fileId}")
    int deleteByFileId(Long fileId);
}
