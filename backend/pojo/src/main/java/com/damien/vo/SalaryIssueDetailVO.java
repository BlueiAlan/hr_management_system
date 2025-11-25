package com.damien.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalaryIssueDetailVO implements Serializable {
    private Integer id;
    private Integer employeeId;
    private String employeeName;
    private String archiveNumber;
    private Integer salaryStandardId;
    private String salaryStandardName;
    private BigDecimal rewardAmount;
    private BigDecimal deductionAmount;
    private BigDecimal finalAmount;
}









