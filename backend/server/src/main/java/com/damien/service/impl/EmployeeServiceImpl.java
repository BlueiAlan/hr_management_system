package com.damien.service.impl;

import com.damien.constant.MessageConstant;
import com.damien.constant.PasswordConstant;
import com.damien.context.BaseContext;
import com.damien.dto.EmployeeDTO;
import com.damien.dto.EmployeeLoginDTO;
import com.damien.dto.EmployeePageQueryDTO;
import com.damien.dto.EmployeePasswordDTO;
import com.damien.entity.Employee;
import com.damien.exception.AccountNotFoundException;
import com.damien.exception.PasswordErrorException;
import com.damien.mapper.EmployeeMapper;
import com.damien.result.PageResult;
import com.damien.service.IEmployeeService;
import com.damien.vo.EmployeeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     * @param employeeLoginDTO  员工登录DTO
     * @return  员工实体对象
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

//        if (employee.getStatus() == StatusConstant.DISABLE) {
//            //账号被锁定
//            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
//        }

        //3、返回实体对象
        return employee;
    }


    /**
     * 修改员工密码
     *
     * @param passwordDTO  员工密码信息
     */
    @Override
    public boolean updatePassword(EmployeePasswordDTO passwordDTO) {

        return false;
    }

}
