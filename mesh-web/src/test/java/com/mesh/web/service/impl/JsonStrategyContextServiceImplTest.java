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
}
