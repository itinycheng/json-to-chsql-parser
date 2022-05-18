package com.cksql.parser;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.model.SqlSelect;
import lombok.AllArgsConstructor;

/** sql validate. */
@AllArgsConstructor
public class SqlValidator {

    private final SqlContext context;

    public boolean validate(SqlSelect sqlSelect) {
        return true;
    }
}
