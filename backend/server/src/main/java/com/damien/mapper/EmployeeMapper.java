package com.damien.mapper;

import com.damien.annotation.AutoFill;
import com.damien.dto.EmployeePageQueryDTO;
import com.damien.entity.Employee;
import com.damien.enumeration.OperationType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据
     * @param employee 员工对象
     */
    @AutoFill(value = OperationType.INSERT)
    void save(Employee employee);


    /**
     * 更新员工信息
     * @param employee  员工对象
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Employee employee);
}
