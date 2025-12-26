package com.example.android_java2.repository;

import com.example.android_java2.model.ErrandOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * 临时数据仓库，后续对接 SSM/数据库时替换。
 */
public class ErrandRepository {

    public List<ErrandOrder> getSampleOrders() {
        List<ErrandOrder> list = new ArrayList<>();
        list.add(new ErrandOrder(
                "1",
                "帮寄快递",
                "怀远一教开一张成绩单，送到A区文贺楼盖章",
                "需要跑腿顺丰寄出，邮费另算",
                "怀远一教413",
                "菜鸟驿站",
                "25.00",
                "completed"
        ));
        list.add(new ErrandOrder(
                "2",
                "帮取外卖",
                "一份过桥米线，尾号8775",
                "带上一次性手套，注意防洒",
                "宁夏大学文萃校区南门",
                "宁夏大学文萃校区北门",
                "5.00",
                "waiting"
        ));
        list.add(new ErrandOrder(
                "3",
                "帮取快递",
                "代取快递送到图书馆一楼",
                "轻拿轻放，有易碎标识",
                "文萃校区菜鸟驿站",
                "宁夏大学文萃校区图书馆",
                "15.00",
                "delivering"
        ));
        list.add(new ErrandOrder(
                "4",
                "帮送物品",
                "帮忙送羽毛球到文萃北门，很近",
                "尽快送达，联系收件人后放置门口",
                "文萃校区六号楼412",
                "文萃北门",
                "15.00",
                "completed"
        ));
        return list;
    }
}

