package com.example.android_java2.repository;

import com.example.android_java2.model.LostFoundItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LostFoundRepository {
    public static List<LostFoundItem> getItems() {
        List<LostFoundItem> list = new ArrayList<>();
        list.add(new LostFoundItem(
                "lf1",
                "阿土",
                "今天 18:10",
                "捡到一张校园卡",
                "操场看台捡到，背面名字张同学，卡面完好。",
                "招领",
                "操场东侧看台",
                43,
                Arrays.asList("https://via.placeholder.com/300")
        ));
        list.add(new LostFoundItem(
                "lf2",
                "小井",
                "今天 16:05",
                "掉了一串黑色钥匙",
                "上面有蓝色卡通挂件，可能掉在图书馆A区或食堂。",
                "失物",
                "图书馆A区/食堂",
                128,
                Arrays.asList("https://via.placeholder.com/300", "https://via.placeholder.com/300")
        ));
        list.add(new LostFoundItem(
                "lf3",
                "七分甜",
                "今天 09:20",
                "捡到粉色雨伞",
                "教学楼一层拾到，伞柄有小熊贴纸。",
                "招领",
                "教学楼一层大厅",
                76,
                Arrays.asList("https://via.placeholder.com/300")
        ));
        return list;
    }

    public static LostFoundItem getItem(String id) {
        for (LostFoundItem item : getItems()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}


