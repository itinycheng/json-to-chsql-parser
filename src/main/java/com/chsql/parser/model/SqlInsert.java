package com.chsql.parser.model;

import lombok.Data;

import java.util.List;

/** Sql insert. */
@Data
public class SqlInsert {

    private boolean overwrite;

    private SqlTable table;

    private List<SqlColumn> partitions;
}
