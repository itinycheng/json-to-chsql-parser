package com.cksql.parser.model;

import com.cksql.parser.common.LiteralRelated;
import com.cksql.parser.common.SqlContext;
import com.cksql.parser.snippet.BuildInFunction;
import com.cksql.parser.snippet.SqlExpression;
import com.cksql.parser.type.DataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
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

    // TODO: sqlFunction.
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
        SqlNode sqlNode = operandMap.remove(firstSqlColumnIdx);
        DataType dataType = parseDataType(sqlNode, context);

        Map<Integer, String> orderedMap = new TreeMap<>(Comparator.naturalOrder());
        orderedMap.put(firstSqlColumnIdx, sqlNode.toSQL(context));
        for (Map.Entry<Integer, SqlNode> entry : operandMap.entrySet()) {
            SqlNode value = entry.getValue();
            Object related = null;
            if (value instanceof SqlLiteral) {
                related = new LiteralRelated(dataType, operator.enableQuoting);
            }
            String sql = value.toSQL(context, related);
            orderedMap.put(entry.getKey(), sql);
        }

        // replace variable in sql expression
        return String.format(operator.expression, orderedMap.values().toArray());
    }

    private boolean containsSqlColumn(SqlNode sqlNode) {
        return CollectionUtils.isNotEmpty(sqlNode.getColumns());
    }

    public DataType parseDataType(SqlNode sqlNode, SqlContext context) {
        if (sqlNode instanceof SqlColumn) {
            return ((SqlColumn) sqlNode).getDataType(context);
        } else if (sqlNode instanceof SqlFunction) {
            SqlFunction sqlFunction = sqlNode.unwrap(SqlFunction.class);
            BuildInFunction function = BuildInFunction.of(sqlFunction.getName());
            return function.resultType;
        }

        throw new RuntimeException("Only columns or functions have data types.");
    }
}
