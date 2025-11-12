package com.damien.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDate;
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
@TableName("salary_standards")
@ApiModel(value="SalaryStandards对象", description="")
public class SalaryStandards implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Integer id;

    @ApiModelProperty(value = "薪酬标准编号")
    private String standardNumber;

    @ApiModelProperty(value = "薪酬标准名称")
    private String standardName;

    @ApiModelProperty(value = "制定人")
    private String creator;

    @ApiModelProperty(value = "登记人")
    private String registrar;

    @ApiModelProperty(value = "登记时间")
    private LocalDate registerTime;

    @ApiModelProperty(value = "薪酬总额")
    private BigDecimal totalAmount;

    private String status;

    @ApiModelProperty(value = "复核人")
    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @ApiModelProperty(value = "复核意见")
    private String reviewOpinion;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
