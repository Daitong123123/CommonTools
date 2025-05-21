package com.daitong.controller;

import com.daitong.bo.common.BaseResponse;
import com.daitong.bo.common.CommonResponse;
import com.daitong.manager.UserManager;
import com.daitong.repository.entity.CoupleAccountRecord;
import com.daitong.service.CoupleAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/account")
public class CoupleAccountController {

    @Autowired
    private CoupleAccountService accountService;

    /**
     * 获取情侣双方的账单记录（支持按月份查询）
     */
    @GetMapping("/records/couple")
    public BaseResponse getCoupleRecords(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        BaseResponse response = new BaseResponse();
        try {
            String coupleId = UserManager.getCurrentUser().getCoupleId();
            List<CoupleAccountRecord> records;

            if (startDate != null && endDate != null) {
                // 转换日期格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                // 查询指定月份的记录
                records = accountService.getRecordsByCoupleIdAndMonth(coupleId, start, end);
            } else {
                // 查询所有记录
                records = accountService.getRecordsByCoupleId(coupleId);
            }

            response.setCode("200");
            response.setMessage("查询成功");
            response.setData(records);
        } catch (ParseException e) {
            response.setCode("400");
            response.setMessage("日期格式错误，应为yyyy-MM-dd");
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("查询失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 获取单个用户的账单记录（支持按月份查询）
     */
    @GetMapping("/records/user")
    public BaseResponse getUserRecords(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        BaseResponse response = new BaseResponse();
        try {
            String userId = UserManager.getCurrentUser().getUserId();
            List<CoupleAccountRecord> records;

            if (startDate != null && endDate != null) {
                // 转换日期格式
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date start = sdf.parse(startDate);
                Date end = sdf.parse(endDate);

                // 查询指定月份的记录
                records = accountService.getRecordsByUserIdAndMonth(userId, start, end);
            } else {
                // 查询所有记录
                records = accountService.getRecordsByUserId(userId);
            }

            response.setCode("200");
            response.setMessage("查询成功");
            response.setData(records);
        } catch (ParseException e) {
            response.setCode("400");
            response.setMessage("日期格式错误，应为yyyy-MM-dd");
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("查询失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 添加新的账单记录
     */
    @PostMapping("/add")
    public CommonResponse addRecord(@RequestBody CoupleAccountRecord record) {
        CommonResponse response = new CommonResponse();
        try {
            // 设置当前用户ID和情侣ID
            if (record.getUserId() == null) {
                record.setUserId(UserManager.getCurrentUser().getUserId());
            }
            record.setCoupleId(UserManager.getCurrentUser().getCoupleId());

            boolean success = accountService.addRecord(record);
            if (success) {
                response.setCode("200");
                response.setMessage("添加成功");
            } else {
                response.setCode("500");
                response.setMessage("添加失败");
            }
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("添加失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 更新账单记录
     */
    @PostMapping("/update")
    public CommonResponse updateRecord(@RequestBody CoupleAccountRecord record) {
        CommonResponse response = new CommonResponse();
        try {
            boolean success = accountService.updateRecord(record);
            if (success) {
                response.setCode("200");
                response.setMessage("更新成功");
            } else {
                response.setCode("500");
                response.setMessage("更新失败");
            }
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("更新失败: " + e.getMessage());
        }
        return response;
    }

    /**
     * 删除账单记录
     */
    @PostMapping("/delete")
    public CommonResponse deleteRecord(@RequestBody DeleteRequest request) {
        CommonResponse response = new CommonResponse();
        try {
            boolean success = accountService.deleteRecord(request.getId());
            if (success) {
                response.setCode("200");
                response.setMessage("删除成功");
            } else {
                response.setCode("500");
                response.setMessage("删除失败");
            }
        } catch (Exception e) {
            response.setCode("500");
            response.setMessage("删除失败: " + e.getMessage());
        }
        return response;
    }

    // 内部类用于接收删除请求
    private static class DeleteRequest {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}