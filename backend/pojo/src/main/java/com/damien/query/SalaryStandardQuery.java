package com.damien.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalaryStandardQuery extends PageQuery {
    private String standardNumber;
    private String standardName;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}



