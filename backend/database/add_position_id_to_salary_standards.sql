-- 为薪酬标准表添加职位ID字段
-- 执行此SQL脚本以支持在薪酬标准中关联职位

ALTER TABLE `salary_standards` 
ADD COLUMN `position_id` INT(11) NULL COMMENT '适用职位ID' AFTER `review_opinion`;

-- 添加外键约束（可选，如果positions表存在）
-- ALTER TABLE `salary_standards` 
-- ADD CONSTRAINT `fk_salary_standards_position` 
-- FOREIGN KEY (`position_id`) REFERENCES `positions` (`id`);

