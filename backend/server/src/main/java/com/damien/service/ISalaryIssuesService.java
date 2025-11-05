package com.damien.service;

import com.damien.dto.PageDTO;
import com.damien.dto.SalaryIssueDTO;
import com.damien.entity.SalaryIssues;
import com.damien.query.SalaryIssueQuery;
import com.damien.vo.SalaryIssueVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
public interface ISalaryIssuesService extends IService<SalaryIssues> {

    /**
     * 新增薪酬发放
     * @param salaryIssueDTO
     */
    void save(SalaryIssueDTO salaryIssueDTO);

    /**
     * 分页查询薪酬发放
     * @param query
     * @return
     */
    PageDTO<SalaryIssueVO> pageQuery(SalaryIssueQuery query);

    /**
     * 根据id查询薪酬发放
     * @param id
     * @return
     */
    SalaryIssueVO getById(Integer id);

    /**
     * 更新薪酬发放
     * @param salaryIssueDTO
     */
    void update(SalaryIssueDTO salaryIssueDTO);

    /**
     * 复核薪酬发放
     * @param id
     * @param reviewOpinion
     * @param isApproved
     */
    void review(Integer id, String reviewOpinion, Boolean isApproved);

    /**
     * 发放薪酬
     * @param id
     */
    void issue(Integer id);

    /**
     * 生成薪酬单号
     * @return
     */
    String generateIssueNumber();
}
