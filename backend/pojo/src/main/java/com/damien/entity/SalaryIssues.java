package com.damien.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Damien
 * @since 2025-10-31
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("salary_issues")
@ApiModel(value="SalaryIssues对象", description="")
public class SalaryIssues implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty(value = "薪酬单号")
    private String issueNumber;

    @ApiModelProperty(value = "发放机构ID")
    private Integer orgId;

    @ApiModelProperty(value = "发放日期")
    private LocalDate issueDate;

    @ApiModelProperty(value = "总人数")
    private Integer totalEmployees;

    @ApiModelProperty(value = "总额")
    private BigDecimal totalAmount;

    private String status;

    @ApiModelProperty(value = "登记人")
    private String createdBy;

    private LocalDateTime createdAt;

    @ApiModelProperty(value = "复核人")
    private String reviewedBy;

    private LocalDateTime reviewedAt;


}
