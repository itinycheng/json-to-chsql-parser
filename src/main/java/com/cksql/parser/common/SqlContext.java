package com.cksql.parser.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/** sql context info. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlContext {

    private Map<Long, TableExtra> tableMap;

    private Map<Long, Map<String, ColumnExtra>> tableColumnMap;

    /** Builder for {@link SqlContext}. TODO: throw exception if exists. */
    public static class Builder {

        private final Map<Long, TableExtra> tableMap = new HashMap<>();

        private final Map<Long, Map<String, ColumnExtra>> tableColumnMap = new HashMap<>();

        public Builder addTable(TableExtra table) {
            Preconditions.checkNotNull(table);
            tableMap.putIfAbsent(table.getId(), table);
            return this;
        }

        public Builder addColumn(ColumnExtra column) {
            Preconditions.checkNotNull(column);
            tableColumnMap
                    .computeIfAbsent(column.getTableId(), tableId -> new HashMap<>())
                    .putIfAbsent(column.getName(), column);
            return this;
        }

        public SqlContext build() {
            return new SqlContext(tableMap, tableColumnMap);
        }
    }
}
