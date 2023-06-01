package com.mesh.web.util;

// 测试代码
public class Test {

  public static void main(String[] args) {

    // 输入的json字符串
    String input = "{\n" +
      " \"consumerProject\": \"project-dev\",\n" +
      " \"datasetName\": \"dataset-dev\",\n" +
      " \"filters\": [\n" +
      " {\n" +
      " \"id\": \"fundGroup\",\n" +
      " \"dbField\": {\"fundGroup\": \"t.group_name\", \"sourceSystem\": \"t.source_name\", \"fund\": \"t.fund_code\"},\n" +
      " \"dataType\": \"string\",\n" +
      " \"value\": [\n" +
      " {\"fundGroup\": \"fg1\", \"sourceSystem\": \"ss1\", \"fund\": \"f1\"},\n" +
      " {\"fundGroup\": \"fg2\", \"sourceSystem\": \"ss2\", \"fund\": \"f2\"}]\n" +
      " },{\n" +
      " \"id\": \"column2\",\n" +
      " \"dataField\": \"t.start_date\",\n" +
      " \"dataType\": \"date\",\n" +
      " \"value\": {\"fromDate\": \"2020-01-01\", \"toDate\": \"2020-01-31\"}\n" +
      " },{\n" +
      " \"id\": \"column3\",\n" +
      " \"dataField\": \"t.price\",\n" +
      " \"dataType\": \"number\",\n" +
      " \"value\": 10000,\n" +
      " \"condition\": \"greater\"\n" +
      " }\n" +
      " ],\n" +
      " \"columns\": [\n" +
      " {\n" +
      " \"id\": \"code\",\n" +
      " \"dataField\": \"t.code_name\",\n" +
      " \"distinct\": true\n" +
      " },\n" +
      " {\n" +
      " \"id\": \"tableName\",\n" +
      " \"dataField\": \"t.table_Name\"\n" +
      " },\n" +
      " {\n" +
      " \"id\": \"column1\",\n" +
      " \"dataField\": \"t.start_date\"\n" +
      " }\n" +
      " ],\n" +
      " \t\"from\": [\"t\"],\n" +
      "\t\"sort\": [{\"id\": \t\"column1\", \t\"dataField\":\"t.start_date\", \t\"direction\":\"ASC\"}],\n" +
      "\t\"pageInfo\":{\"pageNumber\":\"1\",\"pageSize\":\"10\"}\t\n" +
      "}";

    // 创建解析器和生成器
    JsonParser parser = new JsonParser(input);
    JsonGenerator generator = new JsonGenerator(parser);

    // 输出结果
    System.out.println(generator.getOutput());

    // 结果为：
    // {"$project":"project-dev.dataset-dev","$from":["t"],"$match":{"$tuple":[{"t.group_name":"fg1","t.source_name":"ss1","t.fund_code":"f1"},{"t.group_name":"fg2","t.source_name":"ss2","t.fund_code":"f2"}],"t.start_date":{"$between":{"fromDate":"2020-01-01","toDate":"2020-01-31"}},"t.price":{"$gt":10000}},"$project":{"t.code_name":"code","t.table_Name":"tableName","t.start_date":"column1"},"$sort":{"t.start_date":1},"$limit":10,"$skip":0}

  }
}
