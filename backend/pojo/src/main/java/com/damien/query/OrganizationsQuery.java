package com.damien.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
public class OrganizationsQuery extends PageQuery {
    private String orgName;
    private Integer orgLevel;
}
