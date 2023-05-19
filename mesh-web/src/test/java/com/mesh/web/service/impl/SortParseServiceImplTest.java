package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class SortParseServiceImplTest extends BaseTest {

  @Autowired
  private SortParseServiceImpl service;
  // Test 1
  @Test
  public void parse_shouldReturnValidOrderByWithAsc(){
    JsonObject object = new JsonObject()
      .put("key1", 1)
      .put("key2", -1);
    String res = service.parse(object);
    assertEquals("order by key1 asc, key2 desc", res);
  }

  // Test 2
  @Test
  public void parse_shouldReturnEmptyStringWithOtherType(){
    String res = service.parse("a string");
    assertEquals("", res);
  }

}
