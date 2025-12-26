package com.niit.dao;

import com.niit.pojo.Post;
import com.niit.pojo.PostVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 帖子DAO接口
 */
@Mapper
public interface PostMapper {
    /**
     * 根据ID查询帖子详情（包含用户和分类信息）
     * @param postId 帖子ID
     * @return 帖子VO对象
     */
    PostVO selectByPrimaryKey(@Param("postId") Integer postId);

    /**
     * 查询所有帖子（包含用户和分类信息）
     * @return 帖子VO列表
     */
    List<PostVO> selectAll();

    /**
     * 根据条件查询帖子
     * @param params 查询参数Map
     * @return 帖子VO列表
     */
    List<PostVO> selectByCondition(Map<String, Object> params);

    /**
     * 根据条件统计帖子数量
     * @param params 查询参数Map
     * @return 帖子数量
     */
    int countByCondition(Map<String, Object> params);

    /**
     * 更新帖子状态
     * @param postId 帖子ID
     * @param status 状态：0待审核, 1已通过, 2已删除, 3已结束
     * @param reviewTime 审核时间
     * @return 更新的行数
     */
    int updateStatus(@Param("postId") Integer postId, 
                     @Param("status") Integer status,
                     @Param("reviewTime") java.util.Date reviewTime);

    /**
     * 根据状态查询帖子数量
     * @param status 状态：0待审核, 1已通过, 2已删除, 3已结束
     * @return 帖子数量
     */
    int countByStatus(@Param("status") Integer status);
}

