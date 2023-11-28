你想看一个 HAproxy 的配置示例吗？我可以帮你生成一个。😊

这是一个 HAproxy 的配置示例，它实现了以下功能：

- 监听 80 端口，接收 HTTP 请求
- 根据请求的 URL 路径，将请求分发到不同的后端服务器组
- 后端服务器组包括 web、api、static 三组，分别提供网页、接口和静态资源的服务
- 使用 roundrobin 算法实现负载均衡，使用 httpchk 实现健康检查
- 开启 HAproxy 的监控页面，可以查看后端服务器的状态和统计信息

```ini
# HAproxy 配置示例
global
  # 设置日志输出到本地的 rsyslog 服务器
  log 127.0.0.1 local0
  # 设置最大并发连接数
  maxconn 1000
  # 设置运行用户和组
  user haproxy
  group haproxy
  # 设置以守护进程模式运行
  daemon

defaults
  # 设置默认的日志输出
  log global
  # 设置默认的工作模式为 HTTP
  mode http
  # 设置默认的重试次数
  retries 3
  # 设置默认的超时时间
  timeout connect 5s
  timeout client 10s
  timeout server 10s
  # 设置默认的日志格式和选项
  option httplog
  option dontlognull
  # 设置默认的 HTTP 首部处理选项
  option http-server-close
  option forwardfor

# 定义前端服务器
frontend http-in
  # 监听 80 端口
  bind *:80
  # 根据请求的 URL 路径，选择不同的后端服务器组
  acl url_web path_beg /web
  acl url_api path_beg /api
  acl url_static path_beg /static
  use_backend web if url_web
  use_backend api if url_api
  use_backend static if url_static
  # 设置默认的后端服务器组
  default_backend web
  # 开启 HAproxy 的监控页面
  stats enable
  stats uri /haproxy
  stats auth admin:admin

# 定义后端服务器组 web
backend web
  # 使用 roundrobin 算法实现负载均衡
  balance roundrobin
  # 使用 httpchk 实现健康检查
  option httpchk GET /health
  # 定义后端服务器
  server web1 192.168.1.101:8080 check
  server web2 192.168.1.102:8080 check

# 定义后端服务器组 api
backend api
  # 使用 roundrobin 算法实现负载均衡
  balance roundrobin
  # 使用 httpchk 实现健康检查
  option httpchk GET /health
  # 定义后端服务器
  server api1 192.168.1.103:8080 check
  server api2 192.168.1.104:8080 check

# 定义后端服务器组 static
backend static
  # 使用 roundrobin 算法实现负载均衡
  balance roundrobin
  # 使用 httpchk 实现健康检查
  option httpchk GET /health
  # 定义后端服务器
  server static1 192.168.1.105:8080 check
  server static2 192.168.1.106:8080 check
```

这是一个 HAproxy 的配置示例，希望对你有帮助。😊

源: 与必应的对话， 11/28/2023
(1) HAProxy上手指南 - 知乎. https://zhuanlan.zhihu.com/p/344439654.
(2) . https://bing.com/search?q=haproxy+%e9%85%8d%e7%bd%ae%e7%a4%ba%e4%be%8b.
(3) 学习haproxy基础配置，这一篇就够了 - 知乎 - 知乎专栏. https://zhuanlan.zhihu.com/p/356921154.
(4) HAPRoxy（一）：HAProxy基本配置、调度算法与tcp、http、heath模式配置示例 - readygood - 博客园. https://www.cnblogs.com/readygood/p/9774643.html.
(5) 01 . HAProxy原理使用和配置-腾讯云开发者社区-腾讯云. https://cloud.tencent.com/developer/article/1706675.
