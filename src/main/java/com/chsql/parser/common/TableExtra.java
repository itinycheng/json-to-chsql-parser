package com.chsql.parser.common;

import com.chsql.parser.enums.JoinType;
import lombok.Data;

import static com.chsql.parser.util.ClickHouseUtil.parseEngineFull;

/** table extra info. */
@Data
public class TableExtra {

    private final Long id;

    private final String name;

    private final String database;

    private final String engineFull;

    private final DistributedEngineFull engine;

    private final String joinKey;

    private final JoinType joinType;

    public TableExtra(
            Long id,
            String name,
            String database,
            String engineFull,
            String joinKey,
            JoinType joinType) {
        this.id = id;
        this.name = name;
        this.database = database;
        this.engineFull = engineFull;
        this.joinKey = joinKey;
        this.joinType = joinType;
        this.engine = parseEngineFull(engineFull);
    }

    public boolean isDistributed() {
        return engine != null;
    }

    public boolean isGlobalJoin() {
        return joinType == JoinType.GLOBAL;
    }
}
