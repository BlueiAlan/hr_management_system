package com.damien.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.context.BaseContext;
import com.damien.dto.PageDTO;
import com.damien.dto.SalaryStandardDTO;
import com.damien.dto.SalaryStandardDetailDTO;
import com.damien.entity.Employee;
import com.damien.entity.SalaryItems;
import com.damien.entity.SalaryStandardDetails;
import com.damien.entity.SalaryStandards;
import com.damien.mapper.EmployeeMapper;
import com.damien.mapper.SalaryItemsMapper;
import com.damien.mapper.SalaryStandardDetailsMapper;
import com.damien.mapper.SalaryStandardsMapper;
import com.damien.query.SalaryStandardQuery;
import com.damien.service.ISalaryStandardsService;
import com.damien.vo.SalaryStandardDetailVO;
import com.damien.vo.SalaryStandardVO;
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
public class SalaryStandardsServiceImpl extends ServiceImpl<SalaryStandardsMapper, SalaryStandards> implements ISalaryStandardsService {

    @Autowired
    private SalaryStandardDetailsMapper salaryStandardDetailsMapper;

    @Autowired
    private SalaryItemsMapper salaryItemsMapper;

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 新增薪酬标准
     * @param salaryStandardDTO
     */
    @Override
    @Transactional
    public void save(SalaryStandardDTO salaryStandardDTO) {
        SalaryStandards salaryStandards = new SalaryStandards();
        BeanUtils.copyProperties(salaryStandardDTO, salaryStandards);

        // 生成编号
        if (!StringUtils.hasText(salaryStandards.getStandardNumber())) {
            salaryStandards.setStandardNumber(generateStandardNumber());
        }

        // 设置默认状态
        if (!StringUtils.hasText(salaryStandards.getStatus())) {
            salaryStandards.setStatus("待复核");
        }

        // 设置登记时间
        if (salaryStandards.getRegisterTime() == null) {
            salaryStandards.setRegisterTime(LocalDate.now());
        }

        // 设置登记人
        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        if (currentUser != null) {
            salaryStandards.setRegistrar(currentUser.getUsername());
        } else {
            salaryStandards.setRegistrar("admin");
        }

        // 计算总额（根据项目类型：收入类型加上，扣除类型减去）
        if (salaryStandardDTO.getDetails() != null && !salaryStandardDTO.getDetails().isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            // 获取所有薪酬项目信息
            List<Integer> salaryItemIds = salaryStandardDTO.getDetails().stream()
                    .map(SalaryStandardDetailDTO::getSalaryItemId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            if (!salaryItemIds.isEmpty()) {
                List<SalaryItems> salaryItemsList = salaryItemsMapper.selectBatchIds(salaryItemIds);
                Map<Integer, SalaryItems> salaryItemsMap = salaryItemsList.stream()
                        .collect(Collectors.toMap(SalaryItems::getId, item -> item));
                
                for (SalaryStandardDetailDTO detail : salaryStandardDTO.getDetails()) {
                    if (detail.getSalaryItemId() != null && detail.getAmount() != null) {
                        SalaryItems salaryItem = salaryItemsMap.get(detail.getSalaryItemId());
                        if (salaryItem != null) {
                            if ("收入".equals(salaryItem.getItemType())) {
                                // 收入类型：加上金额
                                totalAmount = totalAmount.add(detail.getAmount());
                            } else if ("扣除".equals(salaryItem.getItemType())) {
                                // 扣除类型：减去金额
                                totalAmount = totalAmount.subtract(detail.getAmount());
                            }
                        }
                    }
                }
            }
            salaryStandards.setTotalAmount(totalAmount);
        }

        // 保存薪酬标准
        baseMapper.insert(salaryStandards);

        // 保存明细
        if (salaryStandardDTO.getDetails() != null && !salaryStandardDTO.getDetails().isEmpty()) {
            saveDetails(salaryStandards.getId(), salaryStandardDTO.getDetails());
        }
    }

    /**
     * 分页查询薪酬标准
     * @param query
     * @return
     */
    @Override
    public PageDTO<SalaryStandardVO> pageQuery(SalaryStandardQuery query) {
        LambdaQueryWrapper<SalaryStandards> queryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(query.getStandardNumber())) {
            queryWrapper.like(SalaryStandards::getStandardNumber, query.getStandardNumber());
        }
        if (StringUtils.hasText(query.getStandardName())) {
            queryWrapper.like(SalaryStandards::getStandardName, query.getStandardName());
        }
        if (StringUtils.hasText(query.getStatus())) {
            queryWrapper.eq(SalaryStandards::getStatus, query.getStatus());
        }
        if (query.getStartDate() != null) {
            queryWrapper.ge(SalaryStandards::getRegisterTime, query.getStartDate());
        }
        if (query.getEndDate() != null) {
            queryWrapper.le(SalaryStandards::getRegisterTime, query.getEndDate());
        }

        Page<SalaryStandards> page = query.toMpPageSortByCreatedAtDesc();
        page = this.page(page, queryWrapper);

        PageDTO<SalaryStandardVO> pageDTO = PageDTO.of(page, SalaryStandardVO.class);

        // 填充明细信息
        List<SalaryStandardVO> voList = pageDTO.getRecords();
        for (SalaryStandardVO vo : voList) {
            fillDetails(vo);
        }

        return pageDTO;
    }

