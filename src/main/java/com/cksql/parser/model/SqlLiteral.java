package com.cksql.parser.model;

import com.cksql.parser.common.LiteralRelated;
import com.cksql.parser.common.SqlContext;
import com.cksql.parser.type.LogicalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.cksql.parser.common.Constant.COMMA;
import static com.cksql.parser.common.Constant.EMPTY;
import static com.cksql.parser.common.Constant.UNDERSCORE;
import static java.util.stream.Collectors.joining;

/** sql literal. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlLiteral extends SqlNode {

    private String[] values;

    @Override
    public String ident() {
        return String.join(UNDERSCORE, values);
    }

    @Override
    public List<SqlColumn> getColumns() {
        return Collections.emptyList();
    }

    @Override
    public boolean isValid(SqlContext context) {
        return ArrayUtils.isNotEmpty(values);
    }

    @Override
    public String toSQL(SqlContext context, Object... relation) {
        if (ArrayUtils.isEmpty(relation) || !(relation[0] instanceof LiteralRelated)) {
            throw new RuntimeException("Missing info to parse SqlLiteral to SQL.");
        }

        LiteralRelated related = (LiteralRelated) relation[0];
        LogicalType logicalType = related.getDataType().getLogicalType();
        boolean quoteEnabled = related.isQuoteEnabled();
        return Arrays.stream(values)
                .map(
                        item -> {
                            if (quoteEnabled) {
                                return String.join(
                                        EMPTY, logicalType.quote, item, logicalType.quote);
                            } else {
                                return item;
                            }
                        })
                .collect(joining(COMMA));
    }
}
