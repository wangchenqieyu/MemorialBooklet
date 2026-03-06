package org.example.memorialbooklet.facade;

import org.example.memorialbooklet.blockchain.BlockchainService;
import org.example.memorialbooklet.blockchain.BlockchainServiceFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.memorialbooklet.request.CidCodeRequest;
import org.example.memorialbooklet.response.TransactionResponse;

@RestController
@RequestMapping("/api/v1/chain")
@CrossOrigin(origins = "*") // 允许前端跨域调试
public class IpfsCidCodeChainFacade {

    private final BlockchainServiceFactory serviceFactory;

    private static final String DEFAULT_CHAIN = "CONFLUX";


    public IpfsCidCodeChainFacade(BlockchainServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @PostMapping("/store-cid")
    public ResponseEntity<TransactionResponse> storeCidOnChain(@RequestBody CidCodeRequest request) {
        if (request == null || request.getCidCode() == null || request.getCidCode().isEmpty()) {
            return new ResponseEntity<>(
                    new TransactionResponse(null, "CID code cannot be empty.", DEFAULT_CHAIN),
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            // 1. 通过工厂获取具体的区块链服务实例 (例如 Conflux)
            BlockchainService blockchainService = serviceFactory.getBlockchainService(DEFAULT_CHAIN);

            // 2. 调用核心业务逻辑：将 CID 存储到 Conflux 合约
            String txHash = blockchainService.storeCid(request.getCidCode(), request.getPersonId());

            // 3. 返回成功响应
            return new ResponseEntity<>(
                    new TransactionResponse(txHash, "CID successfully stored on chain.", DEFAULT_CHAIN),
                    HttpStatus.OK
            );

        } catch (IllegalArgumentException e) {
            // 捕获工厂抛出的不支持的链类型错误
            return new ResponseEntity<>(
                    new TransactionResponse(null, e.getMessage(), DEFAULT_CHAIN),
                    HttpStatus.BAD_REQUEST
            );
        }
        catch (RuntimeException e) {
            // 捕获服务层抛出的异常 (如 RPC 连接失败, Gas 不足)
            return new ResponseEntity<>(
                    new TransactionResponse(null, "Transaction failed: " + e.getMessage(), DEFAULT_CHAIN),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }



}
