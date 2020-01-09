package com.atguigu;

import com.atguigu.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/7 18:33
 * @Description:
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EsDemoApplicationTests {
    @Autowired
    //ElasticsearchTemplate elasticsearchTemplate;


    @Test
    public void test01(){
        System.out.println("sss");
      /*  this.elasticsearchTemplate.createIndex(User.class);
        this.elasticsearchTemplate.putMapping(User.class);*/
    }
}
