package org.example.memorialbooklet.pedigree.type;

import lombok.Data;
// 如果没装 Lombok 插件，请手动生成 Getter/Setter/ToString

@Data
public class Person {
    private Long id;
    private String name;
    private Integer level; // 存储计算后的代数 (0, 1, 2...)
}
