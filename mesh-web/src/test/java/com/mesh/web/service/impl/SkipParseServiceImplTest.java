package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class SkipParseServiceImplTest extends BaseTest {


  @Autowired
  private SkipParseServiceImpl service;

  // Test 1
  @Test
  public void parseNumber() {

    Object value = 5;
    String expectedResult = "offset 5";

    // Act
    String result = service.parse(value);

    // Assert
    assertEquals(expectedResult, result);
  }

  // Test 2
  @Test
  public void parseNonNumber() {

    Object value = "someString";
    String expectedResult = "";

    // Act
    String result = service.parse(value);

    // Assert
    assertEquals(expectedResult, result);
  }

  // Test 3
  @Test
  public void type() {

    String expectedResult = "$skip";

    // Act
    String result = service.type();

    // Assert
    assertEquals(expectedResult, result);
  }

}
