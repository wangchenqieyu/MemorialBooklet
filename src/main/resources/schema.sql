CREATE TABLE IF NOT EXISTS t_person (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    level INT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. 创建关系表
CREATE TABLE IF NOT EXISTS t_relationship (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    from_person_id BIGINT NOT NULL,
    to_person_id BIGINT NOT NULL,
    generation_gap INT NOT NULL,
    INDEX idx_from (from_person_id),
    INDEX idx_to (to_person_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `digital_legacy_assets` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `person_id` BIGINT NOT NULL COMMENT '用户的唯一身份识别ID',
    `ipfs_code` VARCHAR(255)  COMMENT '内容在IPFS上的内容哈希(CID)',
    `conflux_code` VARCHAR(255)  COMMENT '在Conflux链上的交易哈希或合约存证指纹',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_person_id` (`person_id`) -- 为person_id建立索引，方便后续按人检索
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS t_login (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    person_id BIGINT NOT NULL,
    password VARCHAR(32) NOT NULL,
    UNIQUE KEY (person_id),
    CONSTRAINT fk_person FOREIGN KEY (person_id) REFERENCES t_person(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE t_person
    ADD UNIQUE INDEX uk_name (name);

ALTER TABLE t_relationship
    ADD UNIQUE KEY uk_relation (from_person_id, to_person_id);

DELETE t1
FROM t_relationship t1
         JOIN t_relationship t2
              ON t1.from_person_id = t2.from_person_id
                  AND t1.to_person_id = t2.to_person_id
WHERE t1.id > t2.id;

ALTER TABLE t_person MODIFY gender CHAR(1) DEFAULT '1' comment '1:男, 0:女';

CREATE TABLE `digital_asset_details` (
                                         `file_id` BIGINT NOT NULL COMMENT '文件ID，关联主表digital_legacy_assets的id',
                                         `description` TEXT COMMENT '文件的详细描述信息',
                                         `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '详情信息的创建/录入时间',

    -- 1. 设为主键：物理上保证了 file_id 绝对不可重复，且非空
                                         PRIMARY KEY (`file_id`),

    -- 2. 设置外键：确保这个 file_id 必须在主表里存在，才能往这里插数据
                                         CONSTRAINT `fk_asset_detail_file_id`
                                             FOREIGN KEY (`file_id`)
                                                 REFERENCES `digital_legacy_assets` (`id`)
                                                 ON DELETE CASCADE -- (可选) 如果主表文件删了，详情自动删除，防止产生垃圾数据
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='数字资产详情表';

