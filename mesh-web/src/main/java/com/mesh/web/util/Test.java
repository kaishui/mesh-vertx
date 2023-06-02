package com.mesh.web.util;

// 测试代码
public class Test {

  public static void main(String[] args) {

    // 输入的json字符串
    String input = """
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
            "id": "column2",
            "dbField": "t.start_date",
            "dataType": "date",
            "value": {"fromDate": "2020-01-01", "toDate": "2020-01-31"}
          },{
            "id": "column3",
            "dbField": "t.price",
            "dataType": "number",
            "value": 10000,
            "condition": "greater"
          },
          {
            "id": "column3",
            "dbField": "t.text",
            "dataType": "string",
            "value": "A",
            "condition": "contains"
          }
        ],
        "columns": [
          {
            "id": "code",
            "dbField": "t.code_name",
            "distinct": true
          },
          {
            "id": "tableName",
            "dbField": "t.table_Name"
          },
          {
            "id": "column1",
            "dbField": "t.start_date"
          }
          ],
        "from": ["t"],
        "sort": [{"id": "column1", "dbField": "t.start_date", "direction": "ASC"}],
        "pageInfo": {
          "pageNumber": 1,
          "pageSize": 10
        }
      }
      """;

    // 创建解析器和生成器
    ParamParser parser = new ParamParser(input);
    ParamConverter generator = new ParamConverter(parser);

    // 输出结果
    System.out.println(generator.getOutput());

    // 结果为：
    // {"$project":"project-dev.dataset-dev","$from":["t"],"$match":{"$tuple":[{"t.group_name":"fg1","t.source_name":"ss1","t.fund_code":"f1"},{"t.group_name":"fg2","t.source_name":"ss2","t.fund_code":"f2"}],"t.start_date":{"$between":{"fromDate":"2020-01-01","toDate":"2020-01-31"}},"t.price":{"$gt":10000}},"$project":{"t.code_name":"code","t.table_Name":"tableName","t.start_date":"column1"},"$sort":{"t.start_date":1},"$limit":10,"$skip":0}

  }
}
