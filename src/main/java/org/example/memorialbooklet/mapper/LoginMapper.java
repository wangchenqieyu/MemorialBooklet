package org.example.memorialbooklet.mapper;
import org.apache.ibatis.annotations.*;
import org.example.memorialbooklet.pedigree.mybatis.type.Login;

@Mapper
public interface LoginMapper {

    /**
     * 插入登录凭证
     * personId 是从 Person 对象中获取的 ID
     */
    @Insert("INSERT INTO t_login(person_id, password) VALUES(#{personId}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Login login);

    /**
     * 根据 personId 查询密码 (用于登录验证)
     */
    @Select("SELECT password FROM t_login WHERE person_id = #{personId}")
    String selectPasswordByPersonId(Long personId);

    /**
     * 更新密码
     */
    @Update("UPDATE t_login SET password = #{password} WHERE person_id = #{personId}")
    void updatePassword(@Param("personId") Long personId, @Param("password") String password);

    /**
     * 删除登录信息 (如果删除了成员，手动清理或依靠数据库级联)
     */
    @Delete("DELETE FROM t_login WHERE person_id = #{personId}")
    void deleteByPersonId(Long personId);
}