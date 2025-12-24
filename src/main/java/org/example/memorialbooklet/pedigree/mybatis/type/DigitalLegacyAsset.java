package org.example.memorialbooklet.pedigree.mybatis.type;

import lombok.Data;

@Data
public class DigitalLegacyAsset {
    private Long id;
    private Long personId;
    private String ipfsCode;
    private String confluxCode;
}
