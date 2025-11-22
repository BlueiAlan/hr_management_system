package com.damien.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.constant.StatusConstant;
import com.damien.dto.OrganizationsDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Organizations;
import com.damien.mapper.OrganizationsMapper;
import com.damien.query.OrganizationsQuery;
import com.damien.service.IOrganizationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.damien.vo.OrganizationsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Damien
 */
@Service
public class OrganizationsServiceImpl extends ServiceImpl<OrganizationsMapper, Organizations> implements IOrganizationsService {

    /**
     * 添加组织
     * @param organizationsDTO 组织DTO
     */
    @Override
    @Transactional
    public void addOrg(OrganizationsDTO organizationsDTO) {
        Organizations organizations = new Organizations();
        BeanUtils.copyProperties(organizationsDTO, organizations);
        if (organizationsDTO.getId() == null) {
            organizations.setOrgCode(generateOrgCode(null, organizations.getOrgLevel()));
        }
        organizations.setOrgCode(generateOrgCode(organizations.getParentId(), organizations.getOrgLevel()));
        baseMapper.insert(organizations);
    }

    /**
     * 查询组织分页列表
     * @param query 组织查询参数
     * @return 组织分页列表
     */
    @Override
    public PageDTO<OrganizationsVO> queryPage(OrganizationsQuery query) {
        LambdaQueryWrapper<Organizations> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Organizations::getIsDeleted, 0)
                .eq(Organizations::getOrgLevel, query.getOrgLevel())
                .orderByDesc(Organizations::getCreatedAt);

        if (query.getOrgName() != null) {
            queryWrapper.like(Organizations::getOrgName, query.getOrgName());
        }
        Page<Organizations> page = new Page<>(query.getPageNum(), query.getPageSize());
        page = this.page(page, queryWrapper);

        PageDTO<OrganizationsVO> pageDTO = PageDTO.of(page, OrganizationsVO.class);
        
        // 填充父级机构名称
        if (query.getOrgLevel() == 2 || query.getOrgLevel() == 3) {
            fillParentOrgName(pageDTO.getRecords(), query.getOrgLevel());
        }
        
        return pageDTO;
    }

    /**
     * 填充父级机构名称
     * @param organizationsVOList 机构VO列表
     * @param orgLevel 机构级别
     */
    private void fillParentOrgName(List<OrganizationsVO> organizationsVOList, Integer orgLevel) {
        if (organizationsVOList == null || organizationsVOList.isEmpty()) {
            return;
        }

        // 获取所有机构映射
        Map<Integer, Organizations> orgMap = baseMapper.selectList(new LambdaQueryWrapper<Organizations>()
                        .eq(Organizations::getIsDeleted, StatusConstant.NO_DELETED))
                .stream()
                .collect(Collectors.toMap(Organizations::getId, o -> o));

        for (OrganizationsVO orgVO : organizationsVOList) {
            if (orgVO.getParentId() == null) {
                continue;
            }

            Organizations parentOrg = orgMap.get(orgVO.getParentId());
            if (parentOrg == null) {
                continue;
            }

            if (orgLevel == 2) {
                // 二级机构：显示一级机构名称
                orgVO.setParentOrgName(parentOrg.getOrgName());
            } else if (orgLevel == 3) {
                // 三级机构：显示二级/一级机构路径
                Organizations secondOrg = parentOrg; // 二级机构
                Organizations firstOrg = orgMap.get(secondOrg.getParentId()); // 一级机构
                
                if (firstOrg != null) {
                    orgVO.setFromOrg(secondOrg.getOrgName() + "/" + firstOrg.getOrgName());
                } else {
                    orgVO.setFromOrg(secondOrg.getOrgName());
                }
            }
        }
    }

    /**
     * 生成组织编码
     * @param parentId 父级机构ID
     * @param orgLevel 机构级别
     * @return 组织编码
     */
    private String generateOrgCode(Integer parentId, Integer orgLevel) {
        long count = baseMapper.selectCount(new LambdaQueryWrapper<Organizations>()
                .eq(Organizations::getOrgLevel, orgLevel)
        .eq(Organizations::getParentId, parentId));
        int code = Math.toIntExact(count) + 1;
        String orgCode = "";
        if ( code < 10){
            orgCode = "0" + code;
        }

        if (parentId == null) {
            return orgCode;
        }
        String parentCode = baseMapper.selectById(parentId).getOrgCode();
        return parentCode + orgCode;
    }
}
