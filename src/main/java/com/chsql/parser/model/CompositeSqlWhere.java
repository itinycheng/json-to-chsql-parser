package com.chsql.parser.model;

import com.chsql.parser.SqlValidator;
import com.chsql.parser.common.SqlContext;
import com.chsql.parser.snippet.SqlExpression;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.chsql.parser.common.Constant.BRACKET_LEFT;
import static com.chsql.parser.common.Constant.BRACKET_RIGHT;
import static com.chsql.parser.common.Constant.EMPTY;

/** composite sql where. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CompositeSqlWhere extends SqlWhere {

    private String relation;

    private List<SqlWhere> conditions;

    @Override
    public boolean validate(SqlValidator validator, SqlContext context) {
        return validator.validateCompositeWhere(this);
    }

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
