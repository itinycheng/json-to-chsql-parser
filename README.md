# json-to-chsql-parser

> A plugin that can convert JSON to ClickHouse Query SQL, support multi-table join query.   
> Test Case: https://github.com/itinycheng/json-to-chsql-parser/blob/master/src/test/java/com/chsql/parser/SqlParserTest.java

## How To Use

```java
SqlContext sqlContext=new SqlContext.Builder()
        .addTable(new TableExtra(1L,"t_user","ods","Distributed('cluster_10shards_2replicas', 'ods', 't_user_local', javaHash(id))","id",JoinType.CO_LOCATE))
        .addColumn(new ColumnExtra("id",1L,"Int64"))
        .addColumn(new ColumnExtra("type",1L,"String"))
        .addColumn(new ColumnExtra("favor",1L,"Array(String)"))
        .addColumn(new ColumnExtra("props",1L,"Map(String, UInt64)"))
        .build();

        File file=new File(classLoader.getResource(jsonFile).getFile());
        SqlSelect sqlSelect=JsonUtil.toBean(file,SqlSelect.class);

        SqlParser parser=new SqlParser(sqlContext);
        String sql=parser.parseQuery(sqlSelect);
        System.out.println(sql);
```

## Demo

### Input Json

```json
{
  "select": [
    {
      "type": "function",
      "name": "uniqCount",
      "operands": [
        {
          "type": "column",
          "qualifier": "1",
          "names": [
            "id"
          ]
        }
      ]
    },
    {
      "type": "column",
      "qualifier": "1",
      "names": [
        "type"
      ]
    }
  ],
  "where": {
    "type": "composite",
    "relation": "AND",
    "conditions": [
      {
        "type": "simple",
        "operator": "GT",
        "operands": [
          {
            "type": "column",
            "qualifier": "1",
            "names": [
              "id"
            ]
          },
          {
            "type": "literal",
            "values": [
              "0"
            ]
          }
        ]
      },
      {
        "type": "composite",
        "relation": "OR",
        "conditions": [
          {
            "type": "simple",
            "operator": "EQ",
            "operands": [
              {
                "type": "function",
                "name": "length",
                "operands": [
                  {
                    "type": "column",
                    "qualifier": "1",
                    "names": [
                      "favor"
                    ]
                  }
                ]
              },
              {
                "type": "literal",
                "values": [
                  "0"
                ]
              }
            ]
          },
          {
            "type": "simple",
            "operator": "BETWEEN",
            "operands": [
              {
                "type": "column",
                "qualifier": "1",
                "names": [
                  "props",
                  "rate"
                ]
              },
              {
                "type": "literal",
                "values": [
                  "0"
                ]
              },
              {
                "type": "literal",
                "values": [
                  "100"
                ]
              }
            ]
          }
        ]
      }
    ]
  },
  "orderBy": {
    "type": "DESC",
    "items": [
      {
        "type": "column",
        "qualifier": "1",
        "names": [
          "type"
        ]
      }
    ]
  },
  "limit": {
    "hasOther": true,
    "offset": 0,
    "rowCount": 10
  }
}
```

### Output SQL

```sql
WITH with_table AS (
    SELECT rowNumberInAllBlocks() as row_num,
        *
    from (
            SELECT count(distinct t1.id) AS uniqcount_t1_id,
                t1.type AS t1_type
            FROM ods.t_user AS t1
            WHERE t1.id > 0
                AND(
                    length(t1.favor) = 0
                    OR t1.props['rate'] BETWEEN 0 AND 100
                )
            GROUP BY t1_type
            ORDER BY t1_type DESC
        )
)
SELECT uniqcount_t1_id,
    t1_type
FROM with_table
WHERE row_num < 10
UNION ALL
SELECT sum(uniqcount_t1_id),
    'Other'
from with_table
where row_num >= 10
```