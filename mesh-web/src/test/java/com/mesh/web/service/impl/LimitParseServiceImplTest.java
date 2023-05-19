package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LimitParseServiceImplTest extends BaseTest {

  @Autowired
  private LimitParseServiceImpl limitParseService;

  @Test
  void type() {
    assertEquals("$limit", limitParseService.type());
  }
  @Test
  public void parseNumber_shouldReturnLimitClause() {
    Integer value = 100;
    String expectedResult = "limit 100";
    String actualResult = limitParseService.parse(value);
    assertEquals(expectedResult, actualResult);
  }

  @Test
  public void parseOtherThanNumber_shouldReturnEmptyString() {
    Object value = "string";
    String expectedResult = "";
    String actualResult = limitParseService.parse(value);
    assertEquals(expectedResult, actualResult);
  }
  @Test
  public void parseNullValue_shouldReturnEmptyString() {
    Object value = null;
    String expectedResult = "";
    String actualResult = limitParseService.parse(value);
    assertEquals(expectedResult, actualResult);
  }


}
