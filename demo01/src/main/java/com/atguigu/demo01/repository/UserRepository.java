package com.atguigu.demo01.repository;

import com.atguigu.demo01.POJO.User;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


/**
 * @Auther: 宋金城
 * @Date: 2020/1/8 10:02
 * @Description:
 */
public interface UserRepository extends ElasticsearchRepository<User,Long> {
    List<User> findByAgeBetween(Integer age1, Integer age2);

    @Query("{\n" +
            "    \"range\": {\n" +
            "      \"age\": {\n" +
            "        \"gte\": \"?0\",\n" +
            "        \"lte\": \"?1\"\n" +
            "      }\n" +
            "    }\n" +
            "  }")
    List<User> findByQuery(Integer age,Integer age2);
    User findByName(String name);
    List<User> findByNameNot(String name);
    List<User> findByNameAndAge(String name,Integer age);
    List<User> findByNameOrderByAgeDesc(String name);

}
