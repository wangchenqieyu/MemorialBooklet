package org.example.memorialbooklet.mapper;

import org.apache.ibatis.annotations.*;
import org.example.memorialbooklet.pedigree.type.Person;

import java.util.List;

@Mapper
public interface PersonMapper {

    /**
     * 1. 插入新成员
     * useGeneratedKeys = true: 告诉 MyBatis 使用数据库自增 ID
     * keyProperty = "id": 插入成功后，把生成的 ID 填回 Person 对象的 id 字段中
     */
    @Insert("INSERT INTO t_person(name, level) VALUES(#{name}, #{level})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Person person);

    /**
     * 2. 根据 ID 查询
     */
    @Select("SELECT * FROM t_person WHERE id = #{id}")
    Person selectById(Long id);

    /**
     * 3. 查询所有人 (用于系统启动时加载内存图)
     */
    @Select("SELECT * FROM t_person")
    List<Person> selectAll();

    /**
     * 4. 更新层级 (核心方法)
     * 当内存中的算法算出新的代数后，调用此方法回写数据库
     */
    @Update("UPDATE t_person SET level = #{level} WHERE id = #{id}")
    void updateLevel(@Param("id") Long id, @Param("level") Integer level);
}