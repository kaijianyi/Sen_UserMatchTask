package com.kaijy.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class User implements Comparable<User>, Cloneable {

    // API格式：纬度lat,39，经度lon,116
    // 数据集格式：经度lon,116，纬度lat,39

    // id
    private int id;
    // 用户id
    private int userId;
    // 用户出价
    private int bid;
    // 原始感知时间
    private int originSenTime;
    // 关联任务最小感知时间和
    private int minTimeTotal;
    // 关联的任务id
    private List<Integer> taskIdList;
    // 时间分配方案
    private HashMap<Integer, Integer> allocationMap;

    // 竞拍单位成本
    private String aveCost;
    // 竞拍获胜后的收益
    private String pay = "0";

    // 异常用户标记，0-正常，1异常
    private int careless;

    public User() {
        this.taskIdList = new ArrayList<Integer>();
        this.allocationMap = new HashMap<Integer, Integer>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getOriginSenTime() {
        return originSenTime;
    }

    public void setOriginSenTime(int originSenTime) {
        this.originSenTime = originSenTime;
    }

    public int getMinTimeTotal() {
        return minTimeTotal;
    }

    public void setMinTimeTotal(int minTimeTotal) {
        this.minTimeTotal = minTimeTotal;
    }

    public List<Integer> getTaskIdList() {
        return taskIdList;
    }

    public void setTaskIdList(List<Integer> taskIdList) {
        this.taskIdList = taskIdList;
    }

    public HashMap<Integer, Integer> getAllocationMap() {
        return allocationMap;
    }

    public void setAllocationMap(HashMap<Integer, Integer> allocationMap) {
        this.allocationMap = allocationMap;
    }

    public String getAveCost() {
        return aveCost;
    }

    public void setAveCost(String aveCost) {
        this.aveCost = aveCost;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public int getCareless() {
        return careless;
    }

    public void setCareless(int careless) {
        this.careless = careless;
    }

    @Override
    public int compareTo(User user) {
        return this.userId - user.getUserId();
    }

    @Override
    public User clone() throws CloneNotSupportedException {
        return (User) super.clone();
    }

}
