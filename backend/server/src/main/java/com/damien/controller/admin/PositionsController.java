package com.damien.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.damien.constant.StatusConstant;
import com.damien.dto.PageDTO;
import com.damien.entity.Positions;
import com.damien.query.PositionsQuery;
import com.damien.result.Result;
import com.damien.service.IPositionsService;
import com.damien.vo.PositionsVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Damien
 */
@RestController
@RequestMapping("/admin/positions")
public class PositionsController {
    @Resource
    private IPositionsService positionsService;

    /**
     * 创建职位
     * @param positions  职位实体
     * @return  创建结果
     */
    @PostMapping
    public Result<String> createPosition(@RequestBody Positions positions) {
        positionsService.save(positions);
        return Result.success("创建成功");
    }

    /**
     * 更新职位
     * @param positions  职位实体
     * @return  更新结果
     */
    @PutMapping
    public Result<String> updatePosition(@RequestBody Positions positions) {
        positionsService.updateById(positions);
        return Result.success("更新成功");
    }

     /**
      * 删除职位
      * @param id  职位ID
      * @return  删除结果
      */
    @DeleteMapping("/{id}")
    public Result<String> deletePosition(@PathVariable Integer id) {
        positionsService.updateById(Positions.builder().id(id).isDeleted(1).build());
        return Result.success("删除成功");
    }

    /**
     * 获取所有职位
     * @return  职位列表
     */
    @GetMapping
    public Result<List<PositionsVO>> listPositions() {
        List<PositionsVO> positionsVOList = positionsService.selectList();

        return Result.success(positionsVOList);
    }

    /**
     * 根据ID获取职位详情
     * @param id  职位ID
     * @return  职位详情
     */
    @GetMapping("/{id}")
    public Result<PositionsVO> getPositionById(@PathVariable Integer id) {
        Positions positions = positionsService.getById(id);
        PositionsVO positionsVO = BeanUtil.copyProperties(positions, PositionsVO.class);
        return Result.success(positionsVO);
    }

    /**
     * 获取职位分页列表
     * @param positionsQuery  职位查询参数
     * @return  职位分页列表
     */
    @GetMapping("/page")
    public Result<PageDTO<PositionsVO>> getPositionPage( PositionsQuery positionsQuery) {
        PageDTO<PositionsVO> p = positionsService.queryPage(positionsQuery);
        return Result.success(p);
    }
}
