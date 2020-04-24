package com.centerm.nettydecode.dao;

import com.centerm.nettydecode.pojo.ReqRecord;
import com.centerm.nettydecode.pojo.SysLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Sheva
 * @date 2020/4/23 11:20
 * @description
 */
@Mapper
public interface SysDao {
    /**
     * 保存系统日志
     * @param sysLog
     */
    void saveSysLog(SysLog sysLog);


    /**
     * 添加设备信息
     * @param sn 设备sn号
     */
    void addTerminal(String sn);

    /**
     * 添加设备请求记录
     * @param reqRecord
     */
    void addReqRecord(ReqRecord reqRecord);

    /**
     * 通过sn号查找设备id
     * @param sn
     * @return
     */
    Long findBySn(String sn);

    /**
     * 更新终端访问次数
     * @param id 设备id
     */
    void updateReqTimes(Long id);
}
