package com.kaijy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.kaijy.model.Platform;
import com.kaijy.model.User;
import com.kaijy.utils.NumberUtils;

public class DataService {

    /**
     * 获得正常汇总数据
     * 
     * @param platform
     * @param normalWinnerList
     * @return
     */
    public static Platform getNormalTotal(Platform platform, List<User> normalWinnerList) {
        for (User normal : normalWinnerList) {
            // 去除分配的感知时间并倒序排序
            List<Integer> senTimeList = new ArrayList<Integer>();
            for (HashMap.Entry<Integer, Integer> entry : normal.getAllocationMap().entrySet()) {
                senTimeList.add(entry.getValue());
            }
            // 正序排序
            Collections.sort(senTimeList);
            // reverse只是将顺序导致，不是倒序
            Collections.reverse(senTimeList);
            platform.setNormalTime(platform.getNormalTime() + senTimeList.get(0));
            platform.setNormalPay(NumberUtils.addStr(platform.getNormalPay(), normal.getPay()));
        }
        return platform;
    }

    /**
     * 
     * @param platform
     * @param abnormalWinnerList
     * @return
     */
    public static Platform getAbnormalTotal(Platform platform, List<User> abnormalWinnerList) {
        for (User abnormal : abnormalWinnerList) {
            // 去除分配的感知时间并倒序排序
            List<Integer> senTimeList = new ArrayList<Integer>();
            for (HashMap.Entry<Integer, Integer> entry : abnormal.getAllocationMap().entrySet()) {
                senTimeList.add(entry.getValue());
            }
            // 正序排序
            Collections.sort(senTimeList);
            // reverse只是将顺序导致，不是倒序
            Collections.reverse(senTimeList);
            platform.setAbnormalTime(platform.getAbnormalTime() + senTimeList.get(0));
            // 只统计非异常用户支付
            if (abnormal.getCareless() == 0) {
                platform.setAbnormalPay(NumberUtils.addStr(platform.getAbnormalPay(), abnormal.getPay()));
            }
        }
        return platform;
    }

    /**
     * 获取MCD数据
     * 
     * @param platform
     * @param McdWinnerList
     * @return
     */
    public static Platform getMCDTotal(Platform platform, List<User> McdWinnerList) {
        for (User mcd : McdWinnerList) {
            // 去除分配的感知时间并倒序排序
            List<Integer> senTimeList = new ArrayList<Integer>();
            for (HashMap.Entry<Integer, Integer> entry : mcd.getAllocationMap().entrySet()) {
                senTimeList.add(entry.getValue());
            }
            // 正序排序
            Collections.sort(senTimeList);
            // reverse只是将顺序导致，不是倒序
            Collections.reverse(senTimeList);
            platform.setMcdTime(platform.getMcdTime() + senTimeList.get(0));
            platform.setMcdPay(NumberUtils.addStr(platform.getMcdPay(), mcd.getPay()));
        }
        return platform;
    }

}
