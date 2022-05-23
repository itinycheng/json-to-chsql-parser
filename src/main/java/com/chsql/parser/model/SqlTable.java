package com.chsql.parser.model;

import com.chsql.parser.common.SqlContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

import static com.chsql.parser.common.Constant.DOT;
import static com.chsql.parser.util.SqlUtil.tableIdent;

/** sql table. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlTable extends SqlNode {

    private String id;

    private String database;

    private String name;

    @Override
    public String ident() {
        return tableIdent(id);
    }

    @Override
    public List<SqlColumn> getColumns() {
        return Collections.emptyList();
    }

    @Override
    public boolean isValid(SqlContext context) {
        return StringUtils.isNoneBlank(id)
                && StringUtils.isNoneBlank(database)
                && StringUtils.isNoneBlank(name);
    }

    public String toSQL(SqlContext sqlContext, Object... relation) {
        return String.join(DOT, database, name);
    }
}
