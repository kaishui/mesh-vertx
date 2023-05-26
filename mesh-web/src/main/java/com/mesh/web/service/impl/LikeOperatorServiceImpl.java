package com.mesh.web.service.impl;

import com.mesh.web.constant.CommonConstants;
import com.mesh.web.service.OperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LikeOperatorServiceImpl implements OperationService {


  @Override
  public String doOperation(String key, Object value) {
    log.info("key:{}, value:{}", key, value);
    if (null == value) {
      return "";
    }
    if(value instanceof String){
      value = "'" + value + "'";
    }

    return switch (key) {
      case "$like" ->
        // 如果是$like操作符，like '%s%' ，返回用逗号分隔的字符串
        " like " + value;
      case "$notLike" ->
        // 如果是$notLike操作符，not like '%s%' ，返回用逗号分隔的字符串
        " not like " + value;
      default ->
        // 如果是其他操作符，返回空字符串
        "";
    };
  }


  @Override
  public String type() {
    return CommonConstants.LIKE_NOT_LIKE;
  }
}
