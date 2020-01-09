package com.atguigu.demo01;

import com.alibaba.fastjson.JSON;
import com.atguigu.demo01.POJO.User;
import com.atguigu.demo01.repository.UserRepository;
import jdk.management.resource.ResourceRequest;
import org.apache.lucene.index.Terms;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;


import javax.security.auth.Subject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;


@SpringBootTest
class Demo01ApplicationTests {
    //这两个类都是属于spring.data
    @Autowired
    private ElasticsearchRestTemplate restTemplate;
    @Autowired//可以完成百分之九十的任务，但是高亮比较麻烦
    private UserRepository userRepository;//可以按照约定自定义查询方法findByName，可以加Query注解，在注解内添加es语法格式


    //需要加配置文件，属于RestHighLevelClient，原生客户端elasticsearch自带的，
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
    void contextLoads() {
     this.restTemplate.createIndex(User.class);
        this.restTemplate.putMapping(User.class);

       // this.restTemplate.deleteIndex(User.class);
    }

    @Test
    void testAdd() {
        this.userRepository.save(new User(1L,"蔡徐坤",20,"123456"));
        List<User> list=new ArrayList<>();
        list.add(new User(2L,"鹿晗",21,"123456"));
        list.add(new User(3L,"吴亦凡",20,"123456"));
        list.add(new User(4L,"刘昊然",22,"123456"));
        list.add(new User(5L,"杨洋",23,"654321"));
        list.add(new User(6L,"李易峰",24,"654321"));
        list.add(new User(7L,"张艺兴",26,"654321"));
        list.add(new User(8L,"蔡徐坤",21,"654321"));
        list.add(new User(10L,"蔡徐坤",22,"654321"));
        list.add(new User(11L,"蔡徐坤",23,"654321"));
        this.userRepository.saveAll(list);
    }
    @Test
    void testDelete() {
        //根据对象删除
        // this.userRepository.delete(new User(4L,"刘昊然",22,"123456"));
        //根据id删除
        //this.userRepository.deleteById(4L);
    }


    @Test
    void testFind() {
        /*Optional<User> user = this.userRepository.findById(1L);
        System.out.println(user);*/
        //第一种方式,spring data自带的，根据方法名称自动实现功能
        //this.userRepository.findByAgeBetween(10, 22).stream().forEach(System.out::println);
        //第二种方式
        //使用注解方式，使用@Query注解
     // this.userRepository.findByQuery(10,22).stream().forEach(System.out::println);
/*        System.out.println(this.userRepository.findByName("蔡徐坤"));
        this.userRepository.findByNameNot("蔡徐坤").stream().forEach(System.out::println);*/
        //this.userRepository.findAllById(Arrays.asList(1L,2L,3L)).forEach(System.out::println);
        //this.userRepository.findAll();
        //this.userRepository.findByNameAndAge("蔡徐坤",22).forEach(System.out::println);
       // this.userRepository.findByAvailableTrueOrderByNameDesc().forEach(System.out::println);
        //this.userRepository.findByNameOrderByAgeDesc("蔡徐坤").forEach(System.out::println);


    }

