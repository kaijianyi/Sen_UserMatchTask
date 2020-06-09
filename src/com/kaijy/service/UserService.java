package com.kaijy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.kaijy.model.User;
import com.kaijy.utils.ConstsUtils;
import com.kaijy.utils.RandomUtils;

public class UserService {

    /**
     * 生成随机用户
     * 
     * @param userNum
     * @param userMinId
     * @param userMaxId
     * @param userMinTime
     * @param userMaxTime
     * @param userMinBid
     * @param userMaxBid
     * @return
     */
    public static List<User> getRandomUser(int userNum, int userMinId, int userMaxId, int userMinTime, int userMaxTime,
            int userMinBid, int userMaxBid) {
        List<User> originUserList = new ArrayList<User>();
        // 防止生成重复数字
        List<Integer> exitList = new ArrayList<Integer>();
        while (originUserList.size() < userNum) {
            int ranUserId = RandomUtils.getRandom(userMinId, userMaxId);
            if (!exitList.contains(ranUserId)) {
                exitList.add(ranUserId);
                // 读取对应id的文件数据
                // User User = readFile(ranUserId);
                User user = new User();
                // 对应id数据满足条件，则生成感知时间等数据
                if (user != null) {
                    // 用户id范围是[1，10357]
                    user.setUserId(ranUserId);
                    // 感知时间范围是[5，10]
                    int senTime = RandomUtils.getRandom(userMinTime, userMaxTime);
                    user.setOriginSenTime(senTime);
                    // 竞标成本范围是[6，10]
                    user.setBid(RandomUtils.getRandom(userMinBid, userMaxBid));
                    originUserList.add(user);
                }
            }
        }
        // 按照userId升序
        Collections.sort(originUserList);
        // 排序后编号
        for (int j = 0; j < originUserList.size(); j++) {
            originUserList.get(j).setId(j);
        }
        // 随机生成每个用户关联的任务
        coverUserAOI(originUserList);
        // 生成异常用户
        createAbnormalUser(originUserList);
        return originUserList;
    }

    /*
     * 随机生成每个用户关联的任务
     */
    private static List<User> coverUserAOI(List<User> originUserList) {
        for (User originUser : originUserList) {
            // 每个用户关联的任务量,即论文中的E
            int userBidNum = RandomUtils.getRandom(0, ConstsUtils.AOI);
            // 在对单个任务进行匹配时，防止相同用户重复加入竞标后出现引用错误
            List<Integer> existList = new ArrayList<Integer>();
            while (originUser.getTaskIdList().size() < userBidNum) {
                // list编号从0开始
                int ranNum = RandomUtils.getRandom(0, ConstsUtils.TASKNUM - 1);
                if (!existList.contains(ranNum)) {
                    // 防止单回合生成重复数字
                    existList.add(ranNum);
                    // 关联任务id数组
                    originUser.getTaskIdList().add(ranNum);
                }
            }
            Collections.sort(originUser.getTaskIdList());
        }
        return originUserList;
    }

    /*
     * 生成异常用户
     */
    private static List<User> createAbnormalUser(List<User> originUserList) {
        // 防止生成重复数字
        List<Integer> existList = new ArrayList<Integer>();
        // 异常用户的数量
        int carelessNum = (int) (ConstsUtils.BREAKPOINT * originUserList.size());
        // 模拟异常用户
        while (existList.size() < carelessNum) {
            int ranNum = RandomUtils.getRandom(0, originUserList.size() - 1);
            if (!existList.contains(ranNum)) {
                existList.add(ranNum);
                originUserList.get(ranNum).setCareless(1);
                // TODO 添加一条异常坐标数据
            }
        }
        return originUserList;
    }

}