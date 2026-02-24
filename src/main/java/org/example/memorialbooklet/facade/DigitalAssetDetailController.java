package org.example.memorialbooklet.facade;

import org.example.memorialbooklet.assetdetail.DigitalAssetDetailService;
import org.example.memorialbooklet.pedigree.mybatis.type.DigitalAssetDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 数字资产详情接口控制器
 * 暴露数字资产详情的增删改查接口，供前端调用
 */
@RestController
@RequestMapping("/api/v1/digital-asset")
@CrossOrigin(origins = "*") // 允许前端跨域调试，与参考FamilyController保持一致
public class DigitalAssetDetailController {

    // 注入Service层，调用业务逻辑
    @Autowired
    private DigitalAssetDetailService digitalAssetDetailService;

    /**
     * 1. 新增数字资产详情
     * POST /api/v1/digital-asset/detail
     */
    @PostMapping("/detail")
    public DigitalAssetDetailDto createDigitalAssetDetail(
            @RequestParam Long fileId,
            @RequestParam String description) {
        return digitalAssetDetailService.createDigitalAssetDetail(fileId, description);
    }

    /**
     * 2. 更新数字资产详情描述
     * PUT /api/v1/digital-asset/detail
     */
    @PutMapping("/detail")
    public DigitalAssetDetailDto updateDigitalAssetDetail(
            @RequestParam Long fileId,
            @RequestParam String description) {
        return digitalAssetDetailService.updateDigitalAssetDetail(fileId, description);
    }

    /**
     * 3. 根据fileId查询数字资产详情
     * GET /api/v1/digital-asset/detail
     */
    @GetMapping("/detail")
    public DigitalAssetDetailDto getDigitalAssetDetail(@RequestParam Long fileId) {
        return digitalAssetDetailService.getDigitalAssetDetailByFileId(fileId);
    }

    /**
     * 4. 根据fileId删除数字资产详情
     * DELETE /api/v1/digital-asset/detail
     */
    @DeleteMapping("/detail")
    public String deleteDigitalAssetDetail(@RequestParam Long fileId) {
        return digitalAssetDetailService.deleteDigitalAssetDetail(fileId);
    }
}
