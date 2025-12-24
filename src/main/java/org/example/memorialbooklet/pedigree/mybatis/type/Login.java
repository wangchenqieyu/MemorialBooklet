package org.example.memorialbooklet.pedigree.mybatis.type;

import lombok.Data;

@Data
public class Login {
    private Long id;
    private Long personId;
    private String password;
}