package com.app.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * API 响应状态码常量
 */
public class ApiResponse {
    
    /**
     * 成功状态码
     */
    public static final int SUCCESS = 200;
    
    /**
     * 客户端错误状态码
     */
    public static final int CLIENT_ERROR = 400;
    
    /**
     * 未授权
     */
    public static final int UNAUTHORIZED = 401;
    
    /**
     * 禁止访问
     */
    public static final int FORBIDDEN = 403;
    
    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;
    
    /**
     * 服务器错误状态码
     */
    public static final int SERVER_ERROR = 500;
    
    /**
     * 默认成功消息
     */
    public static final String DEFAULT_SUCCESS_MESSAGE = "操作成功";
    
    /**
     * 默认失败消息
     */
    public static final String DEFAULT_ERROR_MESSAGE = "操作失败";
}

