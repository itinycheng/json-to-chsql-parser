package com.cksql.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** sql select. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlSelect {

    private List<SqlNode> select;

    private List<SqlTable> from;

    private SqlWhere where;

    private SqlOrderBy orderBy;

    private List<SqlNode> groupBy;

    private SqlLimit limit;
}
