package com.damien.controller.admin;

import com.damien.constant.JwtClaimsConstant;
import com.damien.dto.EmployeeDTO;
import com.damien.dto.EmployeeLoginDTO;
import com.damien.dto.PageDTO;
import com.damien.entity.Employee;
import com.damien.properties.JwtProperties;
import com.damien.query.EmployeeQuery;
import com.damien.result.Result;
import com.damien.service.IEmployeeService;
import com.damien.utils.JwtUtil;
import com.damien.vo.EmployeeLoginVO;
import com.damien.vo.EmployeeVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 * @module 管理端
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "员工相关接口")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO  员工登录DTO
     * @return  员工登录VO
     */
    @ApiOperation(value = "员工登录")
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .name(employee.getUsername())
                .token(token)
                .role(employee.getRole())
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return 退出结果
     */
    @ApiOperation(value = "员工退出")
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增员工")
    public Result<String> save(@RequestBody EmployeeDTO employeeDTO) {
        log.info("新增员工：{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success("新增员工成功");
    }

    /**
     * 员工分页查询
     * @param employeeQuery
     * @return
     */
    @GetMapping("/page")
    @ApiOperation(value = "员工分页查询")
    public Result<PageDTO<EmployeeVO>> page(EmployeeQuery employeeQuery) {
        log.info("员工分页查询，参数为：{}", employeeQuery);
        PageDTO<EmployeeVO> pageResult = employeeService.pageQuery(employeeQuery);
        return Result.success(pageResult);
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查询员工")
    public Result<EmployeeVO> getById(@PathVariable Integer id) {
        EmployeeVO employeeVO = employeeService.getById(id);
        return Result.success(employeeVO);
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation(value = "编辑员工信息")
    public Result<String> update(@RequestBody EmployeeDTO employeeDTO) {
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success("编辑员工信息成功");
    }

    /**
     * 删除员工
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除员工")
    public Result<String> deleteById(@PathVariable Integer id) {
        log.info("删除员工，id：{}", id);
        employeeService.deleteById(id);
        return Result.success("删除员工成功");
    }

    /**
     * 复核员工
     * @param id
     * @param reviewOpinion
     * @return
     */
    @PostMapping("/review/{id}")
    @ApiOperation(value = "复核员工")
    public Result<String> review(@PathVariable Integer id, @RequestParam(required = false) String reviewOpinion) {
        log.info("复核员工，id：{}，复核意见：{}", id, reviewOpinion);
        employeeService.review(id, reviewOpinion);
        return Result.success("复核成功");
    }

}
