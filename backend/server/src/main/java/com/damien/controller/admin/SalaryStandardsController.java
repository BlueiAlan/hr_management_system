package com.damien.controller.admin;

import com.damien.dto.PageDTO;
import com.damien.dto.SalaryStandardDTO;
import com.damien.query.SalaryStandardQuery;
import com.damien.result.Result;
import com.damien.service.ISalaryStandardsService;
import com.damien.vo.SalaryStandardVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
@RestController
@RequestMapping("/admin/salary-standards")
@Api(tags = "薪酬标准管理")
@Slf4j
public class SalaryStandardsController {

    @Autowired
    private ISalaryStandardsService salaryStandardsService;

    /**
     * 新增薪酬标准
     * @param salaryStandardDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增薪酬标准")
    public Result<String> save(@RequestBody SalaryStandardDTO salaryStandardDTO) {
        log.info("新增薪酬标准：{}", salaryStandardDTO);
        salaryStandardsService.save(salaryStandardDTO);
        return Result.success("新增薪酬标准成功");
    }

    /**
     * 分页查询薪酬标准
     * @param query
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询薪酬标准")
    public Result<PageDTO<SalaryStandardVO>> page(SalaryStandardQuery query) {
        log.info("分页查询薪酬标准，参数：{}", query);
        PageDTO<SalaryStandardVO> pageResult = salaryStandardsService.pageQuery(query);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询薪酬标准
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询薪酬标准")
    public Result<SalaryStandardVO> getById(@PathVariable Integer id) {
        SalaryStandardVO vo = salaryStandardsService.getById(id);
        return Result.success(vo);
    }

    /**
     * 更新薪酬标准
     * @param salaryStandardDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新薪酬标准")
    public Result<String> update(@RequestBody SalaryStandardDTO salaryStandardDTO) {
        log.info("更新薪酬标准：{}", salaryStandardDTO);
        salaryStandardsService.update(salaryStandardDTO);
        return Result.success("更新薪酬标准成功");
    }

    /**
     * 复核薪酬标准
     * @param id
     * @param reviewOpinion
     * @param isApproved
     * @return
     */
    @PostMapping("/review/{id}")
    @ApiOperation(value = "复核薪酬标准")
    public Result<String> review(@PathVariable Integer id,
                                  @RequestParam(required = false) String reviewOpinion,
                                  @RequestParam Boolean isApproved) {
        log.info("复核薪酬标准，id：{}，复核意见：{}，是否通过：{}", id, reviewOpinion, isApproved);
        salaryStandardsService.review(id, reviewOpinion, isApproved);
        return Result.success("复核成功");
    }

    /**
     * 生成薪酬标准编号
     * @return
     */
    @GetMapping("/generate-number")
    @ApiOperation(value = "生成薪酬标准编号")
    public Result<String> generateNumber() {
        String number = salaryStandardsService.generateStandardNumber();
        return Result.success(number);
    }
}
