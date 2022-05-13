package com.cksql.parser.model;

import com.cksql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** sql order by. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlOrderBy {

    private SqlExpression type;

    private List<SqlColumn> items;
}
