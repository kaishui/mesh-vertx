用java 语言 vertx 写个转换类，实现以下json，单一责任原则，多个小函数：
```json
{
  "consumerProject": "project-dev",
  "datasetName": "dataset-dev",
  "filters": [
    {
      "id": "fundGroup",
      "dbField": {"fundGroup":  "t.group_name", "sourceSystem":  "t.source_name", "fund":  "t.fund_code"},
      "dataType": "string",
      "value": [
        {"fundGroup": "fg1", "sourceSystem": "ss1", "fund": "f1"},
        {"fundGroup": "fg2", "sourceSystem": "ss2", "fund": "f2"}]
    },{
      "id": "column1",
      "dataField": "t.cb",
      "dataType": "string",
      "value": ["val1", "val2"]
    },{
      "id": "column2",
      "dataField": "t.start_date",
      "dataType": "date",
      "value": {"fromDate": "2020-01-01", "toDate": "2020-01-31"}
    },{
      "id": "column3",
      "dataField": "t.price",
      "dataType": "number",
      "value": 10000,
      "condition": "greater"
    }
  ],
  "columns": [
    {
      "id": "code",
      "dataField": "t.code_name",
      "distinct": true
    },
    {
      "id": "tableName",
      "dataField": "t.table_Name"
    },
    {
      "id": "column1",
      "dataField": "t.start_date"
    }
    ],
  "from": ["t"],
  "sort": [{"id": "column1", "dataField": "t.start_date", "direction": "ASC"}],
  "pageInfo": {
    "pageNumber": 1,
    "pageSize": 10
  }
}

```

转成一下json 格式：
```json
{
  "$project": {
    "t.code_name": "code",
    "t.table_Name": "tableName",
    "t.start_date": "column1"
  },
  "$from": ["project-dev.dataset-dev.t"],
  "$match": {
    "$tuple": [
      {"t.group_name": "fg1", "t.source_name": "ss1", "t.fund_code": "f1"},
      {"t.group_name": "fg2", "t.source_name": "ss2", "t.fund_code": "f2"}
    ],
    "t.cb": {"$in":  ["val1", "val2"]},
    "t.start_date": {
      "$between": {"from": "2020-01-01", "to": "2020-01-31"}
    },
    "t.price":{"$gt": 10000}
  },
  "$sort": {"t.start_date": 1},
  "$limit": 10,
  "$skip": 0
}
```
