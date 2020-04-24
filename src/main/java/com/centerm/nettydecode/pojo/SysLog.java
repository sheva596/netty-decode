package com.centerm.nettydecode.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @author Sheva
 * @date 2020/4/23 11:12
 * @description
 */

@Data
public class SysLog {
    private Long id;
    private Date createTime;
    private String description;
    private String username;
    private String params;
    private String reqIp;
    private String logType;
    private byte[] exceptionDetail;
    private String method;
    private Long executeTime;
}
