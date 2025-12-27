package com.example.lnforum.repository;

import com.example.lnforum.model.SecondHandItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SecondHandRepository {
    public static List<SecondHandItem> getItems() {
        List<SecondHandItem> list = new ArrayList<>();
        list.add(new SecondHandItem(
                "1",
                "狐友169270172863007",
                "今天 18:01",
                "闲置一水间防晒 95新 45出 可小刀",
                "有购买记录，几乎全新，附官方订单截图。",
                "其他",
                "¥45.00",
                "宁夏大学",
                54,
                Arrays.asList("https://via.placeholder.com/300", "https://via.placeholder.com/300")
        ));
        list.add(new SecondHandItem(
                "2",
                "小鬼2",
                "今天 09:27",
                "出床上桌25r 带抽屉",
                "宿舍收拾出的一张床上小桌，完好可折叠。",
                "学习用品",
                "¥25.00",
                "贺兰山校区",
                142,
                Arrays.asList("https://via.placeholder.com/300")
        ));
        list.add(new SecondHandItem(
                "3",
                "Kn耶",
                "今天 09:11",
                "出一个猫爪暖手宝 没用过",
                "带充电器，两档调节，粉色可爱款，几乎全新。",
                "其他",
                "¥8.00",
                "宁夏大学",
                3172,
                Arrays.asList("https://via.placeholder.com/300", "https://via.placeholder.com/300")
        ));
        return list;
    }

    public static SecondHandItem getItem(String id) {
        for (SecondHandItem item : getItems()) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }
}


