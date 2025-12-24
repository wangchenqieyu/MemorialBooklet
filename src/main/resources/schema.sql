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

