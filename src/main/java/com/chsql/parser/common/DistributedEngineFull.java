package com.chsql.parser.common;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static com.chsql.parser.common.Preconditions.checkArgument;

/** Engine info for distributed table. */
@Data
public class DistributedEngineFull {

    private final String cluster;

    private final String database;

    private final String table;

    private final String shardingKey;

    private final String policyName;

    public static DistributedEngineFull of(String cluster, String database, String table) {
        return new DistributedEngineFull(cluster, database, table, null, null);
    }

    public static DistributedEngineFull of(
            String cluster, String database, String table, String shardingKey, String policyName) {
        return new DistributedEngineFull(cluster, database, table, shardingKey, policyName);
    }

    private DistributedEngineFull(
            String cluster, String database, String table, String shardingKey, String policyName) {
        checkArgument(StringUtils.isBlank(cluster), "cluster cannot be null or empty");
        checkArgument(StringUtils.isBlank(database), "database cannot be null or empty");
        checkArgument(StringUtils.isBlank(table), "table cannot be null or empty");

        this.cluster = cluster;
        this.database = database;
        this.table = table;
        this.shardingKey = shardingKey;
        this.policyName = policyName;
    }
}
