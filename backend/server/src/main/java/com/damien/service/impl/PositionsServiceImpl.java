package com.damien.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.constant.StatusConstant;
import com.damien.dto.PageDTO;
import com.damien.entity.Organizations;
import com.damien.entity.Positions;
import com.damien.mapper.OrganizationsMapper;
import com.damien.mapper.PositionsMapper;
import com.damien.query.PositionsQuery;
import com.damien.service.IPositionsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.damien.vo.PositionsVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class PositionsServiceImpl extends ServiceImpl<PositionsMapper, Positions> implements IPositionsService {

    private static final String SEPARATOR = "/";

    @Resource
    private OrganizationsMapper organizationsMapper;

    /**
     * 查询所有岗位信息，包括机构名称
     * @return 岗位信息列表
     */
    @Override
    public List<PositionsVO> selectList() {
        List<Positions> list = baseMapper.selectList(new LambdaQueryWrapper<Positions>()
                .eq(Positions::getIsDeleted, StatusConstant.NO_DELETED));
        List<PositionsVO> positionsVOList = BeanUtil.copyToList(list, PositionsVO.class);

        return getFromOrgName(positionsVOList);
    }

    /**
     * 查询岗位分页列表，包括机构名称
     * @param positionsQuery 岗位查询参数
     * @return 岗位分页列表
     */
    @Override
    public PageDTO<PositionsVO> queryPage(PositionsQuery positionsQuery) {
        // 构建查询条件
        LambdaQueryWrapper<Positions> queryWrapper = new LambdaQueryWrapper<>();
        if (positionsQuery.getPositionName() != null) {
            queryWrapper.like(Positions::getPositionName, positionsQuery.getPositionName());
        }
        queryWrapper.eq(Positions::getIsDeleted, StatusConstant.NO_DELETED);

        // 执行分页查询
        Page<Positions> p = positionsQuery.toMpPageSortByCreatedAtDesc();
        Page<Positions>  page = this.page(p, queryWrapper);

        // 转换为VO列表并添加机构名称
        PageDTO<PositionsVO> pageDTO = PageDTO.of(page, PositionsVO.class);
        List<PositionsVO> positionsVOList = getFromOrgName(pageDTO.getRecords());
        // 设置分页信息
        pageDTO.setRecords(positionsVOList);

        return pageDTO;
    }

    /**
     * 根据机构ID获取机构名称
     * @param positionsVOList 岗位信息列表
     * @return 包含机构名称的岗位信息列表
     */
    private List<PositionsVO> getFromOrgName(List<PositionsVO> positionsVOList) {

        Map<Integer, Organizations> orgMap = organizationsMapper.selectList(new LambdaQueryWrapper<Organizations>()
                        .eq(Organizations::getIsDeleted, StatusConstant.NO_DELETED))
                .stream()
                .collect(Collectors.toMap(Organizations::getId, o -> o));

        for (PositionsVO positionsVO : positionsVOList) {
            int thirdOrgId = positionsVO.getOrgId();
            int secondOrgId = orgMap.get(thirdOrgId).getParentId();
            int firstOrgId = orgMap.get(secondOrgId).getParentId();

            String name = orgMap.get(firstOrgId).getOrgName() + SEPARATOR +
                    orgMap.get(secondOrgId).getOrgName() + SEPARATOR +
                    orgMap.get(thirdOrgId).getOrgName();
            positionsVO.setFromOrg(name);
        }
        return positionsVOList;
    }
}
