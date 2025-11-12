package com.damien.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalaryIssueVO implements Serializable {
    private Integer id;
    private String issueNumber;
    private Integer orgId;
    private String orgName;
    private LocalDate issueDate;
    private Integer totalEmployees;
    private BigDecimal totalAmount;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private List<SalaryIssueDetailVO> details;
}




