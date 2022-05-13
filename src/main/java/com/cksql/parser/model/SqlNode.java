package com.cksql.parser.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/** Sql node. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeInfo(use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true)
@JsonSubTypes({
    @JsonSubTypes.Type(value = SqlTable.class, name = "table"),
    @JsonSubTypes.Type(value = SqlColumn.class, name = "column"),
    @JsonSubTypes.Type(value = SqlLiteral.class, name = "literal"),
    @JsonSubTypes.Type(value = SqlFunction.class, name = "function")
})
public class SqlNode {

    private String ident;

    private String type;

    public boolean isValid() {
        return true;
    }
}
