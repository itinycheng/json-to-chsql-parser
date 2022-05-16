package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.type.DataType;
import com.cksql.parser.type.LogicalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import static com.cksql.parser.common.Constant.COMMA;
import static com.cksql.parser.common.Constant.EMPTY;
import static java.util.stream.Collectors.joining;

/** sql literal. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlLiteral extends SqlNode {

    private String[] values;

    public String toSQL(DataType dataType, boolean isQuoteEnabled) {
        LogicalType logicalType = dataType.getLogicalType();
        return Arrays.stream(values)
                .map(
                        item -> {
                            if (isQuoteEnabled) {
                                return String.join(
                                        EMPTY, logicalType.quote, item, logicalType.quote);
                            } else {
                                return item;
                            }
                        })
                .collect(joining(COMMA));
    }

    @Override
    public boolean isValid(SqlContext context) {
        return ArrayUtils.isNotEmpty(values) && super.isValid(context);
    }
}
