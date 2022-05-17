package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.snippet.SqlExpression;
import com.cksql.parser.type.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cksql.parser.common.Constant.EMPTY;

/**
 * Simple sql where. <br>
 * TODO: operands not null, 1=< length <= 2
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SimpleSqlWhere extends SqlWhere {

    private SqlExpression operator;

    private SqlNode[] operands;

    @Override
    public String toSQL(SqlContext context) {
        if (ArrayUtils.isEmpty(operands)) {
            return EMPTY;
        }

        Map<Integer, SqlNode> operandMap =
                IntStream.range(0, operands.length)
                        .mapToObj(idx -> Pair.of(idx, operands[idx]))
                        .collect(Collectors.toMap(Pair::getLeft, Pair::getRight));
        Integer firstSqlColumnIdx =
                operandMap.entrySet().stream()
                        .filter(entry -> containsSqlColumn(entry.getValue()))
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "No sql column in operands of SimpleSqlWhere"));
        SqlColumn sqlColumn = operandMap.remove(firstSqlColumnIdx).unwrap(SqlColumn.class);
        DataType dataType = sqlColumn.getDataType(context);

        Map<Integer, String> orderedMap = new TreeMap<>(Comparator.naturalOrder());
        orderedMap.put(firstSqlColumnIdx, sqlColumn.toSQL(context));
        for (Map.Entry<Integer, SqlNode> entry : operandMap.entrySet()) {
            orderedMap.put(entry.getKey(), toSQL(entry.getValue(), context, dataType));
        }

        // replace variable in sql expression
        return String.format(operator.expression, orderedMap.values().toArray());
    }

    // TODO: sqlFunction.
    private boolean containsSqlColumn(SqlNode sqlNode) {
        return sqlNode instanceof SqlColumn;
    }

    private String toSQL(SqlNode sqlNode, SqlContext context, DataType dataType) {
        if (sqlNode instanceof SqlColumn) {
            return sqlNode.unwrap(SqlColumn.class).toSQL(context);
        } else if (sqlNode instanceof SqlLiteral) {
            return sqlNode.unwrap(SqlLiteral.class).toSQL(dataType, operator.enableQuoting);
        } else if (sqlNode instanceof SqlFunction) {
            throw new RuntimeException("Unsupported sql function for now.");
        }

        throw new RuntimeException("Unknown sql node: " + sqlNode);
    }
}
