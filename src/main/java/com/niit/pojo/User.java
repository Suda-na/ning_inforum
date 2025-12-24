package com.niit.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Data
/**
 * 用户实体类，对应数据库中的user表
 */
public class User {
    private Integer userId;
    private String username;
    private String realName;
    private String password;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private String signature;
    private String address;
    private Integer role;
    private Integer status;
    private Integer warningCount;
    private Date createTime;
    private Date lastLoginTime;
    private UserPermission permission; // 用户权限信息
    private List<UserBanHistory> banHistories; // 用户封禁历史列表
    private transient java.util.Map<String, String> activeBans; // 当前有效的封禁状态描述，transient字段不参与序列化
    private transient java.util.Map<String, Boolean> isPermanentBans; // 标记哪些权限是永久封禁，transient字段不参与序列化
    private Integer unreadCount;    // 未读消息数量

    // Getter和Setter方法
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public UserPermission getPermission() {
        return permission;
    }

    public void setPermission(UserPermission permission) {
        this.permission = permission;
    }

    public List<UserBanHistory> getBanHistories() {
        return banHistories;
    }

    public void setBanHistories(List<UserBanHistory> banHistories) {
        this.banHistories = banHistories;
    }

    public java.util.Map<String, String> getActiveBans() {
        return activeBans;
    }

    public void setActiveBans(java.util.Map<String, String> activeBans) {
        this.activeBans = activeBans;
    }

    public java.util.Map<String, Boolean> getIsPermanentBans() {
        return isPermanentBans;
    }

    public void setIsPermanentBans(java.util.Map<String, Boolean> isPermanentBans) {
        this.isPermanentBans = isPermanentBans;
    }
}
