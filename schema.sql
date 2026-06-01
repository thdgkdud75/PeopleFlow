-- PeopleFlow HR System Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS hrdb DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hrdb;

-- 부서
CREATE TABLE IF NOT EXISTS department (
    dept_id   INT AUTO_INCREMENT PRIMARY KEY,
    dept_name VARCHAR(100) NOT NULL,
    manager_id INT DEFAULT NULL
);

-- 직급
CREATE TABLE IF NOT EXISTS `position` (
    pos_id   INT AUTO_INCREMENT PRIMARY KEY,
    pos_name VARCHAR(100) NOT NULL,
    level    INT DEFAULT 1
);

-- 직원
CREATE TABLE IF NOT EXISTS employee (
    emp_id    INT AUTO_INCREMENT PRIMARY KEY,
    emp_no    VARCHAR(20) UNIQUE NOT NULL,
    name      VARCHAR(100) NOT NULL,
    email     VARCHAR(150),
    phone     VARCHAR(20),
    dept_id   INT,
    pos_id    INT,
    hire_date DATE,
    status    VARCHAR(20) DEFAULT 'ACTIVE',
    role      VARCHAR(20) DEFAULT 'EMPLOYEE',
    password  VARCHAR(255) NOT NULL,
    FOREIGN KEY (dept_id) REFERENCES department(dept_id),
    FOREIGN KEY (pos_id)  REFERENCES `position`(pos_id)
);

-- 부서 매니저 FK (직원 생성 후)
ALTER TABLE department
    ADD CONSTRAINT fk_dept_manager
    FOREIGN KEY (manager_id) REFERENCES employee(emp_id)
    ON DELETE SET NULL;

-- 근태
CREATE TABLE IF NOT EXISTS attendance (
    att_id     INT AUTO_INCREMENT PRIMARY KEY,
    emp_id     INT NOT NULL,
    att_date   DATE NOT NULL,
    check_in   TIME,
    check_out  TIME,
    status     VARCHAR(20) DEFAULT 'NORMAL',
    UNIQUE KEY uq_att (emp_id, att_date),
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id)
);

-- 휴가 신청
CREATE TABLE IF NOT EXISTS leave_request (
    leave_id   INT AUTO_INCREMENT PRIMARY KEY,
    emp_id     INT NOT NULL,
    leave_type VARCHAR(30) NOT NULL,
    start_date DATE NOT NULL,
    end_date   DATE NOT NULL,
    leave_days INT DEFAULT 1,
    reason     TEXT,
    status     VARCHAR(20) DEFAULT 'PENDING',
    approved_by INT,
    applied_at DATETIME DEFAULT NOW(),
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id),
    FOREIGN KEY (approved_by) REFERENCES employee(emp_id)
);

-- 급여
CREATE TABLE IF NOT EXISTS salary (
    sal_id     INT AUTO_INCREMENT PRIMARY KEY,
    emp_id     INT NOT NULL,
    sal_month  VARCHAR(7) NOT NULL,
    base_pay   BIGINT DEFAULT 0,
    allowance  BIGINT DEFAULT 0,
    deduction  BIGINT DEFAULT 0,
    net_pay    BIGINT DEFAULT 0,
    pay_status VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id)
);

-- 인사평가
CREATE TABLE IF NOT EXISTS evaluation (
    eval_id      INT AUTO_INCREMENT PRIMARY KEY,
    emp_id       INT NOT NULL,
    eval_period  VARCHAR(20) NOT NULL,
    score        INT DEFAULT 0,
    grade        VARCHAR(5),
    comments     TEXT,
    work_summary TEXT,
    ai_report    TEXT,
    evaluator_id INT,
    eval_date    DATE,
    FOREIGN KEY (emp_id) REFERENCES employee(emp_id),
    FOREIGN KEY (evaluator_id) REFERENCES employee(emp_id)
);

-- ========== 샘플 데이터 ==========

INSERT INTO department (dept_name) VALUES ('개발팀'), ('인사팀'), ('영업팀'), ('재무팀');

INSERT INTO `position` (pos_name, level) VALUES
    ('사원', 1), ('주임', 2), ('대리', 3), ('과장', 4), ('차장', 5), ('부장', 6);

-- 관리자 계정 (비밀번호: admin1234)
INSERT INTO employee (emp_no, name, email, phone, dept_id, pos_id, hire_date, status, role, password)
VALUES ('EMP001', '관리자', 'admin@company.com', '010-0000-0000', 2, 6, '2020-01-01', 'ACTIVE', 'ADMIN', 'admin1234');

-- 일반 직원 샘플
INSERT INTO employee (emp_no, name, email, phone, dept_id, pos_id, hire_date, status, role, password)
VALUES
    ('EMP002', '김철수', 'kim@company.com',  '010-1111-2222', 1, 3, '2022-03-15', 'ACTIVE', 'EMPLOYEE', 'pass1234'),
    ('EMP003', '이영희', 'lee@company.com',  '010-3333-4444', 1, 2, '2023-06-01', 'ACTIVE', 'EMPLOYEE', 'pass1234'),
    ('EMP004', '박민준', 'park@company.com', '010-5555-6666', 3, 4, '2021-09-10', 'ACTIVE', 'EMPLOYEE', 'pass1234');
