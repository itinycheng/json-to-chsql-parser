package com.chsql.parser;

import com.chsql.parser.common.SqlContext;
import com.chsql.parser.model.SqlSelect;
import lombok.AllArgsConstructor;

/** sql validate. */
@AllArgsConstructor
public class SqlValidator {

    private final SqlContext context;

    public boolean validate(SqlSelect sqlSelect) {
        return true;
    }
}
