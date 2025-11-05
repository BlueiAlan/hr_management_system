package com.damien.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeQuery extends PageQuery {
    private String username;
    private String archiveNumber;
    private Integer orgId;
    private Integer positionId;
    private String status;
}

