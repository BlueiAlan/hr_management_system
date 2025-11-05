-- 人力资源管理系统 - 修改所有表ID为BIGINT类型
-- 执行此脚本前请备份数据库

USE hr_management_system;

-- 1. 首先删除所有外键约束
ALTER TABLE salary_issue_details DROP FOREIGN KEY salary_issue_details_ibfk_1;
ALTER TABLE salary_issue_details DROP FOREIGN KEY salary_issue_details_ibfk_2;
ALTER TABLE salary_issue_details DROP FOREIGN KEY salary_issue_details_ibfk_3;

ALTER TABLE salary_standard_details DROP FOREIGN KEY salary_standard_details_ibfk_1;
ALTER TABLE salary_standard_details DROP FOREIGN KEY salary_standard_details_ibfk_2;

ALTER TABLE employees DROP FOREIGN KEY employees_ibfk_1;
ALTER TABLE employees DROP FOREIGN KEY employees_ibfk_2;
ALTER TABLE employees DROP FOREIGN KEY employees_ibfk_3;

ALTER TABLE positions DROP FOREIGN KEY positions_ibfk_1;

ALTER TABLE salary_issues DROP FOREIGN KEY salary_issues_ibfk_1;

ALTER TABLE organizations DROP FOREIGN KEY organizations_ibfk_1;

-- 2. 修改organizations表的ID为BIGINT
ALTER TABLE organizations MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;

-- 3. 修改positions表的ID为BIGINT，并更新外键引用
ALTER TABLE positions MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE positions MODIFY org_id BIGINT;
ALTER TABLE positions ADD FOREIGN KEY (org_id) REFERENCES organizations(id);

-- 4. 修改salary_items表的ID为BIGINT
ALTER TABLE salary_items MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;

-- 5. 修改salary_standards表的ID为BIGINT
ALTER TABLE salary_standards MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;

-- 6. 修改salary_standard_details表的ID为BIGINT，并更新外键引用
ALTER TABLE salary_standard_details MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE salary_standard_details MODIFY standard_id BIGINT;
ALTER TABLE salary_standard_details MODIFY salary_item_id BIGINT;
ALTER TABLE salary_standard_details ADD FOREIGN KEY (standard_id) REFERENCES salary_standards(id);
ALTER TABLE salary_standard_details ADD FOREIGN KEY (salary_item_id) REFERENCES salary_items(id);

-- 7. 修改employees表的ID为BIGINT，并更新外键引用
ALTER TABLE employees MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE employees MODIFY org_id BIGINT;
ALTER TABLE employees MODIFY position_id BIGINT;
ALTER TABLE employees MODIFY salary_standard_id BIGINT;
ALTER TABLE employees ADD FOREIGN KEY (org_id) REFERENCES organizations(id);
ALTER TABLE employees ADD FOREIGN KEY (position_id) REFERENCES positions(id);
ALTER TABLE employees ADD FOREIGN KEY (salary_standard_id) REFERENCES salary_standards(id);

-- 8. 修改salary_issues表的ID为BIGINT，并更新外键引用
ALTER TABLE salary_issues MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE salary_issues MODIFY org_id BIGINT;
ALTER TABLE salary_issues ADD FOREIGN KEY (org_id) REFERENCES organizations(id);

-- 9. 修改salary_issue_details表的ID为BIGINT，并更新外键引用
ALTER TABLE salary_issue_details MODIFY id BIGINT PRIMARY KEY AUTO_INCREMENT;
ALTER TABLE salary_issue_details MODIFY issue_id BIGINT;
ALTER TABLE salary_issue_details MODIFY employee_id BIGINT;
ALTER TABLE salary_issue_details MODIFY salary_standard_id BIGINT;
ALTER TABLE salary_issue_details ADD FOREIGN KEY (issue_id) REFERENCES salary_issues(id);
ALTER TABLE salary_issue_details ADD FOREIGN KEY (employee_id) REFERENCES employees(id);
ALTER TABLE salary_issue_details ADD FOREIGN KEY (salary_standard_id) REFERENCES salary_standards(id);

-- 10. 更新organizations表的外键约束
ALTER TABLE organizations MODIFY parent_id BIGINT;
ALTER TABLE organizations ADD FOREIGN KEY (parent_id) REFERENCES organizations(id);

-- 验证修改结果
SELECT 
    TABLE_NAME,
    COLUMN_NAME,
    DATA_TYPE,
    COLUMN_TYPE
FROM 
    INFORMATION_SCHEMA.COLUMNS 
WHERE 
    TABLE_SCHEMA = 'hr_management_system' 
    AND COLUMN_NAME = 'id'
ORDER BY 
    TABLE_NAME;

-- 显示所有表的外键关系
SELECT
    TABLE_NAME,
    COLUMN_NAME,
    CONSTRAINT_NAME,
    REFERENCED_TABLE_NAME,
    REFERENCED_COLUMN_NAME
FROM
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE
WHERE
    REFERENCED_TABLE_SCHEMA = 'hr_management_system'
ORDER BY
    TABLE_NAME, CONSTRAINT_NAME;

-- 完成提示
SELECT '所有表的ID字段已成功修改为BIGINT类型' AS '修改结果';