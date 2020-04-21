package com.centerm.nettydecode.pojo;

import lombok.Data;

/**
 * @author Sheva
 * @date 2020/4/21 14:24
 * @description
 */
@Data
public class Result {

    private int code;

    public Result(int code){
        this.code = code;
    }

}
