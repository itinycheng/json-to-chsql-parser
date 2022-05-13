package com.cksql.parser.model;

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
public class SqlWhere {

    private String type;

    public List<SqlColumn> exhaustiveSqlNodes() {
        Set<SqlColumn> sqlNodeSet = new HashSet<>();
        if (this instanceof SimpleSqlWhere) {
            SimpleSqlWhere simpleSqlWhere = (SimpleSqlWhere) this;
            SqlColumn column = simpleSqlWhere.getColumn();
            sqlNodeSet.add(column);
        } else if (this instanceof CompositeSqlWhere) {
            CompositeSqlWhere compositeSqlWhere = (CompositeSqlWhere) this;
            List<SqlColumn> identifiers =
                    compositeSqlWhere.getConditions().stream()
                            .flatMap(sqlWhere -> sqlWhere.exhaustiveSqlNodes().stream())
                            .collect(Collectors.toList());
            sqlNodeSet.addAll(identifiers);
        }
        return new ArrayList<>(sqlNodeSet);
    }
}
