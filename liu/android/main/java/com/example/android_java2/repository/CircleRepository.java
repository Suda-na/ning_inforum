package com.example.android_java2.repository;

import com.example.android_java2.model.CircleComment;
import com.example.android_java2.model.CirclePost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 圈子动态本地仓库，后续可替换为 SSM + DB。
 */
public class CircleRepository {

    private static final List<CirclePost> posts = new ArrayList<>();
    private static final Map<String, List<CircleComment>> comments = new HashMap<>();

    static {
        CirclePost p1 = new CirclePost("p1", "一块一块", "ic_user_avatar", "今天 17:56",
                "四六级缺考怎么申？", "请问四六级考试怎么申请缺考？", "校园求助", 1896, 10, 56,
                Arrays.asList("https://via.placeholder.com/300"));
        CirclePost p2 = new CirclePost("p2", "糖果碗粥", "ic_user_avatar", "今天 15:20",
                "图书馆有自习位吗", "求一个今晚自习位，最好带插座", "校园日常", 876, 4, 22,
                Arrays.asList("https://via.placeholder.com/300", "https://via.placeholder.com/300"));
        CirclePost p3 = new CirclePost("p3", "小云", "ic_user_avatar", "昨天 20:10",
                "校园拍照打卡点", "有无好看的秋日打卡点推荐", "校园拍照", 2034, 18, 102,
                Arrays.asList("https://via.placeholder.com/300"));
        posts.add(p1);
        posts.add(p2);
        posts.add(p3);

        comments.put("p1", new ArrayList<CircleComment>() {{
            add(new CircleComment("c1", "p1", "糖果碗粥", "不用申请，不会被禁考的", "4小时前"));
            add(new CircleComment("c2", "p1", "一块一块", "确定不会被禁考吗？", "4小时前"));
            add(new CircleComment("c3", "p1", "糖果碗粥", "不会", "4小时前"));
        }});
        comments.put("p2", new ArrayList<CircleComment>() {{
            add(new CircleComment("c4", "p2", "路过的猫", "今晚可能比较满，早点去", "1小时前"));
        }});
        comments.put("p3", new ArrayList<CircleComment>() {{
            add(new CircleComment("c5", "p3", "阿月", "图书馆西侧银杏很美", "昨天"));
        }});
    }

    public static List<CirclePost> getPosts() {
        return new ArrayList<>(posts);
    }

    public static CirclePost getPost(String id) {
        for (CirclePost p : posts) {
            if (p.getId().equals(id)) return p;
        }
        return null;
    }

    public static List<CircleComment> getComments(String postId) {
        List<CircleComment> list = comments.get(postId);
        if (list == null) return new ArrayList<>();
        return new ArrayList<>(list);
    }

    public static void addComment(String postId, String author, String content) {
        List<CircleComment> list = comments.get(postId);
        if (list == null) {
            list = new ArrayList<>();
            comments.put(postId, list);
        }
        String id = "c" + System.currentTimeMillis();
        list.add(new CircleComment(id, postId, author, content, "刚刚"));
        CirclePost post = getPost(postId);
        if (post != null) {
            post.setComments(list.size());
        }
    }
}


