package com.damien.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.models.security.SecurityScheme;
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
@TableName("employee")
@ApiModel(value="Employee对象", description="")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "档案编号")
    private String archiveNumber;

    @ApiModelProperty(value = "姓名")
    private String username;

    private String password;

    @ApiModelProperty(value = "删除原因")
    private String deleteMsg;

    @ApiModelProperty(value = "删除时间")
    private LocalDateTime deletedAt;

    /**
     * 角色权限：0-超级管理员，1-人事专员，2-人事经理，3-薪酬专员，4-薪酬经理
     */
    @ApiModelProperty(value = "角色权限")
    private Integer role;

    @ApiModelProperty(value = "性别")
    private String gender;

    private String email;

    private String phone;

    private String qq;

    private String mobile;

    private String address;

    private String postalCode;

    private String nationality;

    private String birthPlace;

    private LocalDate birthDate;

    private String ethnicity;

    private String religion;

    private String politicalStatus;

    private String idCard;

    private Integer age;

    private String education;

    @ApiModelProperty(value = "薪酬标准ID")
    private Integer salaryStandardId;

    private String hobbies;

    @ApiModelProperty(value = "个人履历")
    private String resume;

    @ApiModelProperty(value = "家庭关系信息")
    private String familyInfo;

    @ApiModelProperty(value = "备注")
    private String remarks;

    @ApiModelProperty(value = "照片路径")
    private String photoPath;

    @ApiModelProperty(value = "所属三级机构ID")
    private Integer orgId;

    @ApiModelProperty(value = "职位ID")
    private Integer positionId;

    @ApiModelProperty(value = "职称")
    private String title;

    private String status;

    @ApiModelProperty(value = "登记人")
    private String createdBy;

    private LocalDateTime createdAt;

    @ApiModelProperty(value = "复核人")
    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime updatedAt;


}
