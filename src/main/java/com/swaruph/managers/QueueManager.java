package com.swaruph.managers;

import com.swaruph.actions.QueueAction;
import com.swaruph.model.Queue;
import com.swaruph.model.Rook;

import java.util.HashMap;
import java.util.Map;


public class QueueManager {
    private Map<Integer, Queue> queues;
    private QueueAction queueAction;

    private static QueueManager instance;

    public QueueManager(QueueAction queueAction) {
        this.queues = new HashMap<>();
        this.queueAction = queueAction;
    }

    public static QueueManager getInstance(QueueAction queueAction){
        if(instance == null){
            instance = new QueueManager(queueAction);
        }
        return instance;
    }

    public synchronized void addQueuestoMap(Queue queue) {
        if (isCurrentQueueFull() && queues.containsKey(queue.getQueueId())) {
            throw new IllegalStateException("Current queue is full");
        }
        int queueId = queue.getQueueId();
        queues.put(queueId, queue);
    }

    public synchronized void addRookInQueue(int queueId, Rook user) {
        Queue currentQueue = getQueue(queueId);
        if (currentQueue == null) {
            throw new IllegalStateException("Queue does not exist");
        }else {
            queueAction.addRookInQueue(currentQueue, user);
        }
    }

    public synchronized Queue getQueue(int queueId) {
        return queues.get(queueId);
    }

    public synchronized void removeQueue(int queueId, Rook user) {
        Queue currentQueue = getQueue(queueId);
        if (currentQueue == null) {
            throw new IllegalStateException("Queue does not exist");
        }else {
            queueAction.removeRookInPlayer(currentQueue, user);
        }
        queues.remove(queueId);
    }

    public synchronized Map<Integer, Queue> getAllQueues() {
        return queues;
    }

    public synchronized boolean isCurrentQueueFull() {
        return queues.isEmpty() || queues.values().stream().allMatch(queue -> queue.size() >= 10);
    }

    public synchronized Queue getCurrentQueue() {
        return queues.values().stream().filter(queue -> queue.size() < 10).findFirst().orElse(null);
    }

}
