package com.damien.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("salary_issue_details")
@ApiModel(value="SalaryIssueDetails对象", description="")
public class SalaryIssueDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    private Integer issueId;

    private Integer employeeId;

    private Integer salaryStandardId;

    @ApiModelProperty(value = "奖励金额")
    private BigDecimal rewardAmount;

    @ApiModelProperty(value = "应扣金额")
    private BigDecimal deductionAmount;

    @ApiModelProperty(value = "实发金额")
    private BigDecimal finalAmount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
