package com.damien.service;

import com.damien.dto.PageDTO;
import com.damien.dto.SalaryStandardDTO;
import com.damien.entity.SalaryStandards;
import com.damien.query.SalaryStandardQuery;
import com.damien.vo.SalaryStandardVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
public interface ISalaryStandardsService extends IService<SalaryStandards> {

    /**
     * 新增薪酬标准
     * @param salaryStandardDTO
     */
    void save(SalaryStandardDTO salaryStandardDTO);

    /**
     * 分页查询薪酬标准
     * @param query
     * @return
     */
    PageDTO<SalaryStandardVO> pageQuery(SalaryStandardQuery query);

    /**
     * 根据id查询薪酬标准
     * @param id
     * @return
     */
    SalaryStandardVO getById(Integer id);

    /**
     * 更新薪酬标准
     * @param salaryStandardDTO
     */
    void update(SalaryStandardDTO salaryStandardDTO);

    /**
     * 复核薪酬标准
     * @param id
     * @param reviewOpinion
     * @param isApproved 是否通过
     */
    void review(Integer id, String reviewOpinion, Boolean isApproved);

    /**
     * 生成薪酬标准编号
     * @return
     */
    String generateStandardNumber();
}
