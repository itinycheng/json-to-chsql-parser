package com.chsql.parser.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/** table extra info. */
@Data
@AllArgsConstructor
public class TableExtra {

    private final Long id;

    private final String name;

    private final String database;
}
