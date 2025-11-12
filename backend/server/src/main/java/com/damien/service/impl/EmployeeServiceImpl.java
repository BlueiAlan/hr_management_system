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
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
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
     * 
     * @param employeeLoginDTO 员工登录DTO
     * @return 员工实体对象
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        // 1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        // 2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            // 账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 密码比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            // 密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        // 3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     * 
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
            employee.setArchiveNumber(generateArchiveNumber(employee.getOrgId()));
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
     * 
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
        // 建档时间查询条件
        if (employeeQuery.getStartDate() != null) {
            queryWrapper.ge(Employee::getCreatedAt, employeeQuery.getStartDate().atStartOfDay());
        }
        if (employeeQuery.getEndDate() != null) {
            queryWrapper.le(Employee::getCreatedAt, employeeQuery.getEndDate().atTime(23, 59, 59));
        }

        // 排除已删除的记录（如果查询条件中没有指定状态，或者指定了其他状态）
        if (!StringUtils.hasText(employeeQuery.getStatus())) {
            queryWrapper.ne(Employee::getStatus, "已删除");
        } else if (!"已删除".equals(employeeQuery.getStatus())) {
            // 如果指定了状态但不是"已删除"，则排除已删除的记录
            queryWrapper.ne(Employee::getStatus, "已删除");
        }
        // 如果指定了status="已删除"，则只查询已删除的记录（不排除）

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
     * 
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
     * 更新员工信息（人力资源档案变更）
     * 
     * @param employeeDTO
     */
    @Override
    @Transactional
    public void update(EmployeeDTO employeeDTO) {
        Employee existingEmployee = employeeMapper.selectById(employeeDTO.getId());
        if (existingEmployee == null) {
            throw new RuntimeException("员工不存在");
        }

        // 档案编号、所属机构和职位不能修改
        employeeDTO.setArchiveNumber(null);
        employeeDTO.setOrgId(null);
        employeeDTO.setPositionId(null);

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        // 如果密码不为空，则加密密码
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(DigestUtils.md5DigestAsHex(employee.getPassword().getBytes()));
        } else {
            // 不更新密码
            employee.setPassword(null);
        }

        // 变更后状态变为"待复核"
        employee.setStatus("待复核");

        employeeMapper.updateById(employee);
    }

    /**
     * 删除员工（逻辑删除）
     * 
     * @param id
     */
    @Override
    @Transactional
    public void deleteById(Integer id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }
        // 状态为"待复核"的员工档案不能删除
        if ("待复核".equals(employee.getStatus())) {
            throw new RuntimeException("状态为'待复核'的员工档案不能删除");
        }
        employee.setStatus("已删除");
        employeeMapper.updateById(employee);
    }

    /**
     * 复核员工
     * 
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
     * 复核员工（支持更新信息）
     * 
     * @param employeeDTO   员工信息（可选，如果提供则更新信息）
     * @param reviewOpinion 复核意见
     * @param isApproved    是否通过复核
     */
    @Override
    @Transactional
    public void reviewWithUpdate(EmployeeDTO employeeDTO, String reviewOpinion, Boolean isApproved) {
        Employee employee = employeeMapper.selectById(employeeDTO.getId());
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }

        // 复核时可以修改信息（除档案编号、所属机构和职位外）
        if (employeeDTO.getUsername() != null) {
            employee.setUsername(employeeDTO.getUsername());
        }
        if (employeeDTO.getGender() != null) {
            employee.setGender(employeeDTO.getGender());
        }
        if (employeeDTO.getEmail() != null) {
            employee.setEmail(employeeDTO.getEmail());
        }
        if (employeeDTO.getPhone() != null) {
            employee.setPhone(employeeDTO.getPhone());
        }
        if (employeeDTO.getMobile() != null) {
            employee.setMobile(employeeDTO.getMobile());
        }
        if (employeeDTO.getTitle() != null) {
            employee.setTitle(employeeDTO.getTitle());
        }
        if (employeeDTO.getSalaryStandardId() != null) {
            employee.setSalaryStandardId(employeeDTO.getSalaryStandardId());
        }
        // 可以更新其他字段...
        if (employeeDTO.getQq() != null) {
            employee.setQq(employeeDTO.getQq());
        }
        if (employeeDTO.getAddress() != null) {
            employee.setAddress(employeeDTO.getAddress());
        }
        if (employeeDTO.getPostalCode() != null) {
            employee.setPostalCode(employeeDTO.getPostalCode());
        }
        if (employeeDTO.getNationality() != null) {
            employee.setNationality(employeeDTO.getNationality());
        }
        if (employeeDTO.getBirthPlace() != null) {
            employee.setBirthPlace(employeeDTO.getBirthPlace());
        }
        if (employeeDTO.getBirthDate() != null) {
            employee.setBirthDate(employeeDTO.getBirthDate());
        }
        if (employeeDTO.getEthnicity() != null) {
            employee.setEthnicity(employeeDTO.getEthnicity());
        }
        if (employeeDTO.getReligion() != null) {
            employee.setReligion(employeeDTO.getReligion());
        }
        if (employeeDTO.getPoliticalStatus() != null) {
            employee.setPoliticalStatus(employeeDTO.getPoliticalStatus());
        }
        if (employeeDTO.getIdCard() != null) {
            employee.setIdCard(employeeDTO.getIdCard());
        }
        if (employeeDTO.getAge() != null) {
            employee.setAge(employeeDTO.getAge());
        }
        if (employeeDTO.getEducation() != null) {
            employee.setEducation(employeeDTO.getEducation());
        }
        if (employeeDTO.getHobbies() != null) {
            employee.setHobbies(employeeDTO.getHobbies());
        }
        if (employeeDTO.getResume() != null) {
            employee.setResume(employeeDTO.getResume());
        }
        if (employeeDTO.getFamilyInfo() != null) {
            employee.setFamilyInfo(employeeDTO.getFamilyInfo());
        }
        if (employeeDTO.getRemarks() != null) {
            employee.setRemarks(employeeDTO.getRemarks());
        }
        if (employeeDTO.getPhotoPath() != null) {
            employee.setPhotoPath(employeeDTO.getPhotoPath());
        }

        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        String reviewer = currentUser != null ? currentUser.getUsername() : "admin";

        employee.setReviewedBy(reviewer);
        employee.setReviewedAt(LocalDateTime.now());
        
        // 根据是否通过设置状态
        log.info("复核员工，isApproved: {}, 当前状态: {}", isApproved, employee.getStatus());
        if (isApproved != null && isApproved) {
            employee.setStatus("正常");
            log.info("设置状态为：正常");
        } else {
            employee.setStatus("不通过"); // 不通过时设置为"不通过"
            log.info("设置状态为：不通过");
        }

        employeeMapper.updateById(employee);
        log.info("更新后的状态: {}", employee.getStatus());
    }

    /**
     * 修改员工密码
     *
     * @param passwordDTO 员工密码信息
     */
    @Override
    public boolean updatePassword(EmployeePasswordDTO passwordDTO) {
        return false;
    }

    /**
     * 恢复已删除的员工档案
     * 
     * @param id
     */
    @Override
    @Transactional
    public void restore(Integer id) {
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            throw new RuntimeException("员工不存在");
        }
        if (!"已删除".equals(employee.getStatus())) {
            throw new RuntimeException("该档案不是已删除状态，无法恢复");
        }

        employee.setStatus("正常");
        employeeMapper.updateById(employee);
    }

    /**
     * 生成档案编号
     * 格式：年份（4位）+一级机构编号（2位）+二级机构编号（2位）+三级机构编号（2位）+编号（2位）
     * 
     * @param orgId 三级机构ID
     * @return 档案编号
     */
    private String generateArchiveNumber(Integer orgId) {
        if (orgId == null) {
            throw new RuntimeException("机构ID不能为空");
        }

        // 获取三级机构
        Organizations org3 = organizationsMapper.selectById(orgId);
        if (org3 == null || org3.getOrgLevel() != 3) {
            throw new RuntimeException("无效的三级机构ID");
        }

        // 获取二级机构
        Organizations org2 = organizationsMapper.selectById(org3.getParentId());
        if (org2 == null || org2.getOrgLevel() != 2) {
            throw new RuntimeException("无效的二级机构");
        }

        // 获取一级机构
        Organizations org1 = organizationsMapper.selectById(org2.getParentId());
        if (org1 == null || org1.getOrgLevel() != 1) {
            throw new RuntimeException("无效的一级机构");
        }

        // 年份（4位）
        String year = String.valueOf(LocalDateTime.now().getYear());

        // 获取机构编码（取后2位）
        String org1Code = org1.getOrgCode();
        String org2Code = org2.getOrgCode();
        String org3Code = org3.getOrgCode();

        // 一级机构编码：取后2位（如果不足2位，前面补0）
        if (org1Code == null || org1Code.isEmpty()) {
            org1Code = "01";
        } else if (org1Code.length() < 2) {
            org1Code = String.format("%02d", Integer.parseInt(org1Code));
        } else {
            org1Code = org1Code.substring(org1Code.length() - 2);
        }

        // 二级机构编码：去掉一级机构编码部分，取后2位
        if (org2Code == null || org2Code.isEmpty()) {
            org2Code = "01";
        } else if (org2Code.length() <= 2) {
            org2Code = String.format("%02d", Integer.parseInt(org2Code));
        } else {
            // 去掉一级机构编码部分
            String org2Part = org2Code.substring(org1Code.length());
            if (org2Part.length() < 2) {
                org2Code = String.format("%02d", Integer.parseInt(org2Part));
            } else {
                org2Code = org2Part.substring(org2Part.length() - 2);
            }
        }

        // 三级机构编码：去掉一级和二级机构编码部分，取后2位
        if (org3Code == null || org3Code.isEmpty()) {
            org3Code = "01";
        } else if (org3Code.length() <= 4) {
            // 如果长度<=4，说明只有一级+二级，取最后2位
            String org3Part = org3Code.length() > 2 ? org3Code.substring(org3Code.length() - 2) : org3Code;
            org3Code = String.format("%02d", Integer.parseInt(org3Part));
        } else {
            // 去掉一级和二级机构编码部分
            String org3Part = org3Code.substring(org1Code.length() + org2Code.length());
            if (org3Part.length() < 2) {
                org3Code = String.format("%02d", Integer.parseInt(org3Part));
            } else {
                org3Code = org3Part.substring(org3Part.length() - 2);
            }
        }

        // 查询该三级机构下最大的编号
        String prefix = year + org1Code + org2Code + org3Code;
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.likeRight(Employee::getArchiveNumber, prefix);
        queryWrapper.orderByDesc(Employee::getArchiveNumber);
        queryWrapper.last("LIMIT 1");

        Employee lastEmployee = employeeMapper.selectOne(queryWrapper);
        int number = 1;
        if (lastEmployee != null && lastEmployee.getArchiveNumber() != null) {
            String lastNumber = lastEmployee.getArchiveNumber();
            if (lastNumber.length() >= 12) {
                String numberPart = lastNumber.substring(10);
                try {
                    number = Integer.parseInt(numberPart) + 1;
                } catch (NumberFormatException e) {
                    number = 1;
                }
            }
        }

        // 编号（2位）
        String numberStr = String.format("%02d", number);

        return prefix + numberStr;
    }

    /**
     * 填充员工关联信息（机构名称、职位名称、薪酬标准名称）
     * 
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
     * 
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
     * 
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
