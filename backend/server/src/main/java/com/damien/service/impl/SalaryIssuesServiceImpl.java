package com.damien.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.context.BaseContext;
import com.damien.dto.PageDTO;
import com.damien.dto.SalaryIssueDTO;
import com.damien.dto.SalaryIssueDetailDTO;
import com.damien.entity.Employee;
import com.damien.entity.Organizations;
import com.damien.entity.SalaryIssueDetails;
import com.damien.entity.SalaryIssues;
import com.damien.entity.SalaryStandards;
import com.damien.mapper.EmployeeMapper;
import com.damien.mapper.OrganizationsMapper;
import com.damien.mapper.SalaryIssueDetailsMapper;
import com.damien.mapper.SalaryIssuesMapper;
import com.damien.mapper.SalaryStandardsMapper;
import com.damien.query.SalaryIssueQuery;
import com.damien.service.ISalaryIssuesService;
import com.damien.vo.SalaryIssueDetailVO;
import com.damien.vo.SalaryIssueVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
@Service
public class SalaryIssuesServiceImpl extends ServiceImpl<SalaryIssuesMapper, SalaryIssues> implements ISalaryIssuesService {

    @Autowired
    private SalaryIssueDetailsMapper salaryIssueDetailsMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private OrganizationsMapper organizationsMapper;

    @Autowired
    private SalaryStandardsMapper salaryStandardsMapper;

    /**
     * 新增薪酬发放
     * @param salaryIssueDTO
     */
    @Override
    @Transactional
    public void save(SalaryIssueDTO salaryIssueDTO) {
        SalaryIssues salaryIssues = new SalaryIssues();
        BeanUtils.copyProperties(salaryIssueDTO, salaryIssues);

        // 生成单号
        if (!StringUtils.hasText(salaryIssues.getIssueNumber())) {
            salaryIssues.setIssueNumber(generateIssueNumber());
        }

        // 设置默认状态
        if (!StringUtils.hasText(salaryIssues.getStatus())) {
            salaryIssues.setStatus("待复核");
        }

        // 设置登记人
        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        if (currentUser != null) {
            salaryIssues.setCreatedBy(currentUser.getUsername());
        } else {
            salaryIssues.setCreatedBy("admin");
        }

        // 计算总人数和总额
        if (salaryIssueDTO.getDetails() != null && !salaryIssueDTO.getDetails().isEmpty()) {
            salaryIssues.setTotalEmployees(salaryIssueDTO.getDetails().size());
            BigDecimal totalAmount = salaryIssueDTO.getDetails().stream()
                    .map(SalaryIssueDetailDTO::getFinalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            salaryIssues.setTotalAmount(totalAmount);
        }

        // 保存薪酬发放
        baseMapper.insert(salaryIssues);

        // 保存明细
        if (salaryIssueDTO.getDetails() != null && !salaryIssueDTO.getDetails().isEmpty()) {
            saveDetails(salaryIssues.getId(), salaryIssueDTO.getDetails());
        }
    }

    /**
     * 分页查询薪酬发放
     * @param query
     * @return
     */
    @Override
    public PageDTO<SalaryIssueVO> pageQuery(SalaryIssueQuery query) {
        LambdaQueryWrapper<SalaryIssues> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getIssueNumber())) {
            queryWrapper.like(SalaryIssues::getIssueNumber, query.getIssueNumber());
        }
        if (query.getOrgId() != null) {
            queryWrapper.eq(SalaryIssues::getOrgId, query.getOrgId());
        }
        if (StringUtils.hasText(query.getStatus())) {
            queryWrapper.eq(SalaryIssues::getStatus, query.getStatus());
        }

        Page<SalaryIssues> page = query.toMpPageSortByCreatedAtDesc();
        page = this.page(page, queryWrapper);

        PageDTO<SalaryIssueVO> pageDTO = PageDTO.of(page, SalaryIssueVO.class);

        // 填充关联信息
        List<SalaryIssueVO> voList = pageDTO.getRecords();
        for (SalaryIssueVO vo : voList) {
            fillRelatedInfo(vo);
        }

