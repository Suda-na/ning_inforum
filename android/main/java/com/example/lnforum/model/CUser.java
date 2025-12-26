package com.example.lnforum.model;

public class CUser {
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
    private int followingCount;
    private int fansCount;
    private int likeCount;

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    // Getters
    public Integer getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getRealName() { return realName; }
    public String getPassword() { return password; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAvatar() { return avatar; }
    public Integer getGender() { return gender; }
    public String getSignature() { return signature; }
    public String getAddress() { return address; }

    // Setters
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setRealName(String realName) { this.realName = realName; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public void setGender(Integer gender) { this.gender = gender; }
    public void setSignature(String signature) { this.signature = signature; }
    public void setAddress(String address) { this.address = address; }

    // 辅助方法：显示性别文字
    public String getGenderText() {
        if (gender == null) return "未知";
        if (gender == 1) return "男";
        if (gender == 2) return "女";
        return "保密";
    }
}