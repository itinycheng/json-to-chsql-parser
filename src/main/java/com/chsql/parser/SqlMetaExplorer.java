package com.chsql.parser;

import com.chsql.parser.type.LogicalType;
import com.chsql.parser.snippet.BuildInFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** sql meta viewer. */
public class SqlMetaExplorer {

    /** data type supported function list. */
    public Map<LogicalType, List<BuildInFunction>> getAggFunctions() {
        Map<LogicalType, List<BuildInFunction>> result = new HashMap<>();
        for (LogicalType logicalType : LogicalType.values()) {
            result.put(logicalType, getAggFunctions(logicalType));
        }
        return result;
    }

    public List<BuildInFunction> getAggFunctions(LogicalType logicalType) {
        return null;
    }
}
