package com.damien.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.dto.OrganizationsDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Organizations;
import com.damien.mapper.OrganizationsMapper;
import com.damien.query.OrganizationsQuery;
import com.damien.query.PageQuery;
import com.damien.service.IOrganizationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.damien.vo.OrganizationsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return PageDTO.of(page, OrganizationsVO.class);
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
