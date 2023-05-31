package com.mesh.web.constant;

import lombok.experimental.FieldNameConstants;

@FieldNameConstants
public class CommonConstants {

  public static final String FUNCTION = "$sum,$avg,$count,$min,$max,";
  public static final String ALIAS = "$as,$alias,";
  public static final String LOGICAL = "$and,$or,$not,";
  public static final String COMPARISON = "$gt,$lt,$eq,$gte,$lte,$ne,";
  public static final String ARITHMETIC_OPERATOR = "$add,$subtract,$sub,$multiply,$mul,$divide,$div,";
  public static final String IN_NOT_IN_OPERATOR = "$in,$nin,$notIn,";
  public static final String LIKE_NOT_LIKE = "$like,$notLike,";
  public static final String BETWEEN_NOT_BETWEEN = "$between,$notBetween,";
  public static final String DATE_FUNCTION = "$FORMAT_DATE,$formatDate,$PARSE_DATE,$parseDate,";
  public static final String DISTINCT = "$distinct,$DISTINCT,";
}
