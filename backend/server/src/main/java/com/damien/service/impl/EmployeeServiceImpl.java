package com.damien.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.constant.MessageConstant;
import com.damien.constant.PasswordConstant;
import com.damien.context.BaseContext;
import com.damien.dto.EmployeeDTO;
import com.damien.dto.EmployeeLoginDTO;
import com.damien.dto.EmployeePasswordDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Employee;
import com.damien.entity.Organizations;
import com.damien.entity.Positions;
import com.damien.entity.SalaryStandards;
import com.damien.exception.AccountNotFoundException;
import com.damien.exception.PasswordErrorException;
import com.damien.mapper.EmployeeMapper;
import com.damien.mapper.OrganizationsMapper;
import com.damien.mapper.PositionsMapper;
import com.damien.mapper.SalaryStandardsMapper;
import com.damien.query.EmployeeQuery;
import com.damien.service.IEmployeeService;
import com.damien.vo.EmployeeVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private OrganizationsMapper organizationsMapper;

    @Autowired
    private PositionsMapper positionsMapper;

    @Autowired
    private SalaryStandardsMapper salaryStandardsMapper;

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

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * @param employeeDTO
     */
    @Override
    @Transactional
    public void save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        // 设置默认密码
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        } else {
            employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        }

        // 生成档案编号
        if (employee.getArchiveNumber() == null || employee.getArchiveNumber().isEmpty()) {
            employee.setArchiveNumber(generateArchiveNumber());
        }

        // 设置默认状态
        if (employee.getStatus() == null || employee.getStatus().isEmpty()) {
            employee.setStatus("待复核");
        }

        // 设置登记人
        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        if (currentUser != null) {
            employee.setCreatedBy(currentUser.getUsername());
        } else {
            employee.setCreatedBy("admin");
        }

        employeeMapper.insert(employee);
    }

    /**
     * 分页查询员工
     * @param employeeQuery
     * @return
     */
    @Override
    public PageDTO<EmployeeVO> pageQuery(EmployeeQuery employeeQuery) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        
        // 查询条件
        if (StringUtils.hasText(employeeQuery.getUsername())) {
            queryWrapper.like(Employee::getUsername, employeeQuery.getUsername());
        }
        if (StringUtils.hasText(employeeQuery.getArchiveNumber())) {
            queryWrapper.eq(Employee::getArchiveNumber, employeeQuery.getArchiveNumber());
        }
        if (employeeQuery.getOrgId() != null) {
            queryWrapper.eq(Employee::getOrgId, employeeQuery.getOrgId());
        }
        if (employeeQuery.getPositionId() != null) {
            queryWrapper.eq(Employee::getPositionId, employeeQuery.getPositionId());
        }
        if (StringUtils.hasText(employeeQuery.getStatus())) {
            queryWrapper.eq(Employee::getStatus, employeeQuery.getStatus());
        }

        // 排除已删除的记录
        queryWrapper.ne(Employee::getStatus, "已删除");

        // 分页查询
        Page<Employee> page = employeeQuery.toMpPageSortByCreatedAtDesc();
        page = employeeMapper.selectPage(page, queryWrapper);

        // 转换为VO并填充关联信息
        PageDTO<EmployeeVO> pageDTO = PageDTO.of(page, EmployeeVO.class);
        List<EmployeeVO> employeeVOList = fillRelatedInfo(pageDTO.getRecords());
        pageDTO.setRecords(employeeVOList);

        return pageDTO;
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @Override
    public EmployeeVO getById(Integer id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            return null;
        }
        EmployeeVO employeeVO = BeanUtil.copyProperties(employee, EmployeeVO.class);
        fillRelatedInfo(employeeVO);
        return employeeVO;
    }

    /**
     * 更新员工信息
     * @param employeeDTO
     */
    @Override
    @Transactional
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        // 如果密码不为空，则加密密码
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        } else {
            // 不更新密码
            employee.setPassword(null);
        }

        employeeMapper.updateById(employee);
    }

    /**
     * 删除员工（逻辑删除）
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Integer id) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setStatus("已删除");
        employeeMapper.updateById(employee);
    }

    /**
     * 复核员工
     * @param id
     * @param reviewOpinion
     */
    @Override
    @Transactional
    public void review(Integer id, String reviewOpinion) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        String reviewer = currentUser != null ? currentUser.getUsername() : "admin";

        employee.setReviewedBy(reviewer);
        employee.setReviewedAt(LocalDateTime.now());
        employee.setStatus("正常");

        employeeMapper.updateById(employee);
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

    /**
     * 生成档案编号
     * @return 档案编号
     */
    private String generateArchiveNumber() {
        // 格式：YYYYMMDD + 6位数字
        String prefix = LocalDateTime.now().toString().substring(0, 10).replace("-", "");
        
        // 查询当天最大的档案编号
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Employee::getArchiveNumber, prefix);
        queryWrapper.orderByDesc(Employee::getArchiveNumber);
        queryWrapper.last("LIMIT 1");
        
        Employee lastEmployee = employeeMapper.selectOne(queryWrapper);
        if (lastEmployee == null) {
            return prefix + "000001";
        }
        
        String lastNumber = lastEmployee.getArchiveNumber();
        if (lastNumber.length() >= 14) {
            String numberPart = lastNumber.substring(8);
            int number = Integer.parseInt(numberPart) + 1;
            return prefix + String.format("%06d", number);
        }
        
        return prefix + "000001";
    }

    /**
     * 填充员工关联信息（机构名称、职位名称、薪酬标准名称）
     * @param employeeVOList
     * @return
     */
    private List<EmployeeVO> fillRelatedInfo(List<EmployeeVO> employeeVOList) {
        // 获取所有关联的机构、职位、薪酬标准
        List<Organizations> organizationsList = organizationsMapper.selectList(null);
        List<Positions> positionsList = positionsMapper.selectList(null);
        List<SalaryStandards> salaryStandardsList = salaryStandardsMapper.selectList(null);

        Map<Integer, Organizations> orgMap = organizationsList.stream()
                .collect(Collectors.toMap(Organizations::getId, o -> o));
        Map<Integer, Positions> positionMap = positionsList.stream()
                .collect(Collectors.toMap(Positions::getId, p -> p));
        Map<Integer, SalaryStandards> salaryMap = salaryStandardsList.stream()
                .collect(Collectors.toMap(SalaryStandards::getId, s -> s));

        for (EmployeeVO employeeVO : employeeVOList) {
            fillRelatedInfo(employeeVO, orgMap, positionMap, salaryMap);
        }

        return employeeVOList;
    }

    /**
     * 填充单个员工关联信息
     * @param employeeVO
     */
    private void fillRelatedInfo(EmployeeVO employeeVO) {
        List<Organizations> organizationsList = organizationsMapper.selectList(null);
        List<Positions> positionsList = positionsMapper.selectList(null);
        List<SalaryStandards> salaryStandardsList = salaryStandardsMapper.selectList(null);

        Map<Integer, Organizations> orgMap = organizationsList.stream()
                .collect(Collectors.toMap(Organizations::getId, o -> o));
        Map<Integer, Positions> positionMap = positionsList.stream()
                .collect(Collectors.toMap(Positions::getId, p -> p));
        Map<Integer, SalaryStandards> salaryMap = salaryStandardsList.stream()
                .collect(Collectors.toMap(SalaryStandards::getId, s -> s));

        fillRelatedInfo(employeeVO, orgMap, positionMap, salaryMap);
    }

    /**
     * 填充单个员工关联信息
     * @param employeeVO
     * @param orgMap
     * @param positionMap
     * @param salaryMap
     */
    private void fillRelatedInfo(EmployeeVO employeeVO, Map<Integer, Organizations> orgMap,
                                  Map<Integer, Positions> positionMap,
                                  Map<Integer, SalaryStandards> salaryMap) {
        if (employeeVO.getOrgId() != null && orgMap.containsKey(employeeVO.getOrgId())) {
            employeeVO.setOrgName(orgMap.get(employeeVO.getOrgId()).getOrgName());
        }
        if (employeeVO.getPositionId() != null && positionMap.containsKey(employeeVO.getPositionId())) {
            employeeVO.setPositionName(positionMap.get(employeeVO.getPositionId()).getPositionName());
        }
        if (employeeVO.getSalaryStandardId() != null && salaryMap.containsKey(employeeVO.getSalaryStandardId())) {
            employeeVO.setSalaryStandardName(salaryMap.get(employeeVO.getSalaryStandardId()).getStandardName());
        }
    }
}
