package org.example.memorialbooklet.assetdetail;


// 实现类（与风格A一致，仅添加 implements 接口）
import org.example.memorialbooklet.mapper.DigitalAssetDetailMapper;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalAssetDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DigitalAssetDetailServiceImpl implements DigitalAssetDetailService {

    @Autowired
    private DigitalAssetDetailMapper digitalAssetDetailMapper;

    @Override
    public DigitalAssetDetailDto createDigitalAssetDetail(Long fileId, String description) {
        DigitalAssetDetailDto detailDto = new DigitalAssetDetailDto();
        detailDto.setFileId(fileId);
        detailDto.setDescription(description);
        digitalAssetDetailMapper.insertDigitalAssetDetail(detailDto);
        return digitalAssetDetailMapper.findByFileId(fileId);
    }

    @Override
    public DigitalAssetDetailDto updateDigitalAssetDetail(Long fileId, String description) {
        digitalAssetDetailMapper.updateDigitalAssetDetail(fileId, description);
        return digitalAssetDetailMapper.findByFileId(fileId);
    }

    @Override
    public DigitalAssetDetailDto getDigitalAssetDetailByFileId(Long fileId) {
        DigitalAssetDetailDto detail = digitalAssetDetailMapper.findByFileId(fileId);
        if (detail == null) {
            // 如果没有找到详情，返回一个空的或者默认的对象，而不是 null
            // 这样前端就不会收到空响应，而是收到一个空描述的对象
            detail = new DigitalAssetDetailDto();
            detail.setFileId(fileId);
            detail.setDescription(""); // 或者 null，取决于前端如何处理
        }
        return detail;
    }

    @Override
    public String deleteDigitalAssetDetail(Long fileId) {
        digitalAssetDetailMapper.deleteByFileId(fileId);
        return "success";
    }
}
