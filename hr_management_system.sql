-- 人力资源管理系统数据库脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS hr_management_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hr_management_system;

-- 1. 机构表
CREATE TABLE organizations (
    id INT PRIMARY KEY AUTO_INCREMENT,
    org_code VARCHAR(10) NOT NULL UNIQUE COMMENT '机构编码',
    org_name VARCHAR(100) NOT NULL COMMENT '机构名称',
    org_level INT NOT NULL COMMENT '机构级别：1-一级机构，2-二级机构，3-三级机构',
    parent_id INT COMMENT '父级机构ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (parent_id) REFERENCES organizations(id)
);

-- 2. 职位表
CREATE TABLE positions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    position_name VARCHAR(50) NOT NULL COMMENT '职位名称',
    org_id INT NOT NULL COMMENT '所属三级机构ID',
    description TEXT COMMENT '职位描述',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (org_id) REFERENCES organizations(id)
);

-- 3. 薪酬项目表
CREATE TABLE salary_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_name VARCHAR(50) NOT NULL COMMENT '薪酬项目名称',
    item_type ENUM('收入', '扣除') NOT NULL COMMENT '项目类型',
    calculation_rule VARCHAR(200) COMMENT '计算规则',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 4. 薪酬标准表
CREATE TABLE salary_standards (
    id INT PRIMARY KEY AUTO_INCREMENT,
    standard_number VARCHAR(20) UNIQUE NOT NULL COMMENT '薪酬标准编号',
    standard_name VARCHAR(100) NOT NULL COMMENT '薪酬标准名称',
    creator VARCHAR(50) NOT NULL COMMENT '制定人',
    registrar VARCHAR(50) NOT NULL COMMENT '登记人',
    register_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '登记时间',
    total_amount DECIMAL(10,2) COMMENT '薪酬总额',
    
    status ENUM('待复核', '已生效', '已停用') DEFAULT '待复核',
    reviewed_by VARCHAR(50) COMMENT '复核人',
    reviewed_at TIMESTAMP NULL,
    review_opinion TEXT COMMENT '复核意见',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 5. 薪酬标准明细表
CREATE TABLE salary_standard_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    standard_id INT NOT NULL,
    salary_item_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (standard_id) REFERENCES salary_standards(id),
    FOREIGN KEY (salary_item_id) REFERENCES salary_items(id)
);

-- 6. 员工档案表
CREATE TABLE employees (
    id INT PRIMARY KEY AUTO_INCREMENT,
    archive_number VARCHAR(20) UNIQUE NOT NULL COMMENT '档案编号',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    gender ENUM('男', '女') COMMENT '性别',
    email VARCHAR(100),
    phone VARCHAR(20),
    qq VARCHAR(20),
    mobile VARCHAR(20),
    address TEXT,
    postal_code VARCHAR(10),
    nationality VARCHAR(50),
    birth_place VARCHAR(100),
    birth_date DATE,
    ethnicity VARCHAR(50),
    religion VARCHAR(50),
    political_status VARCHAR(50),
    id_card VARCHAR(18) UNIQUE,
    age INT,
    education VARCHAR(50),
    salary_standard_id INT COMMENT '薪酬标准ID',
    hobbies TEXT,
    resume TEXT COMMENT '个人履历',
    family_info TEXT COMMENT '家庭关系信息',
    remarks TEXT COMMENT '备注',
    photo_path VARCHAR(200) COMMENT '照片路径',
    
    org_id INT NOT NULL COMMENT '所属三级机构ID',
    position_id INT NOT NULL COMMENT '职位ID',
    title ENUM('初级', '中级', '高级') COMMENT '职称',
    
    status ENUM('待复核', '正常', '已删除') DEFAULT '待复核',
    created_by VARCHAR(50) COMMENT '登记人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_by VARCHAR(50) COMMENT '复核人',
    reviewed_at TIMESTAMP NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (org_id) REFERENCES organizations(id),
    FOREIGN KEY (position_id) REFERENCES positions(id),
    FOREIGN KEY (salary_standard_id) REFERENCES salary_standards(id)
);

-- 7. 薪酬发放表
CREATE TABLE salary_issues (
    id INT PRIMARY KEY AUTO_INCREMENT,
    issue_number VARCHAR(20) UNIQUE NOT NULL COMMENT '薪酬单号',
    org_id INT NOT NULL COMMENT '发放机构ID',
    issue_date DATE NOT NULL COMMENT '发放日期',
    total_employees INT COMMENT '总人数',
    total_amount DECIMAL(12,2) COMMENT '总额',
    
    status ENUM('待复核', '已复核', '已发放') DEFAULT '待复核',
    created_by VARCHAR(50) COMMENT '登记人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reviewed_by VARCHAR(50) COMMENT '复核人',
    reviewed_at TIMESTAMP NULL,
    
    FOREIGN KEY (org_id) REFERENCES organizations(id)
);

