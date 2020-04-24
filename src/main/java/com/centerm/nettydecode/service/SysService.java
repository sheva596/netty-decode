package com.centerm.nettydecode.service;

import com.centerm.nettydecode.pojo.ReqRecord;
import com.centerm.nettydecode.pojo.SysLog;

/**
 * @author Sheva
 * @date 2020/4/23 11:39
 * @description
 */
public interface SysService{

    /**
     * 保存系统日志
     * @param sysLog
     */
    void saveSysLog(SysLog sysLog);

    /**
     * 添加设备记录
     * @param sn
     */
    void addTerminal(String sn);

    /**
     * 添加请求记录
     * @param reqRecord
     */
    void addReqRecord(ReqRecord reqRecord);

    /**
     * 更新请求次数
     * @param id
     */
    void updateReqTimes(Long id);

    /**
     * 根据sn查找设备id
     * @param sn
     * @return
     */
    Long findBySn(String sn);

}