        return pageDTO;
    }

    /**
     * 根据id查询薪酬发放
     * @param id
     * @return
     */
    @Override
    public SalaryIssueVO getById(Integer id) {
        SalaryIssues salaryIssues = baseMapper.selectById(id);
        if (salaryIssues == null) {
            return null;
        }

        SalaryIssueVO vo = BeanUtil.copyProperties(salaryIssues, SalaryIssueVO.class);
        fillRelatedInfo(vo);
        return vo;
    }

    /**
     * 更新薪酬发放
     * @param salaryIssueDTO
     */
    @Override
    @Transactional
    public void update(SalaryIssueDTO salaryIssueDTO) {
        SalaryIssues salaryIssues = new SalaryIssues();
        BeanUtils.copyProperties(salaryIssueDTO, salaryIssues);

        // 重新计算总人数和总额
        if (salaryIssueDTO.getDetails() != null && !salaryIssueDTO.getDetails().isEmpty()) {
            salaryIssues.setTotalEmployees(salaryIssueDTO.getDetails().size());
            BigDecimal totalAmount = salaryIssueDTO.getDetails().stream()
                    .map(SalaryIssueDetailDTO::getFinalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            salaryIssues.setTotalAmount(totalAmount);
        }

        baseMapper.updateById(salaryIssues);

        // 删除旧的明细
        LambdaQueryWrapper<SalaryIssueDetails> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SalaryIssueDetails::getIssueId, salaryIssueDTO.getId());
        salaryIssueDetailsMapper.delete(deleteWrapper);

        // 保存新的明细
        if (salaryIssueDTO.getDetails() != null && !salaryIssueDTO.getDetails().isEmpty()) {
            saveDetails(salaryIssueDTO.getId(), salaryIssueDTO.getDetails());
        }
    }

    /**
     * 复核薪酬发放
     * @param id
     * @param reviewOpinion
     * @param isApproved
     */
    @Override
    @Transactional
    public void review(Integer id, String reviewOpinion, Boolean isApproved) {
        SalaryIssues salaryIssues = baseMapper.selectById(id);
        if (salaryIssues == null) {
            throw new RuntimeException("薪酬发放不存在");
        }

        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        String reviewer = currentUser != null ? currentUser.getUsername() : "admin";

        salaryIssues.setReviewedBy(reviewer);
        salaryIssues.setReviewedAt(LocalDateTime.now());

        if (isApproved != null && isApproved) {
            salaryIssues.setStatus("已复核");
        } else {
            salaryIssues.setStatus("待复核");
        }

        baseMapper.updateById(salaryIssues);
    }

    /**
     * 发放薪酬
     * @param id
     */
    @Override
    @Transactional
    public void issue(Integer id) {
        SalaryIssues salaryIssues = baseMapper.selectById(id);
        if (salaryIssues == null) {
            throw new RuntimeException("薪酬发放不存在");
        }

        if (!"已复核".equals(salaryIssues.getStatus())) {
            throw new RuntimeException("只有已复核的薪酬发放才能执行发放操作");
        }

        salaryIssues.setStatus("已发放");
        baseMapper.updateById(salaryIssues);
    }

    /**
     * 生成薪酬单号
     * 格式：ISS + 年(4位) + 月(2位) + 三位数
     * 示例：ISS202511001
     * @return
     */
    @Override
    public String generateIssueNumber() {
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String prefix = "ISS" + year + month;

        LambdaQueryWrapper<SalaryIssues> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SalaryIssues::getIssueNumber, prefix);
        queryWrapper.orderByDesc(SalaryIssues::getIssueNumber);
        queryWrapper.last("LIMIT 1");

        SalaryIssues lastIssue = baseMapper.selectOne(queryWrapper);
        if (lastIssue == null) {
            return prefix + "001";
        }

        String lastNumber = lastIssue.getIssueNumber();
        if (lastNumber.startsWith(prefix) && lastNumber.length() >= prefix.length() + 3) {
            String numberPart = lastNumber.substring(prefix.length());
            try {
                int number = Integer.parseInt(numberPart) + 1;
                return prefix + String.format("%03d", number);
            } catch (NumberFormatException e) {
                return prefix + "001";
            }
        }

        return prefix + "001";
    }

    /**
     * 保存明细
     * @param issueId
     * @param detailDTOList
     */
    private void saveDetails(Integer issueId, List<SalaryIssueDetailDTO> detailDTOList) {
        for (SalaryIssueDetailDTO detailDTO : detailDTOList) {
            SalaryIssueDetails details = new SalaryIssueDetails();
            details.setIssueId(issueId);
            details.setEmployeeId(detailDTO.getEmployeeId());
            details.setSalaryStandardId(detailDTO.getSalaryStandardId());
            details.setRewardAmount(detailDTO.getRewardAmount() != null ? detailDTO.getRewardAmount() : BigDecimal.ZERO);
            details.setDeductionAmount(detailDTO.getDeductionAmount() != null ? detailDTO.getDeductionAmount() : BigDecimal.ZERO);
            details.setFinalAmount(detailDTO.getFinalAmount());
            salaryIssueDetailsMapper.insert(details);
        }
    }

    /**
     * 填充关联信息
     * @param vo
     */
    private void fillRelatedInfo(SalaryIssueVO vo) {
        // 填充机构名称
        if (vo.getOrgId() != null) {
            Organizations org = organizationsMapper.selectById(vo.getOrgId());
            if (org != null) {
                vo.setOrgName(org.getOrgName());
            }
        }

        // 填充明细信息
        LambdaQueryWrapper<SalaryIssueDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryIssueDetails::getIssueId, vo.getId());
        List<SalaryIssueDetails> detailsList = salaryIssueDetailsMapper.selectList(queryWrapper);

        // 获取所有员工和薪酬标准
        List<Employee> employeeList = employeeMapper.selectList(null);
        List<SalaryStandards> salaryStandardsList = salaryStandardsMapper.selectList(null);

        Map<Integer, Employee> employeeMap = employeeList.stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));
        Map<Integer, SalaryStandards> salaryMap = salaryStandardsList.stream()
                .collect(Collectors.toMap(SalaryStandards::getId, s -> s));

        List<SalaryIssueDetailVO> detailVOList = detailsList.stream().map(detail -> {
            SalaryIssueDetailVO detailVO = BeanUtil.copyProperties(detail, SalaryIssueDetailVO.class);
            if (employeeMap.containsKey(detail.getEmployeeId())) {
                Employee employee = employeeMap.get(detail.getEmployeeId());
                detailVO.setEmployeeName(employee.getUsername());
                detailVO.setArchiveNumber(employee.getArchiveNumber());
            }
            if (salaryMap.containsKey(detail.getSalaryStandardId())) {
                detailVO.setSalaryStandardName(salaryMap.get(detail.getSalaryStandardId()).getStandardName());
            }
            return detailVO;
        }).collect(Collectors.toList());

        vo.setDetails(detailVOList);
    }
}
