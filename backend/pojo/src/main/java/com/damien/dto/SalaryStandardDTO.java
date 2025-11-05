package com.damien.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SalaryStandardDTO implements Serializable {
    private Integer id;
    private String standardNumber;
    private String standardName;
    private String creator;
    private String registrar;
    private BigDecimal totalAmount;
    private String status;
    private String reviewedBy;
    private String reviewOpinion;
    private List<SalaryStandardDetailDTO> details;
}

