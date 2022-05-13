package com.cksql.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/** composite sql where. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompositeSqlWhere extends SqlWhere {

    private String relation;

    private List<SqlWhere> conditions;
}
