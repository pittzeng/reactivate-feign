​
# yigou-reactivate-feign
## OpenFeign一样的微服务调用库。 但它是响应式的OpenFeign，内部使用WebClient请求。

（As the OpenFeign project does not currently support reactive clients, such as Spring WebClient, neither does Spring Cloud OpenFeign.We will add support for it here as soon as it becomes available in the core project.Until that is done, we recommend using feign-reactive for Spring WebClient support.）

OpenFeign官网上面说了他不支持响应式，它见意使用（feign-reactive）库，但我使用后发现这个（feign-reactive）库不支持springboot3版本.

打开OpenFeign的源码可以看到，它内部有一个试验性的的Reactivate-OpenFeign但应该还没做完。后续应该可以等到大佬们的大作，自己先封了一个自己用，简单小巧。

so: 如果大家有需要的话，我再添加功能。目前基本的调用服务，和单个服务配置隔离做完了。

## 下面是如何使用：（PS：完全和OpenFeign一样）

环境: spring-boot-starter-webflux spring-cloud-starter-loadbalancer


## 1、在Application启动类上加上注解：
### @EnableFeignClient(basePackages = "com.xxx.")

（参数：basePackages：对应FeignClient所在的包。如果不填将扫描所有包中有带FeignClient注解的服务）

## 2、在调用的接口类上添加服务提供端的注解： 
### @FeignClient(serviceName = "user-service")
## 例子：
@FeignClient(serviceName = "user-service")
public interface UserHandler {

    @GetMapping(value = "/user/get")
    public Flux<User> getUser(@RequestParam Integer id);
    @PostMapping(value = "/user/save")
    public Mono<User>save(@RequestBody User user);
    @GetMapping(value = "/user/delete/{id}")
    public Mono<Boolean>delete(@PathVariable Integer id);
}
## 3、添加连接配置：application.yml

spring:
  cloud:
    loadbalancer:
      enabled: true  #要找打不然可能无法找到负载均衡的服务
com:
  yigou:
    common:
      feign:
        enable: true   #开始Feign功能
        services:  #以下配置表示配置了两个服务的连接参数
          default:
            connection-time-out: 3000
            read-time-out: 5000
            max-in-memory-size: 5
            follow-redirects: false
          user-service:  #对应FeignClient注解中的（serviceName = "user-service"）
            connection-time-out: 3000 #http请求连接超时
            read-time-out: 5000 #读取服务提供者数据超时
            max-in-memory-size: 5 #读取提供者最大内存大小，以M为单位
            follow-redirects: false #目前重试和跳转功能还没有做配了也没用


​