    /**
     * 根据id查询薪酬标准
     * @param id
     * @return
     */
    @Override
    public SalaryStandardVO getById(Integer id) {
        SalaryStandards salaryStandards = baseMapper.selectById(id);
        if (salaryStandards == null) {
            return null;
        }

        SalaryStandardVO vo = BeanUtil.copyProperties(salaryStandards, SalaryStandardVO.class);
        fillDetails(vo);
        return vo;
    }

    /**
     * 更新薪酬标准
     * @param salaryStandardDTO
     */
    @Override
    @Transactional
    public void update(SalaryStandardDTO salaryStandardDTO) {
        SalaryStandards salaryStandards = new SalaryStandards();
        BeanUtils.copyProperties(salaryStandardDTO, salaryStandards);

        // 计算总额（根据项目类型：收入类型加上，扣除类型减去）
        if (salaryStandardDTO.getDetails() != null && !salaryStandardDTO.getDetails().isEmpty()) {
            BigDecimal totalAmount = BigDecimal.ZERO;
            // 获取所有薪酬项目信息
            List<Integer> salaryItemIds = salaryStandardDTO.getDetails().stream()
                    .map(SalaryStandardDetailDTO::getSalaryItemId)
                    .filter(id -> id != null)
                    .collect(Collectors.toList());
            
            if (!salaryItemIds.isEmpty()) {
                List<SalaryItems> salaryItemsList = salaryItemsMapper.selectBatchIds(salaryItemIds);
                Map<Integer, SalaryItems> salaryItemsMap = salaryItemsList.stream()
                        .collect(Collectors.toMap(SalaryItems::getId, item -> item));
                
                for (SalaryStandardDetailDTO detail : salaryStandardDTO.getDetails()) {
                    if (detail.getSalaryItemId() != null && detail.getAmount() != null) {
                        SalaryItems salaryItem = salaryItemsMap.get(detail.getSalaryItemId());
                        if (salaryItem != null) {
                            if ("收入".equals(salaryItem.getItemType())) {
                                // 收入类型：加上金额
                                totalAmount = totalAmount.add(detail.getAmount());
                            } else if ("扣除".equals(salaryItem.getItemType())) {
                                // 扣除类型：减去金额
                                totalAmount = totalAmount.subtract(detail.getAmount());
                            }
                        }
                    }
                }
            }
            salaryStandards.setTotalAmount(totalAmount);
        }

        baseMapper.updateById(salaryStandards);

        // 删除旧的明细
        LambdaQueryWrapper<SalaryStandardDetails> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SalaryStandardDetails::getStandardId, salaryStandardDTO.getId());
        salaryStandardDetailsMapper.delete(deleteWrapper);

