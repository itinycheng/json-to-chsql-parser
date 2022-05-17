package com.cksql.parser;

import com.cksql.parser.model.SqlSelect;
import lombok.AllArgsConstructor;
import lombok.Data;

/** sql validate. */
@Data
@AllArgsConstructor
public class SqlValidator {

    private final SqlSelect select;

    public boolean validate() {
        return true;
    }
}
