package com.centerm.nettydecode.pojo;

import lombok.Data;

/**
 * @author Sheva
 * @date 2020/4/24 10:44
 * @description
 */
@Data
public class ReqRecord {

    private Long id;
    private String sn;
    private String ip;
    private byte[] reqData;
    private byte[] rspData;
    private Long executeTime;

}
