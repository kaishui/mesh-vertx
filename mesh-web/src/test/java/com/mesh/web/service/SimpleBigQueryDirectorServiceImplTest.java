package com.mesh.web.service;

import com.mesh.web.instance.CommonInstance;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@ExtendWith(SpringExtension.class)
class SimpleBigQueryDirectorServiceImplTest {

  @Autowired
  private SQLDirectorService director;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testConstruct() {
    JsonObject sqlParamJson = new JsonObject();
    sqlParamJson.put("select", "*");
    sqlParamJson.put("from", "table_name");
    sqlParamJson.put("where", "column1 > 5");
    sqlParamJson.put("groupBy", "column2");
    sqlParamJson.put("orderBy", "column3");
    JsonObject join = new JsonObject();
    join.put("type", "inner");
    join.put("table", "join_table_name");
    join.put("condition", "table_name.id = join_table_name.id");
    sqlParamJson.put("join", join);

//    SimpleBigQueryDirectorServiceImpl director = new SimpleBigQueryDirectorServiceImpl();
    String expectedSql = "SELECT * FROM table_name INNER JOIN join_table_name ON table_name.id = join_table_name.id WHERE column1 > 5 GROUP By column2 ORDER BY column3";

    String sql = director.construct(sqlParamJson);
    assertEquals(expectedSql.toLowerCase(), sql.toLowerCase());
  }

  @Test
  public void testConstructSubSql() {
    JsonObject sqlParamJson = new JsonObject();
    sqlParamJson.put("select", "*");
    sqlParamJson.put("from", "table_name a");
    sqlParamJson.put("where", "a.amount > (select avg(amount) from order)");
    sqlParamJson.put("groupBy", "column2");
    sqlParamJson.put("orderBy", "column3");
    JsonObject join = new JsonObject();
    join.put("type", "inner");
    join.put("table", "join_table_name");
    join.put("condition", "table_name.id = join_table_name.id");
    sqlParamJson.put("join", join);

//    SimpleBigQueryDirectorServiceImpl director = new SimpleBigQueryDirectorServiceImpl();
    String expectedSql = "select * from table_name a inner join join_table_name on table_name.id = join_table_name.id where (a.amount > (select avg(amount) from order)) group by column2 order by column3";

    String sql = director.construct(sqlParamJson);
    assertEquals(expectedSql.toLowerCase(), sql.toLowerCase());
  }
  @Test
  public void testGetSqlBuilder() {
//    SimpleBigQueryDirectorServiceImpl director = new SimpleBigQueryDirectorServiceImpl();
    SQLBuilder builder = director.getSqlBuilder();

    assertEquals(CommonInstance.BIG_QUERY, builder.type());
  }


}
