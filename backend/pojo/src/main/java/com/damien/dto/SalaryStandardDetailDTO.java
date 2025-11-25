package com.damien.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalaryStandardDetailDTO implements Serializable {
    private Integer id;
    private Integer salaryItemId;
    private BigDecimal amount;
}









