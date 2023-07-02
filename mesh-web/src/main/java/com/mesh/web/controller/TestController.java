package com.mesh.web.controller;

import com.mesh.web.core.controller.RouterInterface;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


@Slf4j
@Controller
public class TestController implements RouterInterface {
  @Override
  public void router(Router router) {
    router.route("/stream").handler(context -> {
      HttpServerResponse response = context.response();

      // Set the content type to "text/event-stream" for streaming
      response.putHeader("Content-Type", "text/event-stream");

      // Enable chunked encoding for streaming
      response.setChunked(true);

      // Write streaming data to the response
      for (int i = 0; i < 10; i++) {
        response.write("Data " + i + "\n");
      }

      // End the response to close the stream
      response.end();
    });

    router.route("/stream/data").handler(context -> {
      HttpServerResponse response = context.response();

      // Set the content type to "text/event-stream" for streaming
      response.putHeader("Content-Type", "text/event-stream");
      response.putHeader("Content-Encoding", "gzip");


      // Enable chunked encoding for streaming
      response.setChunked(true);

      // 创建一个Buffer用于存储要发送的数据
      Buffer buffer = Buffer.buffer();

      // 模拟生成大量数据
      for (int i = 0; i < 1000; i++) {
        buffer.appendString(new JsonObject().put("test", "value is toooooooo long" + i).toString());
      }

      log.info("compress before length: {}", buffer.length());
      // 压缩Buffer
      try {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outputStream);

        gzipOutputStream.write(buffer.getBytes());
        gzipOutputStream.close();
        byte[] compressedBytes = outputStream.toByteArray();
        log.info("compress length after: {}", Buffer.buffer(compressedBytes).length());
        response.write(Buffer.buffer(compressedBytes));
        // End the response to close the stream
        response.end();
      } catch (Exception e) {
        e.printStackTrace();
      }

    });


  }

}
