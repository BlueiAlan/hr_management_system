package com.damien.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String archiveNumber;

    private String username;

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

    private Integer salaryStandardId;

    private String salaryStandardName;

    private String hobbies;

    private String resume;

    private String familyInfo;

    private String remarks;

    private String photoPath;

    private Integer orgId;

    private String orgName;

    private Integer positionId;

    private String positionName;

    private String title;

    private Integer role;

    private String status;

    private String createdBy;

    private LocalDateTime createdAt;

    private String reviewedBy;

    private LocalDateTime reviewedAt;

    private LocalDateTime updatedAt;

    private String deleteMsg;

    private LocalDateTime deletedAt;

}
