package com.app.pojo;

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
    // ... 其他字段可以根据需要加，核心是以上这些

    // 必须要有 Getter 和 Setter
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}


