package com.daitong.repository;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.daitong.repository.entity.CoupleAccountRecord;
import com.daitong.repository.mapper.CoupleAccountRecordMapper;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * CoupleAccountRecordRepository
 *
 * @since 2025-05-21
 */
@Repository
public class CoupleAccountRecordRepository extends ServiceImpl<CoupleAccountRecordMapper, CoupleAccountRecord> {

    /**
     * 获取情侣双方的所有账单记录
     */
    public List<CoupleAccountRecord> getRecordsByCoupleId(String coupleId) {
        return list(new QueryWrapper<CoupleAccountRecord>()
                .lambda()
                .eq(CoupleAccountRecord::getCoupleId, coupleId)
                .orderByDesc(CoupleAccountRecord::getDate)
                .orderByDesc(CoupleAccountRecord::getCreatedAt));
    }

    /**
     * 获取单个用户的账单记录
     */
    public List<CoupleAccountRecord> getRecordsByUserId(String userId) {
        return list(new QueryWrapper<CoupleAccountRecord>()
                .lambda()
                .eq(CoupleAccountRecord::getUserId, userId)
                .orderByDesc(CoupleAccountRecord::getDate)
                .orderByDesc(CoupleAccountRecord::getCreatedAt));
    }

    /**
     * 获取情侣双方指定月份的账单记录
     */
    public List<CoupleAccountRecord> getRecordsByCoupleIdAndMonth(String coupleId, Date startDate, Date endDate) {
        return list(new QueryWrapper<CoupleAccountRecord>()
                .lambda()
                .eq(CoupleAccountRecord::getCoupleId, coupleId)
                .ge(CoupleAccountRecord::getDate, startDate)
                .le(CoupleAccountRecord::getDate, endDate)
                .orderByDesc(CoupleAccountRecord::getDate)
                .orderByDesc(CoupleAccountRecord::getCreatedAt));
    }

    /**
     * 获取单个用户指定月份的账单记录
     */
    public List<CoupleAccountRecord> getRecordsByUserIdAndMonth(String userId, Date startDate, Date endDate) {
        return list(new QueryWrapper<CoupleAccountRecord>()
                .lambda()
                .eq(CoupleAccountRecord::getUserId, userId)
                .ge(CoupleAccountRecord::getDate, startDate)
                .le(CoupleAccountRecord::getDate, endDate)
                .orderByDesc(CoupleAccountRecord::getDate)
                .orderByDesc(CoupleAccountRecord::getCreatedAt));
    }

    /**
     * 添加新的账单记录
     */
    public boolean addRecord(CoupleAccountRecord record) {
        record.setCreatedAt(new Date());
        record.setUpdatedAt(new Date());
        return save(record);
    }

    /**
     * 更新账单记录
     */
    public boolean updateRecord(CoupleAccountRecord record) {
        record.setUpdatedAt(new Date());
        return updateById(record);
    }

    /**
     * 删除账单记录
     */
    public boolean deleteRecord(Long id) {
        return removeById(id);
    }

    /**
     * 获取账单记录详情
     */
    public Optional<CoupleAccountRecord> getRecordById(Long id) {
        return Optional.ofNullable(getById(id));
    }
}