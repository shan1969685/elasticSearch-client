# elasticSearch +Java 使用 client  项目笔记

  ## 1.本项目使用 spingboot + elasticSearch 连接客户端，
     
      主要引用xml为：
      ```xml
      
        <dependency>
            <groupId>org.elasticsearch.client</groupId>
            <artifactId>transport</artifactId>
            <version>6.4.3</version>
        </dependency>
        
      ```
      
      
  ## 2. 使用 jar
  ### 1 注入连接elstaticSearch 的 必要 参数
      
       
        方式1 
  ```Java   
        @Configuration
        public class MyConfig {
            @Bean
            public TransportClient client ()throws  Exception{
                Settings settings = Settings.builder().put("cluster.name","xiaoming").build();
                TransportClient client = new PreBuiltTransportClient(settings);
                client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
                return  client;
            }
        }
      
  ``` 
      
   ### 2 使用
       
   ####      详见 APIController
       
      
   ### get  
        
          GetResponse response = this.client.prepareGet("book","novel",id).get();
        
   ### post Save
        
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                        .field("name",name).field("country" ,country).field("age",age).field("date","2019-09-03" )
                        .endObject();
                IndexResponse  response = this.client.prepareIndex("book","novel").setSource(xContentBuilder).get();  
   ### DELETE 
   
   
       DeleteResponse deleteRequestBuilder = this.client.prepareDelete("book","novel",id).get();
   
   
  ### UODATE 
  
      UpdateRequest update =  new UpdateRequest("book","novel",id);
      XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
      if (name != null) {
          builder.field("name",name);
      }
      builder.endObject();
      update.doc(builder);
      UpdateResponse response=  this.client.update(update).get(); 
   
   
  ###    复合查询 
  
         @PostMapping("/book/novel/query")
         @ResponseBody
         public  ResponseEntity query(@RequestParam(value = "name",required = false)String name ,
                                      @RequestParam(value = "age",required = false) Integer age,
                                      @RequestParam(value = "lgt" ,required =  false) String lgt ){
             BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
             if (name!= null ){
                // boolQueryBuilder.must(QueryBuilders.matchQuery("name",name));
              //   boolQueryBuilder.must(QueryBuilders.matchAllQuery());
                 boolQueryBuilder.must(QueryBuilders.multiMatchQuery(name,"name","country"));
             }
                RangeQueryBuilder rangeQueryBuilder =QueryBuilders.rangeQuery("age").format("0");
             if(lgt!= null){
                 rangeQueryBuilder.to(lgt);
             }
             boolQueryBuilder.filter(rangeQueryBuilder);
             SearchRequestBuilder builder=  this.client.prepareSearch("book")
                     .setTypes("novel").setSearchType(SearchType.DEFAULT)
                     .setQuery(boolQueryBuilder).setFrom(0).setSize(10);
             log.info("build"+builder);
     
             SearchResponse response = builder.get();
             List<Map<String,Object>> res= new ArrayList<>();
             for(SearchHit hit :response.getHits()){
                 res.add(hit.getSourceAsMap());
             }
     
             StringBuffer sb = new StringBuffer();
             sb.append("")
     
                 .append("");
             UserBook userBook = new UserBook() ;
             userBook.toString();
             return new ResponseEntity(res,HttpStatus.OK);
         }
   
   ## 相关博客
   

    ```thymeleafurlexpressions
    https://blog.csdn.net/prestigeding/article/details/83188043   
    ```
    
    