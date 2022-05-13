package com.cksql.parser.model;

import java.util.List;

/** Sql insert. */
public class SqlInsert {

    private boolean overwrite;

    private SqlColumn table;

    private List<SqlColumn> partitions;
}
