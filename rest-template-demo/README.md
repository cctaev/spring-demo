# RestTemplate
RestTemplate是一个同步执行HTTP请求的客户端，它对外暴露简单的、模板化的API。
与之相对的是较为底层的类库，如：JDK的HttpURLConnection和Apache的HttpComponents等等。
RestTemplate通过HTTP方法提供了一些通用场景的模板，另外也提供了exchange和execute方法来支持不常用的例子。

RestTemplate是一个典型的共享组件。
但是，它的配置不支持并发同时修改，因此通常在创建的时候进行配置。
如果必要的话，你可以创建多个不同配置的RestTemplate实例。

5.0后，建议使用更现代化的，支持同步、异步和流的API：org.springframework.web.reactive.client.WebClient 
操了~

# 方法使用练习
RestTemplate的主要方法

发送GET请求的方法有两类
- getForEntity
  - `<T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)`
  - `<T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)`
  - `<T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType)`
- getForObject
  - `<T> T getForObject(String url, Class<T> responseType, Object... uriVariables)`
  - `<T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables)`
  - `<T> T getForObject(URI url, Class<T> responseType)`

可以看到这两类的重载方法都需要传递的第2个参数是响应体类型，它是一个Class对象，表示请求的响应体应该转为什么类型。
getForObject和getForEntity大体是一样的，
区别是getForObject直接将响应体转换为Pojo，而getForEntity将响应转换为entity，其中entity包含header和转换后的pojo。

探究HttpMessageConverter
HttpMessageConverter是一个策略接口类，将请求体或响应体中的数据与数据类型互相转换。
RestTemplate默认包含如下的HttpMessageConverter:
org.springframework.http.converter.ByteArrayHttpMessageConverter
org.springframework.http.converter.StringHttpMessageConverter
org.springframework.http.converter.ResourceHttpMessageConverter
org.springframework.http.converter.xml.SourceHttpMessageConverter
org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter
org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter
org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

详细输出可见用例：
com.example.resttemplatedemo.RestTemplateDemoApplicationTests.test_default_convertor_and_its_support_media

# 源码分析  

