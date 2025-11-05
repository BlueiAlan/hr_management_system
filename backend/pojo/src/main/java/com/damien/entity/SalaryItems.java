package com.damien.entity;

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
@TableName("salary_items")
@ApiModel(value="SalaryItems对象", description="")
public class SalaryItems implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "薪酬项目编号")
    private String itemNum;

    @ApiModelProperty(value = "薪酬项目名称")
    private String itemName;

    @ApiModelProperty(value = "项目类型")
    private String itemType;

    @ApiModelProperty(value = "计算规则")
    private String calculationRule;

    @ApiModelProperty(value = "是否启用")
    private Boolean isActive;

    @ApiModelProperty(value = "是否删除")
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
