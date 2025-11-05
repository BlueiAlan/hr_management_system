package com.damien.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.dto.OrganizationsDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Organizations;
import com.damien.query.OrganizationsQuery;
import com.damien.result.Result;
import com.damien.service.IOrganizationsService;
import com.damien.vo.OrganizationsVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Damien
 */
@RestController
@RequestMapping("/admin/organizations")
public class OrganizationsController {

    @Resource
    private IOrganizationsService organizationsService;

    /**
     * 添加组织
     * @param organizationsDTO  组织DTO
     * @return  添加结果
     */
    @PostMapping
    public Result<String> addOrg(@RequestBody OrganizationsDTO organizationsDTO){
        organizationsService.addOrg(organizationsDTO);
        return Result.success("添加成功");
    }

    /**
     * 根据组织等级查询组织列表
     * @param organizationsDTO  组织DTO
     * @return  组织VO列表
     */
    @PostMapping("/list")
    public Result<List<OrganizationsVO>> listByOrg(@RequestBody OrganizationsDTO organizationsDTO){
        List<Organizations> organizationsList = organizationsService.list(new LambdaQueryWrapper<Organizations>()
                .eq(Organizations::getOrgLevel, organizationsDTO.getOrgLevel())
);

        List<OrganizationsVO> organizationsVOList = BeanUtil.copyToList(organizationsList, OrganizationsVO.class);
        return Result.success(organizationsVOList);
    }

    /**
     * 查询所有组织
     * @return  组织VO列表
     */
    @GetMapping("/list")
    public Result<List<OrganizationsVO>> getAllOrg(){
        List<Organizations> organizationsList = organizationsService.list();
        List<OrganizationsVO> organizationsVOList = BeanUtil.copyToList(organizationsList, OrganizationsVO.class);
        return Result.success(organizationsVOList);
    }

    /**
     * 更新组织
     * @param organizations  组织实体
     * @return  更新结果
     */
    @PutMapping
    public Result<String> updateOrg(@RequestBody Organizations organizations){
        organizationsService.updateById(organizations);
        return Result.success("更新成功");
    }

     /**
     * 根据ID查询组织详情
     * @param id  组织ID
     * @return  组织详情
     */
    @GetMapping("/{id}")
    public Result<OrganizationsVO> getOrgById(@PathVariable Integer id){
        Organizations organizations = organizationsService.getById(id);
        OrganizationsVO organizationsVO = BeanUtil.copyProperties(organizations, OrganizationsVO.class);
        return Result.success(organizationsVO);
    }

    /**
     * 查询组织分页列表
     * @param query 组织查询参数
     * @return 组织分页列表
     */
    @GetMapping("/page")
    public Result<PageDTO<OrganizationsVO>> page(OrganizationsQuery query){
        PageDTO<OrganizationsVO> page = organizationsService.queryPage(query);
        return Result.success(page);
    }
}
