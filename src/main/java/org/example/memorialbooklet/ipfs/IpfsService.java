package org.example.memorialbooklet.ipfs;


import jnr.ffi.annotations.In;

import java.io.IOException;
import java.io.InputStream;

/**
 * IPFS服务接口：定义文件上传能力
 */
public interface IpfsService {
    String uploadFile(InputStream inputStream, Long personId) throws IOException;

    InputStream downloadFile(String cid) throws IOException;
}
