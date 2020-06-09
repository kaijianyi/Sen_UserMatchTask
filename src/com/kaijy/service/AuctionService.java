package com.kaijy.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.google.gson.reflect.TypeToken;
import com.kaijy.model.Task;
import com.kaijy.model.User;
import com.kaijy.utils.JsonUtils;
import com.kaijy.utils.NumberUtils;

public class AuctionService {

    /**
     * 开始入口
     * 
     * @param originBidUserList
     * @param originTaskList
     * @return
     * @throws CloneNotSupportedException
     */
    public static List<User> startAuction(String originUserListStr, String originTaskListStr)
            throws CloneNotSupportedException {
        // 返回值
        List<User> winnerList = new ArrayList<User>();

        System.out.println("\n$$$$$$$$$$$$$$$$$ winnerSelection开始 $$$$$$$$$$$$$$$$");
        winnerList = winnerSelection(originTaskListStr, originUserListStr);
        System.out.println("$$$$$$$$$$$$$$$$$ winnerSelection结束 $$$$$$$$$$$$$$$$");

        System.out.println("\n$$$$$$$$$$$$$$$$$ paymentDetermination开始 $$$$$$$$$$$$$$$$");
        winnerList = paymentDetermination(originTaskListStr, originUserListStr, winnerList);
        System.out.println("$$$$$$$$$$$$$$$$$ paymentDetermination结束 $$$$$$$$$$$$$$$$");

        return winnerList;
    }

    /**
     * 1-0、Winner Selection
     */
    private static List<User> winnerSelection(String originTaskListStr, String originUserListStr)
            throws CloneNotSupportedException {

        // 竞拍流程使用-用户
        List<User> bidUserList = JsonUtils.fastjsonToObj(originUserListStr, new TypeToken<List<User>>() {
        }.getType());
        // 竞拍流程使用-任务
        List<Task> bidTaskList = JsonUtils.fastjsonToObj(originTaskListStr, new TypeToken<List<Task>>() {
        }.getType());
        // 获胜者总集合，返回值
        List<User> winnerList = new ArrayList<User>();

        // 计算总的任务感知时间
        int taskTimeTotal = 0;
        for (Task task : bidTaskList) {
            taskTimeTotal += task.getRemainSenTime();
        }

        System.out.println("\n>>>>>>>>>>任务信息列表：");
        for (Task task : bidTaskList) {
            System.out.println("编号：" + task.getId() + ", id：" + task.getTaskId() + ", 原始时间：" + task.getOriginSenTime()
                    + ", 剩余时间：" + task.getRemainSenTime());
        }

        // 开始拍卖
        while (taskTimeTotal > 0) {
            // 获得winner
            User winner = (User) getWinner(bidUserList, bidTaskList).clone();

            System.out.println("\n>>>>>>>>>>获胜者信息列表：");
            System.out.println("编号：" + winner.getId() + ", id：" + winner.getUserId() + ", 报价：" + winner.getBid()
                    + ", 感知时间：" + winner.getOriginSenTime() + ", 最小感知时间和：" + winner.getMinTimeTotal() + ", 关联任务："
                    + Arrays.toString(winner.getTaskIdList().toArray()) + ", 单位成本：" + winner.getAveCost() + ", 收益："
                    + winner.getPay());
            System.out.println("############################################################################");

            // 添加到获胜者集合
            winnerList.add(winner);
            // 更新候选者集合数据
            Iterator<User> bidUserListIter = bidUserList.iterator();
            while (bidUserListIter.hasNext()) {
                User deleteUser = bidUserListIter.next();
                // 重置时间
                deleteUser.setMinTimeTotal(0);
                // 从候选集合删除获胜者
                if (deleteUser.getId() == winner.getId()) {
                    bidUserListIter.remove();
                }
            }
            // 更新任务剩余感知时间
            for (Task task : bidTaskList) {
                if (winner.getTaskIdList().contains(task.getId())) {
                    // 获取最小竞标时间
                    int minTime = NumberUtils.getMin(task.getRemainSenTime(), winner.getOriginSenTime());
                    // 更新剩余时间
                    task.setRemainSenTime(task.getRemainSenTime() - minTime);
                    // 设置时间分配方案
                    int key = task.getId();
                    int value = minTime;
                    winner.getAllocationMap().put(key, value);
                    // 更新总的任务感知时间
                    taskTimeTotal -= minTime;
                }
            }

            System.out.println("\n>>>>>>>>>>更新任务信息列表：");
            for (Task task : bidTaskList) {
                System.out.println("编号：" + task.getId() + ", id：" + task.getTaskId() + ", 原始时间："
                        + task.getOriginSenTime() + ", 剩余时间：" + task.getRemainSenTime());
            }
        }
        return winnerList;
    }

