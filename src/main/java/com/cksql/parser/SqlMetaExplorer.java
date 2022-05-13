package com.cksql.parser;

import com.cksql.parser.snippet.BuildInFunction;
import com.cksql.parser.type.LogicalType;

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
