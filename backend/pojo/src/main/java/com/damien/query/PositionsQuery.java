package com.damien.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class PositionsQuery extends PageQuery{
    private String positionName;

}
