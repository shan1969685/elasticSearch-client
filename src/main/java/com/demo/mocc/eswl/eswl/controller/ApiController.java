package com.demo.mocc.eswl.eswl.controller;

import com.demo.mocc.eswl.eswl.UserBook;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @program eswl
 * @description: d
 * @author: sr
 * @create: 2019/09/01 16:17
 */
@Slf4j
@RestController
public class ApiController {



    @Autowired
    private TransportClient client ;

    @GetMapping("/book")
    @ResponseBody
    public ResponseEntity get(@RequestParam(name ="id")String id){
        GetResponse response = this.client.prepareGet("book","novel",id).get();
        return  new ResponseEntity(response, HttpStatus.OK);
    }

    @PostMapping("/book/novel")
    @ResponseBody
    public  ResponseEntity add(@RequestParam(name = "name") String name ,
                               @RequestParam(name = "country") String country,
                               @RequestParam(name  ="age") String age,
                               @RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date date  ) throws IOException {
        date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.info(format.format(date));
        XContentBuilder xContentBuilder = XContentFactory.jsonBuilder().startObject()
                .field("name",name).field("country" ,country).field("age",age).field("date","2019-09-03" )
                .endObject();
        IndexResponse  response = this.client.prepareIndex("book","novel").setSource(xContentBuilder).get();
        return  new ResponseEntity(response.getId() ,HttpStatus.OK);
    }

    @DeleteMapping("/book/novel")
    @ResponseBody
    public ResponseEntity delete(@RequestParam (name = "id") String id ){
        DeleteResponse deleteRequestBuilder = this.client.prepareDelete("book","novel",id).get();
        return  new ResponseEntity( deleteRequestBuilder.getResult(),HttpStatus.OK);
    }

    @PutMapping("/book/novel")
    @ResponseBody
    public  ResponseEntity update (@RequestParam (value = "id") String id,
                                   @RequestParam(value = "name",required = false)String name) throws IOException, ExecutionException, InterruptedException {
        UpdateRequest update =  new UpdateRequest("book","novel",id);
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject();
        if (name != null) {
            builder.field("name",name);
        }
        builder.endObject();
        update.doc(builder);
        UpdateResponse response=  this.client.update(update).get();
        return  new ResponseEntity(response.getResult(),HttpStatus.OK);

    }


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
    
}



