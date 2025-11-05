package com.damien.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SalaryItemsVO implements Serializable {

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
    private Integer isActive;

    private LocalDateTime createdAt;



}
