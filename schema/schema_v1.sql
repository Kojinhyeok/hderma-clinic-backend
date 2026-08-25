-- =====================================================
-- H-Derma Global Clinical Center — 전체 스키마 v1
-- =====================================================

-- -----------------------------------------------------
-- 0. 공용 파일 테이블
-- -----------------------------------------------------
CREATE TABLE files (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) COMMENT '연결된 테이블명',
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

-- -----------------------------------------------------
-- 1. 회원
-- -----------------------------------------------------
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE COMMENT '아이디',
    password VARCHAR(255) COMMENT '비밀번호 (BCrypt 해시)',
    name VARCHAR(100) COMMENT '이름',
    email VARCHAR(255) UNIQUE COMMENT '이메일',
    phone VARCHAR(20) COMMENT '휴대전화번호',
    role VARCHAR(20) DEFAULT 'MEMBER' COMMENT 'MEMBER/PROFESSOR/ADMIN',
    privacy_agreed_at DATETIME COMMENT '개인정보 수집 동의 일시',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT 'ACTIVE/WITHDRAWN',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 2. 임상평가 (평가분야)
-- -----------------------------------------------------
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

CREATE TABLE eval_functional (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    test_period VARCHAR(100),
    eval_items VARCHAR(255),
    subject_count VARCHAR(100),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_health_food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    test_period VARCHAR(100),
    eval_items VARCHAR(255),
    subject_count VARCHAR(100),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_usability (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    test_period VARCHAR(100),
    eval_items VARCHAR(255),
    subject_count VARCHAR(100),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_safety (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    test_period VARCHAR(100),
    subject_count VARCHAR(100),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE eval_invitro (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100),
    test_period VARCHAR(100),
    eval_items VARCHAR(255),
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 3. 인증마크
-- -----------------------------------------------------
CREATE TABLE certification_mark (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) COMMENT '대분류명',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE certification_mark_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    certification_mark_id BIGINT COMMENT 'certification_mark.id',
    title VARCHAR(255) COMMENT '세부 항목명',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_cert_category_mark ON certification_mark_category(certification_mark_id);

-- -----------------------------------------------------
-- 4. 시험의뢰
-- -----------------------------------------------------
CREATE TABLE trial_request (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    company_name VARCHAR(255),
    business_reg_no VARCHAR(50),
    manager_name VARCHAR(100),
    manager_title VARCHAR(100),
    contact VARCHAR(50),
    email VARCHAR(255),
    product_type VARCHAR(255),
    desired_start_date DATE,
    desired_report_date DATE,
    consult_type VARCHAR(20) COMMENT 'PHONE 또는 EMAIL',
    content TEXT,
    password VARCHAR(255) COMMENT '비회원 조회용 (해시 저장)',
    privacy_agreed BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'RECEIVED' COMMENT 'RECEIVED/IN_PROGRESS/COMPLETED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trial_request_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trial_request_id BIGINT,
    eval_source VARCHAR(30) COMMENT 'EFFICACY_ITEM/FUNCTIONAL/HEALTH_FOOD/SAFETY/INVITRO',
    eval_source_id BIGINT,
    item_name_snapshot VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_trial_request_item_req ON trial_request_item(trial_request_id);

-- -----------------------------------------------------
-- 5. 시험참여신청 (모집공고 + 피험자 신청)
-- -----------------------------------------------------
CREATE TABLE recruitment_field (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) COMMENT '지역명',
    sort_order INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE recruitment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trial_code VARCHAR(50) COMMENT '예: 피부0001',
    trial_name VARCHAR(255) COMMENT '모집 공고 제목',
    eval_category VARCHAR(30) COMMENT 'EFFICACY/FUNCTIONAL/HEALTH_FOOD/SAFETY',
    status VARCHAR(20) DEFAULT 'OPEN' COMMENT 'OPEN/CLOSED/PERMANENTLY_OPEN',
    start_date DATE COMMENT '시험기간 시작',
    end_date DATE COMMENT '시험기간 종료',
    application_start_date DATE COMMENT '시험참여신청 시작',
    application_end_date DATE COMMENT '시험참여신청 종료',
    participation_group VARCHAR(255) COMMENT '모집대상',
    requirements TEXT COMMENT '선정기준',
    participation_number INT COMMENT '모집인원',
    participation_cost VARCHAR(100) COMMENT '시험참여비',
    recruitment_field_ids VARCHAR(255) COMMENT '지역 ID 목록 (쉼표 구분)',
    detail_content LONGTEXT COMMENT '상세 안내 (CKEditor)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE recruitment_time_table (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruitment_id BIGINT,
    visit_date DATE,
    start_time VARCHAR(20),
    end_time VARCHAR(20),
    capacity INT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE trial_application (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    recruitment_id BIGINT,
    member_id BIGINT COMMENT 'member.id — 시험참여신청은 회원 필수',
    applicant_name VARCHAR(100),
    applicant_contact VARCHAR(50),
    applicant_birth DATE,
    status VARCHAR(20) DEFAULT 'APPLIED' COMMENT 'APPLIED/SELECTED/REJECTED/CANCELLED',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_trial_app_recruitment ON trial_application(recruitment_id);
CREATE INDEX idx_trial_app_member ON trial_application(member_id);

-- -----------------------------------------------------
-- 6. 뉴스레터 / 공지사항
-- -----------------------------------------------------
CREATE TABLE newsletter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content LONGTEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE notice (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    is_pinned TINYINT(1) DEFAULT 0,
    content LONGTEXT,
    view_count INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- -----------------------------------------------------
-- 7. 팝업
-- -----------------------------------------------------
CREATE TABLE popup (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) COMMENT '관리자 식별용 제목',
    link_url VARCHAR(500),
    start_date DATETIME,
    end_date DATETIME,
    width INT DEFAULT 400,
    pos_x INT DEFAULT 50,
    pos_y INT DEFAULT 50,
    popup_order INT DEFAULT 0,
    is_active TINYINT(1) DEFAULT 1,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_popup_active_dates ON popup(is_active, start_date, end_date);