package com.atguigu.demo01.POJO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @Auther: 宋金城
 * @Date: 2020/1/7 19:00
 * @Description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "student",type = "VIP",shards = 3,replicas = 2)
public class User {
    @Id
    private Long id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String name;

    @Field(type = FieldType.Keyword)
    private Integer age;

    @Field(type = FieldType.Keyword,index = false)
    private String password;
}
