package com.cksql.parser.common;

import com.cksql.parser.model.SqlLiteral;
import com.cksql.parser.type.DataType;
import lombok.Data;

/** Refer to: {@link SqlLiteral}. */
@Data
public class LiteralRelated {

    private final DataType dataType;

    private final boolean isQuoteEnabled;
}
