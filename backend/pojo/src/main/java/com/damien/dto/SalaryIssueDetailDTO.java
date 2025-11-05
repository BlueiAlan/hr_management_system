package com.damien.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalaryIssueDetailDTO implements Serializable {
    private Integer id;
    private Integer employeeId;
    private Integer salaryStandardId;
    private BigDecimal rewardAmount;
    private BigDecimal deductionAmount;
    private BigDecimal finalAmount;
}

