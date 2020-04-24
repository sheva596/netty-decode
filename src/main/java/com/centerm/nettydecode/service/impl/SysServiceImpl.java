package com.centerm.nettydecode.service.impl;

import com.centerm.nettydecode.dao.SysDao;
import com.centerm.nettydecode.pojo.ReqRecord;
import com.centerm.nettydecode.pojo.SysLog;
import com.centerm.nettydecode.service.SysService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Sheva
 * @date 2020/4/23 11:40
 * @description
 */

@Service
public class SysServiceImpl implements SysService {

    @Autowired
    private SysDao sysDao;
    @Override
    public void saveSysLog(SysLog sysLog) {
        sysDao.saveSysLog(sysLog);
    }

    @Override
    public void addTerminal(String sn) {
        sysDao.addTerminal(sn);
    }

    @Override
    public void addReqRecord(ReqRecord reqRecord) {
        sysDao.addReqRecord(reqRecord);
    }

    @Override
    public void updateReqTimes(Long id) {
        sysDao.updateReqTimes(id);
    }

    @Override
    public Long findBySn(String sn) {
        return sysDao.findBySn(sn);
    }
}
