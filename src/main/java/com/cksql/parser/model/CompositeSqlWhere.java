package com.cksql.parser.model;

import com.cksql.parser.common.SqlContext;
import com.cksql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.cksql.parser.common.Constant.BRACKET_LEFT;
import static com.cksql.parser.common.Constant.BRACKET_RIGHT;
import static com.cksql.parser.common.Constant.EMPTY;

/** composite sql where. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompositeSqlWhere extends SqlWhere {

    private String relation;

    private List<SqlWhere> conditions;

    @Override
    public String toSQL(SqlContext context) {
        if (CollectionUtils.isEmpty(conditions)) {
            return StringUtils.EMPTY;
        }

        List<String> conditionSqlList = new ArrayList<>();
        for (SqlWhere sqlWhere : conditions) {
            conditionSqlList.add(sqlWhere.toSQL(context));
        }

        SqlExpression sqlExpr = SqlExpression.of(relation);
        String joined = String.join(sqlExpr.name(), conditionSqlList);
        if (conditionSqlList.size() > 1) {
            joined = String.join(EMPTY, BRACKET_LEFT, joined, BRACKET_RIGHT);
        }
        return joined;
    }
}
