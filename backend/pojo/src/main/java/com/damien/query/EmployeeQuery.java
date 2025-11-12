package com.damien.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployeeQuery extends PageQuery {
    private String username;
    private String archiveNumber;
    private Integer orgId;
    private Integer positionId;
    private String status;
    // 建档时间查询：起止日期
    private LocalDate startDate;
    private LocalDate endDate;
}


