package com.mesh.web.util;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ParamParserTest {

  private static ParamParser paramParser;

  // Setup mock json input
  @BeforeAll
  public static void setup() {
    paramParser = new ParamParser("{" +
      "\"consumerProject\": \"project\"," +
      "\"datasetName\": \"data\"," +
      "\"filters\": [{\"column\": \"a\", \"operator\": \"=\", \"value\": \"1\"}]," +
      "\"columns\": [\"a\", \"b\"]," +
      "\"from\": [\"table1\", \"table2\"]," +
      "\"sort\": [{\"column\": \"a\", \"desc\": true}]," +
      "\"pageInfo\": {\"totalSize\": 1000, \"pageSize\": 10}" +
      "}");
  }

  @Test
  public void testGetConsumerProject() {
    assertEquals("project", paramParser.getConsumerProject());
  }

  @Test
  public void testGetDatasetName() {
    assertEquals("data", paramParser.getDatasetName());
  }

  @Test
  public void testGetFilters() {
    assertTrue(paramParser.getFilters().size() == 1);
  }

  @Test
  public void testGetColumns() {
    assertTrue(paramParser.getColumns().size() == 2);
  }

  @Test
  public void testGetFrom() {
    assertTrue(paramParser.getFrom().size() == 2);
  }

  @Test
  public void testGetSort() {
    assertTrue(paramParser.getSort().size() == 1);
  }

  @Test
  public void testGetPageInfo() {
    assertTrue(paramParser.getPageInfo().size() == 2);
  }
}
