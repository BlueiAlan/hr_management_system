package com.damien.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SalaryIssueQuery extends PageQuery {
    private String issueNumber;
    private Integer orgId;
    private String status;
}