-- 8. 薪酬发放明细表
CREATE TABLE salary_issue_details (
    id INT PRIMARY KEY AUTO_INCREMENT,
    issue_id INT NOT NULL,
    employee_id INT NOT NULL,
    salary_standard_id INT NOT NULL,
    reward_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '奖励金额',
    deduction_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '应扣金额',
    final_amount DECIMAL(10,2) COMMENT '实发金额',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (issue_id) REFERENCES salary_issues(id),
    FOREIGN KEY (employee_id) REFERENCES employees(id),
    FOREIGN KEY (salary_standard_id) REFERENCES salary_standards(id)
);

-- 插入示例数据

-- 机构数据
INSERT INTO organizations (org_code, org_name, org_level, parent_id) VALUES
('01', '总部', 1, NULL),
('0101', '技术部', 2, 1),
('010101', '开发一组', 3, 2),
('010102', '开发二组', 3, 2),
('02', '销售部', 1, NULL),
('0201', '销售一部', 2, 5);

-- 职位数据
INSERT INTO positions (position_name, org_id, description) VALUES
('Java开发工程师', 3, '负责Java后端开发'),
('前端开发工程师', 3, '负责前端页面开发'),
('项目经理', 4, '负责项目管理工作'),
('销售专员', 6, '负责产品销售');

-- 薪酬项目数据
INSERT INTO salary_items (item_name, item_type, calculation_rule) VALUES
('基本工资', '收入', NULL),
('交通补助', '收入', NULL),
('午餐补助', '收入', NULL),
('通信补助', '收入', NULL),
('养老保险', '扣除', '基本工资 * 8%'),
('医疗保险', '扣除', '基本工资 * 2% + 3'),
('失业保险', '扣除', '基本工资 * 0.5%'),
('住房公积金', '扣除', '基本工资 * 8%');

-- 薪酬标准数据
INSERT INTO salary_standards (standard_number, standard_name, creator, registrar, total_amount, status) VALUES
('SAL2025001', '中级开发工程师标准', '王经理', '李专员', 15000.00, '已生效'),
('SAL2025002', '初级开发工程师标准', '王经理', '李专员', 10000.00, '待复核');

-- 薪酬标准明细数据
INSERT INTO salary_standard_details (standard_id, salary_item_id, amount) VALUES
(1, 1, 12000.00),
(1, 2, 500.00),
(1, 3, 300.00),
(1, 4, 200.00),
(2, 1, 8000.00),
(2, 2, 300.00),
(2, 3, 200.00),
(2, 4, 100.00);

-- 员工档案数据
INSERT INTO employees (archive_number, name, gender, email, mobile, org_id, position_id, title, salary_standard_id, status, created_by) VALUES
('20250101010101', '张三', '男', 'zhangsan@company.com', '13800138000', 3, 1, '中级', 1, '正常', 'admin'),
('20250101010102', '李四', '女', 'lisi@company.com', '13900139000', 3, 2, '初级', 2, '待复核', 'admin');

-- 薪酬发放数据
INSERT INTO salary_issues (issue_number, org_id, issue_date, total_employees, total_amount, status, created_by) VALUES
('ISSUE202501001', 3, '2025-01-15', 2, 30500.00, '待复核', '薪酬专员');

-- 薪酬发放明细数据
INSERT INTO salary_issue_details (issue_id, employee_id, salary_standard_id, reward_amount, deduction_amount, final_amount) VALUES
(1, 1, 1, 1000.00, 500.00, 15500.00),
(1, 2, 2, 500.00, 300.00, 10200.00);

-- 创建索引以提高查询性能
CREATE INDEX idx_employees_org_id ON employees(org_id);
CREATE INDEX idx_employees_position_id ON employees(position_id);
CREATE INDEX idx_employees_status ON employees(status);
CREATE INDEX idx_salary_issues_org_id ON salary_issues(org_id);
CREATE INDEX idx_salary_issues_status ON salary_issues(status);
CREATE INDEX idx_organizations_parent_id ON organizations(parent_id);

-- 查询所有表验证数据
SELECT '机构表' as table_name, COUNT(*) as record_count FROM organizations
UNION ALL
SELECT '职位表', COUNT(*) FROM positions
UNION ALL
SELECT '薪酬项目表', COUNT(*) FROM salary_items
UNION ALL
SELECT '薪酬标准表', COUNT(*) FROM salary_standards
UNION ALL
SELECT '薪酬标准明细表', COUNT(*) FROM salary_standard_details
UNION ALL
SELECT '员工档案表', COUNT(*) FROM employees
UNION ALL
SELECT '薪酬发放表', COUNT(*) FROM salary_issues
UNION ALL
SELECT '薪酬发放明细表', COUNT(*) FROM salary_issue_details;