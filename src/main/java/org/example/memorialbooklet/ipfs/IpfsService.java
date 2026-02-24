package org.example.memorialbooklet.ipfs;


import jnr.ffi.annotations.In;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalLegacyAsset;
import org.example.memorialbooklet.response.FileIpfsDetailResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * IPFS服务接口：定义文件上传能力
 */
public interface IpfsService {
    String uploadFile(InputStream inputStream, Long personId) throws IOException;

    InputStream downloadFile(String cid) throws IOException;

    List<FileIpfsDetailResponse> findByPersonId(long personId) throws IOException;
}
