package com.cksql.parser.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** table extra info. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableExtra {

    private Long id;

    private String name;

    private String database;
}
