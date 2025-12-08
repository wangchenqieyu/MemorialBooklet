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