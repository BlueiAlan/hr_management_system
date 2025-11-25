package com.damien.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SalaryStandardDetailVO implements Serializable {
    private Integer id;
    private Integer salaryItemId;
    private String itemName;
    private String itemType;
    private BigDecimal amount;
}









