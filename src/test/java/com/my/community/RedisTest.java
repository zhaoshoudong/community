package com.my.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String key = "test:string";
        redisTemplate.opsForValue().set(key, "zhangsan");
        System.out.println(redisTemplate.opsForValue().get(key));
        System.out.println(redisTemplate.opsForValue().increment(key));
        System.out.println(redisTemplate.opsForValue().decrement(key));
    }

    @Test
    public void testLists() {
        String key = "test:list";
        //从左面放入一个元素到list中,先push的会在list最下方(右)
        redisTemplate.opsForList().leftPush(key, "Jordan");
        redisTemplate.opsForList().leftPush(key, "Kobe");
        redisTemplate.opsForList().leftPush(key, "James");
        redisTemplate.opsForList().leftPush(key, "WeiDe");
        redisTemplate.opsForList().leftPush(key, "Oneal");
        //返回键为key的list中元素的总个数
        System.out.println(redisTemplate.opsForList().size(key));
        //返回list中所有的元素
        System.out.println(redisTemplate.opsForList().range(key, 0, -1));
        //返回list中下标为 0 的元素
        System.out.println(redisTemplate.opsForList().index(key, 0));
        //从左面弹出list中一个元素
        System.out.println(redisTemplate.opsForList().leftPop(key));
    }

    @Test
    public void testHashes() {
        String key = "test:hashmap";
        redisTemplate.opsForHash().put(key, "username", "lisi");
        System.out.println(redisTemplate.opsForHash().get(key, "username"));
    }

    @Test
    public void testSet() {
        String key = "test:set";
        redisTemplate.opsForSet().add(key, "Chinese", "Math", "English");
//        System.out.println(redisTemplate.opsForSet().pop(key,2));
        System.out.println(redisTemplate.opsForSet().size(key));
        System.out.println(redisTemplate.opsForSet().randomMember(key));
    }

    @Test
    public void testSortedSet() {
        String key = "test:sortedSet";
        redisTemplate.opsForZSet().add(key, "赵四", 80);
        redisTemplate.opsForZSet().add(key, "刘能", 70);
        redisTemplate.opsForZSet().add(key, "谢广坤", 100);
        //返回set中元素的总个数
        System.out.println(redisTemplate.opsForZSet().zCard(key));
        //返回set中所有元素,默认按score的升序排序
        System.out.println(redisTemplate.opsForZSet().range(key, 0, -1));
        System.out.println(redisTemplate.opsForZSet().randomMember(key));
//        System.out.println(redisTemplate.opsForZSet().popMin(key));
//        System.out.println(redisTemplate.opsForZSet().popMax(key));
        //返回赵四的分数
        System.out.println(redisTemplate.opsForZSet().score(key, "赵四"));
        //返回赵四在set中的排名(元素所在的下标值)
        System.out.println(redisTemplate.opsForZSet().reverseRank(key, "赵四"));
        //返回set中所有的元素,按照降序排序
        System.out.println(redisTemplate.opsForZSet().reverseRange(key, 0, -1));
    }

    //多次访问同一个 key
    @Test
    public void testBoundOperations(){
        //先绑定 key,避免出现上面重复调用set key的情况
        String key = "test:count";
        BoundValueOperations keyOps = redisTemplate.boundValueOps(key);
        keyOps.set("赵四");
        System.out.println(keyOps.get());
        BoundListOperations listOps = redisTemplate.boundListOps(key);
        listOps.leftPop();
        listOps.leftPush("1");
    }
    //编程式事务
    @Test
    public void testTransactional(){

    }
}
