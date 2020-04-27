package com.centerm.nettydecode.service.impl;

import com.centerm.nettydecode.dao.SysDao;
import com.centerm.nettydecode.pojo.ReqRecord;
import com.centerm.nettydecode.pojo.SysLog;
import com.centerm.nettydecode.pojo.Terminal;
import com.centerm.nettydecode.service.SysService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Sheva
 * @date 2020/4/23 11:40
 * @description
 */

@Service
@Slf4j
public class SysServiceImpl implements SysService {

    @Autowired
    private SysDao sysDao;
    @Autowired
    private RedisTemplate redisTemplate;

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

    /**
     * 更新设备策略，先更新表，再删除原来的缓存，再更新新的缓存
     * @param id 设备id
     */
    @Override
    public Boolean updateReqTimes(Long id) {
        return sysDao.updateReqTimes(id);
    }

    /**
     * 获取终端策略：先从缓存中获取，如果没有则读取mysql数据，再写入缓存
     * @param sn
     * @return
     */
    @Override
    public Terminal findBySn(String sn) {
        String key = "terminal_" + sn;
        ValueOperations<String, Terminal> operations = redisTemplate.opsForValue();
        //判断redis中是否有键为key的缓存
        boolean hasKey = redisTemplate.hasKey(key);
        if (hasKey){
            Terminal terminal = operations.get(key);
            log.info("缓存中获取数据： " + terminal);
            log.info("---------------------------------------");
            return terminal;
        }else{
            Terminal terminal = sysDao.findBySn(sn);
            log.info("查询数据库数据：" + terminal);
            log.info("---------------------------------------");
            operations.set(key, terminal, 1, TimeUnit.HOURS);
            return terminal;
        }
    }
}
