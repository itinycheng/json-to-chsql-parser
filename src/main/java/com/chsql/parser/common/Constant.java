package com.chsql.parser.common;

/** constant. */
public class Constant {

    public static final String COMMA = ",";

    public static final String DOT = ".";

    public static final String SPACE = " ";

    public static final String EMPTY = "";

    public static final String AS = "AS";

    public static final String SINGLE_QUOTE = "'";

    public static final String BACK_TICK = "`";

    public static final String UNDERSCORE = "_";

    public static final String BRACKET_LEFT = "(";

    public static final String BRACKET_RIGHT = ")";

    public static final String DEFAULT_ARRAY = "[]";

    public static final String DEFAULT_MAP = "map()";

    public static final String NULL = "null";

    public static final String DOLLAR_SYMBOL = "$";

    public static final String ROW_NUM = "row_num";

    public static final String TOP_N_AND_OTHER_SQL_FORMAT =
            "WITH with_table AS (SELECT rowNumberInAllBlocks() as "
                    + ROW_NUM
                    + ", * from (%s))"
                    + " SELECT %s FROM with_table WHERE "
                    + ROW_NUM
                    + " < %s"
                    + " UNION ALL"
                    + " SELECT %s from with_table where "
                    + ROW_NUM
                    + " >= %s";
}
