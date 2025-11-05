package com.damien.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class SalaryIssueDTO implements Serializable {
    private Integer id;
    private String issueNumber;
    private Integer orgId;
    private LocalDate issueDate;
    private Integer totalEmployees;
    private BigDecimal totalAmount;
    private String status;
    private String createdBy;
    private List<SalaryIssueDetailDTO> details;
}

