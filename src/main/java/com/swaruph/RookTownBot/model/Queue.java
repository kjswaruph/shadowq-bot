package com.swaruph.RookTownBot.model;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.User;

public class Queue {

    private int queueId;
    private String queueName;
    private String queueType;
    private boolean queueStatus;
    private User queueAdmin;
    private List<Rook> queueMembers;
    private final int capacity = 10 ;

    public Queue(int queueId){
        this.queueMembers = new ArrayList<>();
        this.queueId = queueId;
    }

    public int getQueueId() {
        return queueId;
    }

    public void setQueueId(int queueId) {
        this.queueId = queueId;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getQueueType() {
        return queueType;
    }

    public void setQueueType(String queueType) {
        this.queueType = queueType;
    }

    public boolean getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(boolean queueStatus) {
        this.queueStatus = queueStatus;
    }

    public User getQueueAdmin() {
        return this.queueAdmin;
    }

    public void setQueueAdmin(User queueAdmin) {
        this.queueAdmin = queueAdmin;
    }

    public List<Rook> getRooks() {
        return this.queueMembers;
    }

    public void addRook(Rook rook) {
        queueMembers.add(rook);
    }

    public void removeRook(Rook rook) {
        queueMembers.remove(rook);
    }

    public int size() {
        return queueMembers.size();
    }

    public boolean contains(Rook rook) {
        return queueMembers.contains(rook);
    }

    public boolean isFull() {
        return queueMembers.size() >= capacity;
    }
}
