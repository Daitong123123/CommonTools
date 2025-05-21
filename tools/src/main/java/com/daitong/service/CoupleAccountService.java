package com.daitong.service;

import com.daitong.repository.CoupleAccountRecordRepository;
import com.daitong.repository.entity.CoupleAccountRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CoupleAccountService {

    @Autowired
    private CoupleAccountRecordRepository recordRepository;

    /**
     * 获取情侣双方的所有账单记录
     */
    public List<CoupleAccountRecord> getRecordsByCoupleId(String coupleId) {
        return recordRepository.getRecordsByCoupleId(coupleId);
    }

    /**
     * 获取单个用户的账单记录
     */
    public List<CoupleAccountRecord> getRecordsByUserId(String userId) {
        return recordRepository.getRecordsByUserId(userId);
    }

    /**
     * 获取情侣双方指定月份的账单记录
     */
    public List<CoupleAccountRecord> getRecordsByCoupleIdAndMonth(String coupleId, Date startDate, Date endDate) {
        return recordRepository.getRecordsByCoupleIdAndMonth(coupleId, startDate, endDate);
    }

    /**
     * 获取单个用户指定月份的账单记录
     */
    public List<CoupleAccountRecord> getRecordsByUserIdAndMonth(String userId, Date startDate, Date endDate) {
        return recordRepository.getRecordsByUserIdAndMonth(userId, startDate, endDate);
    }

    /**
     * 添加新的账单记录
     */
    public boolean addRecord(CoupleAccountRecord record) {
        return recordRepository.addRecord(record);
    }

    /**
     * 更新账单记录
     */
    public boolean updateRecord(CoupleAccountRecord record) {
        return recordRepository.updateRecord(record);
    }

    /**
     * 删除账单记录
     */
    public boolean deleteRecord(Long id) {
        return recordRepository.deleteRecord(id);
    }
}