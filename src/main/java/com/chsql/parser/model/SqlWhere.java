package com.chsql.parser.model;

import com.chsql.parser.common.SqlContext;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/** sql where. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = CompositeSqlWhere.class, name = "composite"),
    @JsonSubTypes.Type(value = SimpleSqlWhere.class, name = "simple")
})
public abstract class SqlWhere {

    private String type;

    public abstract String toSQL(SqlContext context);

    public List<SqlColumn> getAllColumns() {
        Set<SqlColumn> sqlNodeSet = new HashSet<>();
        if (this instanceof SimpleSqlWhere) {
            SimpleSqlWhere simpleSqlWhere = (SimpleSqlWhere) this;
            for (SqlNode sqlNode : simpleSqlWhere.getOperands()) {
                sqlNodeSet.addAll(sqlNode.getColumns());
            }
        } else if (this instanceof CompositeSqlWhere) {
            CompositeSqlWhere compositeSqlWhere = (CompositeSqlWhere) this;
            List<SqlColumn> columns =
                    compositeSqlWhere.getConditions().stream()
                            .flatMap(sqlWhere -> sqlWhere.getAllColumns().stream())
                            .collect(Collectors.toList());
            sqlNodeSet.addAll(columns);
        }
        return new ArrayList<>(sqlNodeSet);
    }
}
