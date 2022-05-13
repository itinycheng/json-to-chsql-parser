package com.cksql.parser.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** sql limit. */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SqlLimit {

    private int offset;

    private int rowCount;
}
