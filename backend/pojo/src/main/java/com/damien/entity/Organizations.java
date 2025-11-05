package com.damien.entity;


import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("organizations")
@ApiModel(value="Organizations对象", description="")
public class Organizations implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "机构编码")
    private String orgCode;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "机构级别：1-一级机构，2-二级机构，3-三级机构")
    private Integer orgLevel;

    @ApiModelProperty(value = "父级机构ID")
    private Integer parentId;

    @ApiModelProperty(value = "是否删除：0-未删除，1-已删除")
    private Integer isDeleted;


    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
