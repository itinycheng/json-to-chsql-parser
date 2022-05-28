package com.chsql.parser;

import com.chsql.parser.common.DistributedEngineFull;
import com.chsql.parser.common.Preconditions;
import com.chsql.parser.common.SqlContext;
import com.chsql.parser.common.TableExtra;
import com.chsql.parser.enums.BuildInFunction;
import com.chsql.parser.model.SqlColumn;
import com.chsql.parser.model.SqlFunction;
import com.chsql.parser.model.SqlNode;
import com.chsql.parser.model.SqlSelect;
import com.chsql.parser.model.SqlTable;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.chsql.parser.enums.JoinType.CO_LOCATE;
import static com.chsql.parser.util.SqlUtil.extractAllColumns;

/**
 * parse json to sql. <br>
 * thread safe.
 */
public class SqlParser {

    private final SqlContext context;

    private final SqlValidator validator;

    public SqlParser(SqlContext context) {
        this.context = Preconditions.checkNotNull(context);
        this.validator = new SqlValidator(context);
    }

    public String parseQuery(SqlSelect origin) {
        List<SqlTable> sqlTables = extractFrom(origin, context);
        List<SqlNode> groupBy = extractGroupBy(origin.getSelect());

        SqlSelect sqlSelect = new SqlSelect();
        sqlSelect.setSelect(origin.getSelect());
        sqlSelect.setFrom(sqlTables);
        sqlSelect.setWhere(origin.getWhere());
        sqlSelect.setGroupBy(groupBy);
        sqlSelect.setOrderBy(origin.getOrderBy());
        sqlSelect.setLimit(origin.getLimit());

        if (!validator.validate(sqlSelect)) {
            throw new RuntimeException("invalid json string.");
        }

        SqlContext cloneContext = context.clone();
        cloneContext.setSqlSelect(sqlSelect);
        return sqlSelect.toSQL(cloneContext);
    }

    private List<SqlTable> extractFrom(SqlSelect origin, SqlContext sqlContext) {
        Map<String, SqlTable> tableMap = new TreeMap<>(Comparator.naturalOrder());
        Map<String, TableExtra> allTableMap = sqlContext.getTableMap();
        for (SqlColumn column : extractAllColumns(origin)) {
            String tableId = column.getQualifier();
            TableExtra tableExtra = allTableMap.get(tableId);

            if (tableMap.containsKey(tableId)) {
                continue;
            }

            if (tableExtra.getJoinType() == CO_LOCATE
                    && tableExtra.isDistributed()
                    && tableMap.values().stream().anyMatch(SqlTable::isDistributed)) {
                DistributedEngineFull engine = tableExtra.getEngine();
                String table = engine.getTable();
                String database = engine.getDatabase();
                tableMap.put(
                        tableId,
                        new SqlTable(
                                tableId,
                                table,
                                database,
                                tableExtra.getJoinKey(),
                                tableExtra.isGlobalJoin(),
                                false));
            } else {
                tableMap.put(
                        tableId,
                        new SqlTable(
                                tableId,
                                tableExtra.getName(),
                                tableExtra.getDatabase(),
                                tableExtra.getJoinKey(),
                                tableExtra.isGlobalJoin(),
                                tableExtra.isDistributed()));
            }
        }

        return new ArrayList<>(tableMap.values());
    }

    private List<SqlNode> extractGroupBy(List<SqlNode> select) {
        List<SqlNode> groupByList = new ArrayList<>();
        List<SqlNode> aggFuncList = new ArrayList<>();
        for (SqlNode sqlNode : select) {
            if (!(sqlNode instanceof SqlFunction)) {
                groupByList.add(sqlNode);
                continue;
            }

            SqlFunction sqlFunction = sqlNode.unwrap(SqlFunction.class);
            BuildInFunction buildIn = BuildInFunction.of(sqlFunction.getName());
            if (buildIn.isAggFunc()) {
                aggFuncList.add(sqlNode);
            } else {
                groupByList.add(sqlNode);
            }
        }

        if (CollectionUtils.isEmpty(aggFuncList)) {
            return null;
        }

        return groupByList;
    }
}
