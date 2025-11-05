package com.damien.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalaryStandardQuery extends PageQuery {
    private String standardNumber;
    private String standardName;
    private String status;
}