    /**
     *  @author: 宋金城
     *  @Date: 2020/1/9 11:18
     *  @Description:
     *  使用elasticsearchRepository实现分页功能
     *  PageRequest.of(起始页，每页多少条记录)
     *  起始页是从零开始的，
     */
    @Test
    void searchDemo() {
        //QueryBuilders查询条件构建器
        //范围查询
        //this.userRepository.search(QueryBuilders.rangeQuery("age").gte(20).lte(22)).forEach(System.out::println);
        //pageRequest page起始页是从零开始，需要减一
        //分页查询
       /* Page<User> page = this.userRepository.search(QueryBuilders.rangeQuery("age"), PageRequest.of(1, 1));
        page.getContent().forEach(System.out::println);
        System.out.println("一共有"+page.getTotalPages());
        System.out.println("一共有对象:"+page.getTotalElements());*/
       /**
        *  @author: 宋金城
        *  @Date: 2020/1/9 13:48
        *  @Description:
        *
        */
       //初始化自定义查询构建器,(searchQuery)该构造方法使用比较多，无法获取高亮结果集,用于构建自定义查询

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //条件
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name","蔡徐坤").operator(Operator.OR));
        //范围
        nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
        //分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(1,2));
        //高亮
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().field("name").preTags("<em>").postTags("</em>"));
        //聚合,    聚合名称 ，聚合字段
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("passwordAgg").field("password"));
        //Page<User> search = this.userRepository.search(nativeSearchQueryBuilder.build());有聚合不要使用page来接收，接收不到聚合信息
        AggregatedPage aggregatedPage=(AggregatedPage) this.userRepository.search(nativeSearchQueryBuilder.build());
        aggregatedPage.getContent().forEach(System.out::println);
        System.out.println("总记录"+aggregatedPage.getTotalElements());
        System.out.println("总页数"+aggregatedPage.getTotalPages());
        ParsedStringTerms terms =(ParsedStringTerms) aggregatedPage.getAggregation("passwordAgg");
        terms.getBuckets().forEach(bucket -> {
            System.out.println(bucket.getKeyAsString());
        });

    }

    @Test
    void searchDemo2() {
        //进行高亮显示restTemplate
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        //条件
        nativeSearchQueryBuilder.withQuery(QueryBuilders.matchQuery("name","蔡徐坤").operator(Operator.OR));
        //范围
        nativeSearchQueryBuilder.withSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
        //分页
        nativeSearchQueryBuilder.withPageable(PageRequest.of(1,2));
        //高亮
        nativeSearchQueryBuilder.withHighlightBuilder(new HighlightBuilder().field("name").preTags("<em>").postTags("</em>"));
        //聚合,    聚合名称 ，聚合字段
        nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("passwordAgg").field("password"));
        //Page<User> search = this.userRepository.search(nativeSearchQueryBuilder.build());有聚合不要使用page来接收，接
        this.restTemplate.query(nativeSearchQueryBuilder.build(),response->{
            SearchHit[] searchHits = response.getHits().getHits();
            for (SearchHit searchHites:
                 searchHits) {
                String sourceAsString = searchHites.getSourceAsString();
                //进行反序列化
                User user = JSON.parseObject(sourceAsString, User.class);
                // System.out.println("使用josn串接收"+sourceAsString);
                Map<String, HighlightField> highlightFields = searchHites.getHighlightFields();
                HighlightField highlightField = highlightFields.get("name");
                String name = highlightField.getFragments()[0].string();
                user.setName(name);
                System.out.println(user);
            }
            Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
            ParsedTerms parsedTerms =(ParsedTerms) asMap.get("passwordAgg");
            parsedTerms.getBuckets().forEach(bucket->{
                System.out.println(bucket.getKeyAsString());
            });


            return null;
        });

    }
    @Test
    void searchDemo3() throws IOException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name","坤").operator(Operator.OR));
        searchSourceBuilder.sort("age",SortOrder.DESC);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(5);
        searchSourceBuilder.highlighter(new HighlightBuilder().field("name").preTags("<em>").postTags("</em>"));
        searchSourceBuilder.aggregation(AggregationBuilders.terms("passwordAgg").field("password"));
        //第一个参数为，你的索引库名称，第二个是你构建的查询条件（高亮，页数，范围查询），此对象是elasticsearch原生自带,第三个参数为请求选项，选择默认
        SearchResponse response = this.restHighLevelClient.search(new SearchRequest(new String[]{"student"}, searchSourceBuilder), RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        for (SearchHit searchHites:
                searchHits) {
            String sourceAsString = searchHites.getSourceAsString();
            //进行反序列化
            User user = JSON.parseObject(sourceAsString, User.class);
            // System.out.println("使用josn串接收"+sourceAsString);
            //该高亮集合，String是你的字段的名称，HighlightField是高亮字段，封装为一个map集合
            Map<String, HighlightField> highlightFields = searchHites.getHighlightFields();
            System.out.println("从高亮集合中获取的数据："+highlightFields.get("name"));
            HighlightField highlightField = highlightFields.get("name");
            user.setName(highlightField.getFragments()[0].string());
            System.out.println(user);
        }
        Map<String, Aggregation> asMap = response.getAggregations().getAsMap();
        ParsedTerms parsedTerms =(ParsedTerms) asMap.get("passwordAgg");
        parsedTerms.getBuckets().forEach(bucket->{
            System.out.println(bucket.getKeyAsString());
        });

    }




}
