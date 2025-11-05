package com.damien.controller.admin;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.damien.constant.StatusConstant;
import com.damien.entity.SalaryItems;
import com.damien.mapper.SalaryItemsMapper;
import com.damien.result.Result;
import com.damien.service.ISalaryItemsService;
import com.damien.vo.SalaryItemsVO;
import org.springframework.web.bind.annotation.*;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.List;

/**
 *
 * @author Damien
 */
@RestController
@RequestMapping("/admin/salary-items")
public class SalaryItemsController {

    @Resource
    private ISalaryItemsService salaryItemsService;

    /**
     * 添加薪酬项目
     * @param salaryItems 薪酬项目
     * @return 结果
     */
    @PostMapping
    public Result<String> addSalaryItems(@RequestBody SalaryItems salaryItems) {
        salaryItemsService.addSalaryItems(salaryItems);
        return Result.success("添加成功");
    }

     /**
      * 删除薪酬项目
      * @param id 薪酬项目id
      * @return 结果
      */
    @DeleteMapping("/{id}")
    public Result<String> deleteSalaryItems(@PathVariable String id) {
        SalaryItems s = salaryItemsService.getById(id);
        s.setIsDeleted(StatusConstant.DELETED);
        salaryItemsService.updateById(s);
        return Result.success("删除成功");
    }
     /**
      * 获取薪酬项目列表
      * @return 结果
      */
    @GetMapping("/list")
    public Result<List<SalaryItemsVO>> getSalaryItemsList() {
        List<SalaryItems> salaryItemsList = salaryItemsService.list(new LambdaQueryWrapper<SalaryItems>()
                .eq(SalaryItems::getIsDeleted, StatusConstant.NO_DELETED)
                        .eq(SalaryItems::getIsActive, StatusConstant.ACTIVE)
                .orderByAsc(SalaryItems::getItemNum)
        );
        List<SalaryItemsVO> salaryItemsVOList = BeanUtil.copyToList(salaryItemsList, SalaryItemsVO.class);
        return Result.success(salaryItemsVOList);
    }

     /**
      * 更新薪酬项目
      * @param salaryItems 薪酬项目
      * @return 结果
      */
    @PutMapping
    public Result<String> updateSalaryItems(@RequestBody SalaryItems salaryItems) {
        salaryItemsService.updateById(salaryItems);
        return Result.success("更新成功");
    }

     /**
      * 根据id获取薪酬项目
      * @param id 薪酬项目id
      * @return 结果
      */
    @GetMapping("/{id}")
    public Result<SalaryItemsVO> getById(@PathVariable String id) {
        SalaryItems salaryItems = salaryItemsService.getById(id);
        SalaryItemsVO salaryItemsVO = BeanUtil.copyProperties(salaryItems, SalaryItemsVO.class);
        return Result.success(salaryItemsVO);
    }



}
