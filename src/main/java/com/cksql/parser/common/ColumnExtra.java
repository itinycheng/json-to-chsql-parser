package com.cksql.parser.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** column extra info. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnExtra {

    private String name;

    private Long tableId;

    private String originType;
}
