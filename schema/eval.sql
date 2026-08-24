-- 화장품 효능평가 (그룹형)
CREATE TABLE eval_efficacy_group (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) COMMENT '제품군명 (예: 스킨케어제품)',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_efficacy_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    group_id BIGINT COMMENT 'eval_efficacy_group.id',
    name VARCHAR(100) COMMENT '세부 항목명 (예: 주름)',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_eval_efficacy_item_group ON eval_efficacy_item(group_id);

-- 구조화형 4개 탭 공통 형태 (테이블은 각각 별도)
CREATE TABLE eval_functional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) COMMENT '항목명',
    test_period VARCHAR(100) COMMENT '시험기간',
    eval_items VARCHAR(255) COMMENT '평가항목',
    subject_count VARCHAR(100) COMMENT '시험대상자 수',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_health_food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) COMMENT '항목명',
    test_period VARCHAR(100) COMMENT '시험기간',
    eval_items VARCHAR(255) COMMENT '평가항목',
    subject_count VARCHAR(100) COMMENT '시험대상자 수',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_usability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) COMMENT '항목명',
    test_period VARCHAR(100) COMMENT '시험기간',
    eval_items VARCHAR(255) COMMENT '평가항목',
    subject_count VARCHAR(100) COMMENT '시험대상자 수',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_safety (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) COMMENT '항목명',
    test_period VARCHAR(100) COMMENT '시험기간',
    subject_count VARCHAR(100) COMMENT '시험대상자 수',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_invitro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) COMMENT '항목명',
    test_period VARCHAR(100) COMMENT '시험기간',
    eval_items VARCHAR(255) COMMENT '평가항목',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) COMMENT '연결된 테이블명 (예: eval_functional)',
    entity_id BIGINT COMMENT '연결된 게시글 ID',
    file_category VARCHAR(50) COMMENT 'THUMBNAIL 등',
    s3_key VARCHAR(500),
    s3_bucket VARCHAR(100),
    original_filename VARCHAR(255),
    file_size BIGINT,
    mime_type VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_files_entity ON files(entity_type, entity_id);

CREATE TABLE trial_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255) COMMENT '회사명',
    business_reg_no VARCHAR(50) COMMENT '사업자등록번호',
    manager_name VARCHAR(100) COMMENT '담당자명',
    manager_title VARCHAR(100) COMMENT '담당자 직함',
    contact VARCHAR(50) COMMENT '연락처',
    email VARCHAR(255) COMMENT '이메일',
    product_type VARCHAR(255) COMMENT '제품유형',
    desired_start_date DATE COMMENT '희망시험시작일',
    desired_report_date DATE COMMENT '희망결과보고일',
    consult_type VARCHAR(20) COMMENT 'PHONE 또는 EMAIL',
    content TEXT COMMENT '기타 문의내용',
    password VARCHAR(255) COMMENT '비회원 조회용 비밀번호 (해시 저장)',
    privacy_agreed BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'RECEIVED' COMMENT 'RECEIVED/IN_PROGRESS/COMPLETED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE trial_request_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trial_request_id BIGINT COMMENT 'trial_request.id',
    eval_source VARCHAR(30) COMMENT '출처 테이블: EFFICACY_ITEM/FUNCTIONAL/HEALTH_FOOD/SAFETY/INVITRO',
    eval_source_id BIGINT COMMENT '해당 eval_* 테이블의 PK',
    item_name_snapshot VARCHAR(100) COMMENT '선택 당시 항목명 (원본이 나중에 수정/삭제돼도 신청 기록은 보존)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE recruitment ADD COLUMN eval_category VARCHAR(30) COMMENT '연결된 평가분야: EFFICACY/FUNCTIONAL/HEALTH_FOOD/SAFETY';
ALTER TABLE recruitment ADD COLUMN application_start_date DATE COMMENT '시험참여신청 시작일';
ALTER TABLE recruitment ADD COLUMN application_end_date DATE COMMENT '시험참여신청 종료일';

CREATE TABLE newsletter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) COMMENT '제목',
    content LONGTEXT COMMENT '본문',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) COMMENT '제목',
    is_pinned TINYINT(1) DEFAULT 0 COMMENT '상단 고정 여부',
    content LONGTEXT COMMENT '본문',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;