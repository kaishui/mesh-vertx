package com.mesh.web.service.impl;

import com.mesh.web.service.BaseTest;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class BetweenOperatorServiceImplTest extends BaseTest {

  @Autowired
  private BetweenOperatorServiceImpl betweenOperatorService;
  @Test
  public void testDoOperation_between_allInputValid_shouldReturnCorrectResult() {
    // Given
    JsonObject between = new JsonObject().put("from", "2013-01-01").put("to", "2013-12-01");
    String key = "$between";
    Object value = between;

    // When
    String result = betweenOperatorService.doOperation(key, value);

    // Then
    assertEquals("  between  '2013-01-01' and  '2013-12-01'", result);
  }
  @Test
  public void testDoOperation_notBetween_allInputValid_shouldReturnCorrectResult() {
    // Given
    JsonObject notBetween = new JsonObject().put("from", "2013-01-01").put("to", "2013-12-01");
    String key = "$notBetween";
    Object value = notBetween;

    // When
    String result = betweenOperatorService.doOperation(key, value);

    // Then
    assertEquals(" not between  '2013-01-01' and  '2013-12-01'", result);
  }

  @Test
  public void testDoOperation_notBetween_nullInput_shouldReturnEmptyString() {
    // Given
    JsonObject notBetween = new JsonObject().put("from", null).put("to", "2013-12-01");
    String key = "$notBetween";
    Object value = notBetween;

    // When
    String result = betweenOperatorService.doOperation(key, value);

    // Then
    assertEquals("", result);
  }


}
