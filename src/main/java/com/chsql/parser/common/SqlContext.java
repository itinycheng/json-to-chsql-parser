package com.chsql.parser.common;

import com.chsql.parser.model.SqlSelect;
import lombok.Data;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * sql context info.
 */
@Data
public class SqlContext implements Cloneable {

    private final Map<String, TableExtra> tableMap;

    private final Map<String, Map<String, ColumnExtra>> tableColumnMap;

    private SqlSelect sqlSelect;

    @Override
    public SqlContext clone() {
        try {
            return (SqlContext) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builder for {@link SqlContext}.
     */
    public static class Builder {

        private final Map<String, TableExtra> tableMap = new HashMap<>();

        private final Map<String, Map<String, ColumnExtra>> tableColumnMap = new HashMap<>();

        public Builder addTable(TableExtra table) {
            Preconditions.checkNotNull(table);
            tableMap.putIfAbsent(table.getId().toString(), table);
            return this;
        }

        public Builder addColumn(ColumnExtra column) {
            Preconditions.checkNotNull(column);
            tableColumnMap
                    .computeIfAbsent(column.getTableId().toString(), tableId -> new HashMap<>())
                    .putIfAbsent(column.getName(), column);
            return this;
        }

        public SqlContext build() {
            if (MapUtils.isEmpty(tableMap) || MapUtils.isEmpty(tableColumnMap)) {
                throw new RuntimeException("Tables and columns can not be null");
            }
            return new SqlContext(tableMap, tableColumnMap);
        }
    }
}
