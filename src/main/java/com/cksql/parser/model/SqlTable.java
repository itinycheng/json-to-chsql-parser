package com.cksql.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import static com.cksql.parser.util.SqlUtil.tableIdent;

/** sql table. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SqlTable extends SqlNode {

    private String id;

    private String database;

    private String name;

    @Override
    public String ident() {
        return tableIdent(id);
    }
}
