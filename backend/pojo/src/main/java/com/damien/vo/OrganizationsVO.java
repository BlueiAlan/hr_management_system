package com.damien.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationsVO implements Serializable {

    private Integer id;

    @ApiModelProperty(value = "机构名称")
    private String orgName;

    @ApiModelProperty(value = "机构级别：1-一级机构，2-二级机构，3-三级机构")
    private Integer orgLevel;

    @ApiModelProperty(value = "父级机构ID")
    private Integer parentId;

    @ApiModelProperty(value = "父级机构名称（用于二级机构显示一级机构）")
    private String parentOrgName;

    @ApiModelProperty(value = "所属机构路径（用于三级机构显示：一级/二级）")
    private String fromOrg;

    private LocalDateTime createdAt;

}
