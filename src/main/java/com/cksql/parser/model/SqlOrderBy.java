package com.cksql.parser.model;

import com.cksql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** sql order by. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlOrderBy {

    private SqlExpression type;

    private List<SqlNode> items;

    public List<SqlColumn> getAllColumns() {
        Set<SqlColumn> sqlNodeSet = new HashSet<>();
        return null;
    }
}