        // 保存新的明细
        if (salaryStandardDTO.getDetails() != null && !salaryStandardDTO.getDetails().isEmpty()) {
            saveDetails(salaryStandardDTO.getId(), salaryStandardDTO.getDetails());
        }
    }

    /**
     * 复核薪酬标准
     * @param id
     * @param reviewOpinion
     * @param isApproved
     */
    @Override
    @Transactional
    public void review(Integer id, String reviewOpinion, Boolean isApproved) {
        SalaryStandards salaryStandards = baseMapper.selectById(id);
        if (salaryStandards == null) {
            throw new RuntimeException("薪酬标准不存在");
        }

        Employee currentUser = employeeMapper.selectById(BaseContext.getCurrentId());
        String reviewer = currentUser != null ? currentUser.getUsername() : "admin";

        salaryStandards.setReviewedBy(reviewer);
        salaryStandards.setReviewedAt(LocalDateTime.now());
        salaryStandards.setReviewOpinion(reviewOpinion);

        if (isApproved != null && isApproved) {
            salaryStandards.setStatus("已生效");
        } else {
            salaryStandards.setStatus("已停用");
        }

        baseMapper.updateById(salaryStandards);
    }

    /**
     * 生成薪酬标准编号
     * @return
     */
    @Override
    public String generateStandardNumber() {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String prefix = "SAL" + year + month;

        LambdaQueryWrapper<SalaryStandards> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(SalaryStandards::getStandardNumber, prefix);
        queryWrapper.orderByDesc(SalaryStandards::getStandardNumber);
        queryWrapper.last("LIMIT 1");

        SalaryStandards lastStandard = baseMapper.selectOne(queryWrapper);
        if (lastStandard == null) {
            return prefix + "001";
        }

        String lastNumber = lastStandard.getStandardNumber();
        if (lastNumber.length() >= prefix.length() + 3) {
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
     * @param standardId
     * @param detailDTOList
     */
    private void saveDetails(Integer standardId, List<SalaryStandardDetailDTO> detailDTOList) {
        for (SalaryStandardDetailDTO detailDTO : detailDTOList) {
            SalaryStandardDetails details = new SalaryStandardDetails();
            details.setStandardId(standardId);
            details.setSalaryItemId(detailDTO.getSalaryItemId());
            details.setAmount(detailDTO.getAmount());
            salaryStandardDetailsMapper.insert(details);
        }
    }

    /**
     * 填充明细信息
     * @param vo
     */
    private void fillDetails(SalaryStandardVO vo) {
        LambdaQueryWrapper<SalaryStandardDetails> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryStandardDetails::getStandardId, vo.getId());
        List<SalaryStandardDetails> detailsList = salaryStandardDetailsMapper.selectList(queryWrapper);

        // 获取所有薪酬项目
        List<SalaryItems> salaryItemsList = salaryItemsMapper.selectList(null);
        Map<Integer, SalaryItems> itemsMap = salaryItemsList.stream()
                .collect(Collectors.toMap(SalaryItems::getId, item -> item));

        List<SalaryStandardDetailVO> detailVOList = detailsList.stream().map(detail -> {
            SalaryStandardDetailVO detailVO = BeanUtil.copyProperties(detail, SalaryStandardDetailVO.class);
            if (itemsMap.containsKey(detail.getSalaryItemId())) {
                SalaryItems item = itemsMap.get(detail.getSalaryItemId());
                detailVO.setItemName(item.getItemName());
                detailVO.setItemType(item.getItemType());
            }
            return detailVO;
        }).collect(Collectors.toList());

        vo.setDetails(detailVOList);
    }
}
