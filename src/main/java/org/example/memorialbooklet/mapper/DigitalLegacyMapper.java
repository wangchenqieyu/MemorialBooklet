package org.example.memorialbooklet.mapper;

import org.apache.ibatis.annotations.*;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalLegacyAsset;

import java.util.List;

@Mapper
public interface DigitalLegacyMapper {

    /**
     * 1. 插入新记录 (初始阶段只有 person_id 和 ipfs_code)
     * useGeneratedKeys = true: 告诉 MyBatis 使用数据库自增 ID
     * keyProperty = "id": 插入成功后，把生成的 ID 填回对象中
     */
    @Insert("INSERT INTO digital_legacy_assets(person_id, ipfs_code) " +
            "VALUES(#{personId}, #{ipfsCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertIpfsCode(DigitalLegacyAsset asset);

    /**
     * 1. 插入新记录 (初始阶段只有 person_id 和 ipfs_code)
     * useGeneratedKeys = true: 告诉 MyBatis 使用数据库自增 ID
     * keyProperty = "id": 插入成功后，把生成的 ID 填回对象中
     */
    @Insert("INSERT INTO digital_legacy_assets(person_id, conflux_code) " +
            "VALUES(#{personId}, #{confluxCode})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertConfluxCode(DigitalLegacyAsset asset);

    /**
     * 2. 更新 Conflux 存证哈希 (用于后续上链成功后的回填)
     */
    @Update("UPDATE digital_legacy_assets SET conflux_code = #{confluxCode} WHERE id = #{id}")
    int updateConfluxCode(@Param("id") Long id, @Param("confluxCode") String confluxCode);

    /**
     * 3. 根据 ID 查询详情
     */
    @Select("SELECT * FROM digital_legacy_assets WHERE id = #{id}")
    @Results({
            @Result(property = "personId", column = "person_id"),
            @Result(property = "ipfsCode", column = "ipfs_code"),
            @Result(property = "confluxCode", column = "conflux_code")
    })
    DigitalLegacyAsset findById(Long id);

    /**
     * 根据人员ID查询资产列表
     */
    @Select("SELECT id, person_id AS personId, ipfs_code AS ipfsCode, conflux_code AS confluxCode " +
            "FROM digital_legacy_assets WHERE person_id = #{personId}")
    List<DigitalLegacyAsset> selectByPersonId(Long personId);
}
