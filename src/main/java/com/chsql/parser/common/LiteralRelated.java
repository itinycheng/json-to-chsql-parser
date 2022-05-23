package com.chsql.parser.common;

import com.chsql.parser.model.SqlLiteral;
import com.chsql.parser.type.DataType;
import lombok.Data;

/** Refer to: {@link SqlLiteral}. */
@Data
public class LiteralRelated {

    private final DataType dataType;

    private final boolean isQuoteEnabled;
}
