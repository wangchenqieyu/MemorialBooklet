package org.example.memorialbooklet.response;

import lombok.Data;

@Data
public class FileIpfsDetailResponse {
    private Long id;
    private Long personId;
    private String ipfsCode;
    private String confluxCode;
}
