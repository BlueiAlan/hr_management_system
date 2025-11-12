package com.damien.service;

import com.damien.dto.EmployeeDTO;
import com.damien.dto.EmployeeLoginDTO;
import com.damien.dto.EmployeePageQueryDTO;
import com.damien.dto.EmployeePasswordDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Employee;
import com.damien.query.EmployeeQuery;
import com.damien.result.PageResult;
import com.damien.vo.EmployeeVO;

public interface IEmployeeService {

    /**
     * 员工登录
     * 
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    /**
     * 新增员工
     * 
     * @param employeeDTO
     */
    void save(EmployeeDTO employeeDTO);

    /**
     * 分页查询员工
     * 
     * @param employeeQuery
     * @return
     */
    PageDTO<EmployeeVO> pageQuery(EmployeeQuery employeeQuery);

    /**
     * 根据id查询员工
     * 
     * @param id
     * @return
     */
    EmployeeVO getById(Integer id);

    /**
     * 更新员工信息
     * 
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 删除员工（逻辑删除）
     * 
     * @param id
     * @param deleteMsg 删除原因
     */
    void deleteById(Integer id, String deleteMsg);

    /**
     * 复核员工
     * 
     * @param id
     * @param reviewOpinion
     */
    void review(Integer id, String reviewOpinion);

    /**
     * 复核员工（支持更新信息）
     * 
     * @param employeeDTO   员工信息（可选，如果提供则更新信息）
     * @param reviewOpinion 复核意见
     */
    void reviewWithUpdate(EmployeeDTO employeeDTO, String reviewOpinion, Boolean isApproved);

    /**
     * 修改员工密码
     * 
     * @param passwordDTO 员工密码DTO
     */
    boolean updatePassword(EmployeePasswordDTO passwordDTO);

    /**
     * 恢复已删除的员工档案
     * 
     * @param id
     */
    void restore(Integer id);
}
