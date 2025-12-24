package com.niit.pojo;

import java.util.Date;

public class SystemSetting {
    private Integer id;
    private String siteName;
    private String icpCode;
    private Integer siteStatus;     // 1开启，0维护
    private Integer allowRegister;  // 1允许，0禁止
    private String sensitiveWords;
    private Integer maxImageSize;
    private Date updateTime;

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }
    public String getIcpCode() { return icpCode; }
    public void setIcpCode(String icpCode) { this.icpCode = icpCode; }
    public Integer getSiteStatus() { return siteStatus; }
    public void setSiteStatus(Integer siteStatus) { this.siteStatus = siteStatus; }
    public Integer getAllowRegister() { return allowRegister; }
    public void setAllowRegister(Integer allowRegister) { this.allowRegister = allowRegister; }
    public String getSensitiveWords() { return sensitiveWords; }
    public void setSensitiveWords(String sensitiveWords) { this.sensitiveWords = sensitiveWords; }
    public Integer getMaxImageSize() { return maxImageSize; }
    public void setMaxImageSize(Integer maxImageSize) { this.maxImageSize = maxImageSize; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}