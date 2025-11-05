package com.damien.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class EmployeeDTO implements Serializable {

    private Integer id;

    private String archiveNumber;

    private String username;

    private String password;

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

    private String hobbies;

    private String resume;

    private String familyInfo;

    private String remarks;

    private String photoPath;

    private Integer orgId;

    private Integer positionId;

    private String title;

    private Integer role;

    private String status;

    private String createdBy;

}
