package org.example.memorialbooklet.pedigree.mybatis.type;

import lombok.Data;

@Data
public class Relationship {
    private Long id;
    private Long fromPersonId;  // 对应数据库 from_person_id
    private Long toPersonId;    // 对应数据库 to_person_id
    private Integer generationGap; // 对应数据库 generation_gap
}