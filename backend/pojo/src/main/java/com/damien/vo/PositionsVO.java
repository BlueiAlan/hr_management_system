package com.damien.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "职位名称")
    private String positionName;

    @ApiModelProperty(value = "所属三级机构ID")
    private Integer orgId;

    @ApiModelProperty(value = "职位描述")
    private String description;

    @ApiModelProperty(value = "所属机构")
    private String fromOrg;

}