    /*
     * 1-1、获胜者算法
     */
    private static User getWinner(List<User> bidUserList, List<Task> bidTaskList) {

        System.out.println("\n>>>>>>>>>>用户竞标列表：");
        for (User bidUser : bidUserList) {
            // 计算每个用户关联任务的minTime
            for (Task task : bidTaskList) {
                if (bidUser.getTaskIdList().contains(task.getId())) {
                    int minTime = NumberUtils.getMin(task.getRemainSenTime(), bidUser.getOriginSenTime());
                    bidUser.setMinTimeTotal(bidUser.getMinTimeTotal() + minTime);
                }
            }
            // 计算每个用户的单位竞标成本
            String aveCostStr = NumberUtils.division(bidUser.getBid(), bidUser.getMinTimeTotal());
            bidUser.setAveCost(aveCostStr);

            System.out.println("编号：" + bidUser.getId() + ", id：" + bidUser.getUserId() + ", 报价：" + bidUser.getBid()
                    + ", 感知时间：" + bidUser.getOriginSenTime() + ", 最小感知时间和：" + bidUser.getMinTimeTotal() + ", 关联任务："
                    + Arrays.toString(bidUser.getTaskIdList().toArray()) + ", 单位成本：" + bidUser.getAveCost() + ", 收益："
                    + bidUser.getPay());
            System.out.println("############################################################################");
        }

        // 获得winner
        User winner = getMinAveCost(bidUserList);
        return winner;
    }

    /*
     * 1-2、选择最小的竞标成本的获胜
     */
    private static User getMinAveCost(List<User> bidUserList) {
        // 假设第一个人竞拍成本最小
        User winner = bidUserList.get(0);
        for (int i = 1; i < bidUserList.size(); i++) {
            if (Float.valueOf(winner.getAveCost()) > Float.valueOf(bidUserList.get(i).getAveCost())) {
                winner = bidUserList.get(i);
            }
        }
        return winner;
    }

    /**
     * 2-0、Payment Determination
     */
    private static List<User> paymentDetermination(String originTaskListStr, String originUserListStr,
            List<User> winnerList) throws CloneNotSupportedException {

        for (User winner : winnerList) {

            System.out.println("\n>>>>>>>>>>当前获胜者信息：");
            System.out.println("编号：" + winner.getId() + ", id：" + winner.getUserId() + ", 报价：" + winner.getBid()
                    + ", 感知时间：" + winner.getOriginSenTime() + ", 最小感知时间和：" + winner.getMinTimeTotal() + ", 关联任务："
                    + Arrays.toString(winner.getTaskIdList().toArray()) + ", 单位成本：" + winner.getAveCost() + ", 收益："
                    + winner.getPay());
            System.out.println("############################################################################");

            // 只删除当前winner，下次迭代时恢复上次被删除的winner
            List<User> deleteUserList = JsonUtils.fastjsonToObj(originUserListStr, new TypeToken<List<User>>() {
            }.getType());
            // 删除获胜者
            Iterator<User> deleteListItor = deleteUserList.iterator();
            while (deleteListItor.hasNext()) {
                User deleteUser = deleteListItor.next();
                if (deleteUser.getId() == winner.getId()) {
                    deleteListItor.remove();
                }
            }
            // 深拷贝用户
            String payUserListStr = JsonUtils.objToFastjson(deleteUserList);
            // 开始
            List<User> nextWinnerList = winnerSelection(originTaskListStr, payUserListStr);
            // 确定支付价格
            for (User nextWinner : nextWinnerList) {
                System.out.println("\n>>>>>>编号：" + nextWinner.getId() + ", 次级获胜者ID：" + nextWinner.getUserId()
                        + ", 次级获胜者时间和：" + nextWinner.getMinTimeTotal() + ", 次级获胜者报价：" + nextWinner.getBid());
                winner.setPay(getPay(winner, nextWinner));
                System.out.println(">>>>>>编号：" + winner.getId() + ", 获胜者ID：" + winner.getUserId() + ", 获胜者时间和："
                        + winner.getMinTimeTotal() + ", 获胜者报价：" + winner.getBid() + ", 当前支付价格:" + winner.getPay());
            }
        }
        return winnerList;
    }

    /*
     * 2-1、支付函数
     */
    private static String getPay(User winner, User nextWinner) {
        String winnerPay = winner.getPay();
        // 保留2位小数
        String nextPay = getNextPay(winner.getMinTimeTotal(), nextWinner.getMinTimeTotal(), nextWinner.getBid());
        winnerPay = NumberUtils.getStrMax(winnerPay, nextPay);
        return winnerPay;
    }

    /*
     * 2-2、计算支付价格
     */
    private static String getNextPay(int winnerSenTime, int nextSenTime, int nextBid) {
        float result = (float) winnerSenTime / nextSenTime * nextBid;
        String nextPay = String.format("%.2f", result);
        return nextPay;
    }

}
