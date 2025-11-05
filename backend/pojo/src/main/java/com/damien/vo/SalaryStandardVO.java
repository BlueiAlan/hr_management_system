package com.damien.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SalaryStandardVO implements Serializable {
    private Integer id;
    private String standardNumber;
    private String standardName;
    private String creator;
    private String registrar;
    private LocalDateTime registerTime;
    private BigDecimal totalAmount;
    private String status;
    private String reviewedBy;
    private LocalDateTime reviewedAt;
    private String reviewOpinion;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<SalaryStandardDetailVO> details;
}

