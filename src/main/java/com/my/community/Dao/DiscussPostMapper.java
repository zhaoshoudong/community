package com.my.community.Dao;

import com.my.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    //userId 通过if标签动态获取,系统用户根据id查询自己的发布的文章,非系统用户(游客身份)查看所有已发布的文章
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    //@Param 用来给参数取别名
    //如果方法中只有一个参数,并且在<if>里使用,则必须加别名
    int findDiscussPostRows(@Param("userId") int userId);

    int addDiscussPost(DiscussPost discussPost);

    DiscussPost findDetailPost(int id);

    int updateCommentCount(int id,int commentCount);
}
