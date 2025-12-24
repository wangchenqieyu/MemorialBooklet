package org.example.memorialbooklet.mapper;


import org.apache.ibatis.annotations.*;
import org.example.memorialbooklet.pedigree.mybatis.type.Relationship;

import java.util.List;

@Mapper
public interface RelationshipMapper {

    /**
     * 1. 插入新关系
     */
    @Insert("INSERT INTO t_relationship(from_person_id, to_person_id, generation_gap) " +
            "VALUES(#{fromPersonId}, #{toPersonId}, #{generationGap})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Relationship relationship);

    /**
     * 2. 查询所有关系 (用于重建内存图)
     */
    @Select("SELECT * FROM t_relationship")
    List<Relationship> selectAll();

    /**
     * 3. (可选) 查询某人的所有关系
     */
    @Select("SELECT * FROM t_relationship WHERE from_person_id = #{personId} OR to_person_id = #{personId}")
    List<Relationship> selectByPersonId(Long personId);

    @Delete("DELETE FROM t_relationship WHERE from_person_id = #{fromId} AND to_person_id = #{toId}")
    void delete(@Param("fromId") Long fromId, @Param("toId") Long toId);
}
