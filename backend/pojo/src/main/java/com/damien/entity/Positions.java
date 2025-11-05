package com.damien.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
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
@TableName("positions")
@ApiModel(value="Positions对象", description="")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Positions implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "职位名称")
    private String positionName;

    @ApiModelProperty(value = "所属三级机构ID")
    private Integer orgId;

    @ApiModelProperty(value = "职位描述")
    private String description;

    @ApiModelProperty(value = "是否删除, 0-未删除, 1-已删除")
    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
