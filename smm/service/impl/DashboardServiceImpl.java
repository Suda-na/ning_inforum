package com.niit.service.impl;

import com.niit.dao.DashboardPostDao;
import com.niit.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 数字大屏服务实现类，实现DashboardService接口
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    private DashboardPostDao dashboardPostDao;

    @Override
    public int countTodayPosts() {
        return dashboardPostDao.countTodayPosts();
    }

    @Override
    public int countPendingReports() {
        return dashboardPostDao.countPendingReports();
    }

    @Override
    public int countBannedUsers() {
        return dashboardPostDao.countBannedUsers();
    }

    @Override
    public List<Map<String, Object>> getLast7DaysNewUsers() {
        return dashboardPostDao.getLast7DaysNewUsers();
    }

    @Override
    public List<Map<String, Object>> getLast7DaysInteractions() {
        return dashboardPostDao.getLast7DaysInteractions();
    }

    @Override
    public List<Map<String, Object>> getLast7DaysNewReports() {
        return dashboardPostDao.getLast7DaysNewReports();
    }

    @Override
    public List<Map<String, Object>> getReportTypeCounts() {
        return dashboardPostDao.getReportTypeCounts();
    }

    @Override
    public List<Map<String, Object>> getCategoryPostCounts() {
        return dashboardPostDao.getCategoryPostCounts();
    }

    @Override
    public List<Map<String, Object>> getUserGenderCounts() {
        return dashboardPostDao.getUserGenderCounts();
    }
}

