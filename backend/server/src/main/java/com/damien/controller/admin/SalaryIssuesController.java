package com.damien.controller.admin;

import com.damien.dto.PageDTO;
import com.damien.dto.SalaryIssueDTO;
import com.damien.query.SalaryIssueQuery;
import com.damien.result.Result;
import com.damien.service.ISalaryIssuesService;
import com.damien.vo.SalaryIssueVO;
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
@RequestMapping("/admin/salary-issues")
@Api(tags = "薪酬发放管理")
@Slf4j
public class SalaryIssuesController {

    @Autowired
    private ISalaryIssuesService salaryIssuesService;

    /**
     * 新增薪酬发放
     * @param salaryIssueDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增薪酬发放")
    public Result<String> save(@RequestBody SalaryIssueDTO salaryIssueDTO) {
        log.info("新增薪酬发放：{}", salaryIssueDTO);
        salaryIssuesService.save(salaryIssueDTO);
        return Result.success("新增薪酬发放成功");
    }

    /**
     * 分页查询薪酬发放
     * @param query
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "分页查询薪酬发放")
    public Result<PageDTO<SalaryIssueVO>> page(SalaryIssueQuery query) {
        log.info("分页查询薪酬发放，参数：{}", query);
        PageDTO<SalaryIssueVO> pageResult = salaryIssuesService.pageQuery(query);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询薪酬发放
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询薪酬发放")
    public Result<SalaryIssueVO> getById(@PathVariable Integer id) {
        SalaryIssueVO vo = salaryIssuesService.getById(id);
        return Result.success(vo);
    }

    /**
     * 更新薪酬发放
     * @param salaryIssueDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新薪酬发放")
    public Result<String> update(@RequestBody SalaryIssueDTO salaryIssueDTO) {
        log.info("更新薪酬发放：{}", salaryIssueDTO);
        salaryIssuesService.update(salaryIssueDTO);
        return Result.success("更新薪酬发放成功");
    }

    /**
     * 复核薪酬发放
     * @param id
     * @param reviewOpinion
     * @param isApproved
     * @return
     */
    @PostMapping("/review/{id}")
    @ApiOperation(value = "复核薪酬发放")
    public Result<String> review(@PathVariable Integer id,
                                 @RequestParam(required = false) String reviewOpinion,
                                 @RequestParam Boolean isApproved) {
        log.info("复核薪酬发放，id：{}，复核意见：{}，是否通过：{}", id, reviewOpinion, isApproved);
        salaryIssuesService.review(id, reviewOpinion, isApproved);
        return Result.success("复核成功");
    }

    /**
     * 发放薪酬
     * @param id
     * @return
     */
    @PostMapping("/issue/{id}")
    @ApiOperation(value = "发放薪酬")
    public Result<String> issue(@PathVariable Integer id) {
        log.info("发放薪酬，id：{}", id);
        salaryIssuesService.issue(id);
        return Result.success("发放成功");
    }

    /**
     * 生成薪酬单号
     * @return
     */
    @GetMapping("/generate-number")
    @ApiOperation(value = "生成薪酬单号")
    public Result<String> generateNumber() {
        String number = salaryIssuesService.generateIssueNumber();
        return Result.success(number);
    }
}
