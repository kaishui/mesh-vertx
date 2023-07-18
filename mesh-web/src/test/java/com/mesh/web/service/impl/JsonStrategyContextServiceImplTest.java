package com.mesh.web.service.impl;

import com.mesh.web.service.StrategyContextService;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
class JsonStrategyContextServiceImplTest {
  @Autowired
  private StrategyContextService strategyContextService;

  @Autowired
  private MatchParseServiceImpl matchParseService;

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void parseEmptyJson() {
    JsonObject json = new JsonObject();
    assertEquals("", strategyContextService.parse(json));

  }

  @Test
  void getStrategy() {
    String type = "$match";
    assertEquals(matchParseService, strategyContextService.getStrategy(type));

  }

  @Test
  void TestFormIsJsonObject() {

    String jsonStr = """
      { "$project": "*", "$from": {"aliasTable": {"$project": {"c1": 1}, "$from": "T"}}}
      """;
    JsonObject jsonObject = new JsonObject(jsonStr);
    String actual = strategyContextService.parse(jsonObject);
    assertEquals("select * from (select c1 from T)  as aliasTable", actual);

  }

  @Test
  void TestLookup() {

    String jsonStr = """
      { "$project": "*", "$from": {"aliasTable": {"$project": {"c1": 1, "view":1}, "$from": "T"}},
        "$lookup": {
      		"from": {"user": {"$project": {"c1": 1, "view":1}, "$from": "T", "$match": {"c1": {"$gte": 4}}}},
      		"type": "left",
      		"foreign": [
      		{"aliasTable.view": "user.view"},
      		{"aliasTable.c1": "user.c1"}
      		]
      	}
      }
      """;
    JsonObject jsonObject = new JsonObject(jsonStr);
    String actual = strategyContextService.parse(jsonObject);
    String expected = "select * from (select c1, view from T)  as aliasTable   left join (select c1, view from T where c1 >= 4)  as user on (aliasTable.view=user.view and aliasTable.c1=user.c1)";
    assertEquals(expected, actual);
  }
}
