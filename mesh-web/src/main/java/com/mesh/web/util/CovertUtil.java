package com.mesh.web.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class CovertUtil {

  public static void main(String[] args) {
    String bigqueryResultJson= """
      {
        "schema": {
        	 "fields":[
        	 	{
        	 		"name": "column1",
        	 		"type": "STRING",
        	 		"mode": "NULLABLE"
        	 	},
        	 	{
        	 		"name": "column2",
        	 		"type": "STRING",
        	 		"mode": "NULLABLE"
        	 	},
        	 	{
        	 		"name": "column3",
        	 		"type": "DATE",
        	 		"mode": "NULLABLE"
        	 	},
        	 	{
        	 		"name": "price",
        	 		"type": "NUMERIC",
        	 		"mode": "NULLABLE"
        	 	}
        	 ]
        },
        "totalRows": 3,
        "rows": [
          {
            "f": [
            	{"v": "value1"},
            	{"v": "value2"},
            	{"v": "2023-01-01"},
            	{"v": "1.3712"}
            ]
          },
          {
            "f": [
            	{"v": "value3"},
            	{"v": "value4"},
            	{"v": "2023-01-21"},
            	{"v": "1.3812"}
            ]
          }
        ]
      }
      """;
    JsonObject bigqueryResult = new JsonObject(bigqueryResultJson); // bigqueryResultJson是一个字符串
    JsonArray rows = bigqueryResult.getJsonArray("rows"); // rows是一个JsonArray
    JsonObject schema = bigqueryResult.getJsonObject("schema"); // rows是一个JsonArray

// 创建一个Validator实例
//    Validator validator = Validator.create(schema, new JsonSchemaOptions().setDraft(Draft.DRAFT7));

// 创建一个空的JsonArray来存储转换后的对象
    JsonArray transformedRows = new JsonArray();

// 遍历rows数组中的每个对象，验证并转换成key pair的形式
    for (int i = 0; i < rows.size(); i++) {
      JsonObject row = rows.getJsonObject(i); // row是一个JsonObject，包含一个"f"键，其值是一个JsonArray
      JsonArray values = row.getJsonArray("f"); // values是一个JsonArray，包含多个JsonObject，每个对象有一个"v"键，其值是一个Object

      // 创建一个空的JsonObject来存储key pair
      JsonObject keyPair = new JsonObject();

      // 遍历values数组中的每个对象，获取其值，并根据schema中的字段名和类型设置到key pair中
      for (int j = 0; j < values.size(); j++) {
        JsonObject value = values.getJsonObject(j); // value是一个JsonObject，包含一个"v"键，其值是一个Object
        Object v = value.getValue("v"); // v是一个Object，表示字段的值

        // 获取schema中的字段名和类型
        JsonObject field = schema.getJsonArray("fields").getJsonObject(j); // field是一个JsonObject，表示字段的属性
        String name = field.getString("name"); // name是一个String，表示字段的名字
        String type = field.getString("type"); // type是一个String，表示字段的类型

        // 根据类型将v转换成合适的Java类型，并设置到key pair中
        switch (type) {
          case "STRING":
            keyPair.put(name, (String) v);
            break;
          case "DATE":
//            keyPair.put(name, Date.parse((String) v));
            keyPair.put(name, (String) v);
            break;
          case "NUMERIC":
            keyPair.put(name, new BigDecimal((String) v));
            break;
          default:
            keyPair.put(name, v);
            break;
        }
      }

      // 验证key pair是否符合schema
//      OutputUnit result = validator.validate(keyPair);
//      if (result.getValid()) {
        // 如果符合，添加到transformedRows中
        transformedRows.add(keyPair);
//      } else {
//        // 如果不符合，抛出异常或者跳过该对象
//        throw new RuntimeException("Invalid key pair: " + keyPair);
//      }
    }

// 打印或返回转换后的json数组
    System.out.println(transformedRows.encodePrettily());

  }
}
