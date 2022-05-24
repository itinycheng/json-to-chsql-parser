package com.chsql.parser.common;

/** constant. */
public class Constant {

    public static final String COMMA = ",";

    public static final String DOT = ".";

    public static final String STAR = "*";

    public static final String SPACE = " ";

    public static final String EMPTY = "";

    public static final String AS = "AS";

    public static final String SINGLE_QUOTE = "'";

    public static final String BACK_TICK = "`";

    public static final String UNDERSCORE = "_";

    public static final String BRACKET_LEFT = "(";

    public static final String BRACKET_RIGHT = ")";

    public static final String TOP_N_AND_OTHER_SQL_FORMAT =
            "WITH with_table AS (SELECT rowNumberInAllBlocks() as row_num, * from (%s))"
                    + " SELECT %s FROM with_table WHERE row_num < %s"
                    + " UNION ALL"
                    + " SELECT %s from with_table where row_num >= %s";
}
