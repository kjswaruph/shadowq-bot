package com.swaruph.RookTownBot.manager;

import com.swaruph.RookTownBot.model.Queue;

import java.util.HashMap;
import java.util.Map;

public class QueueManager {
    private static QueueManager instance;
    private final Map<Integer, Queue> activeQueues;

    private QueueManager() {
        activeQueues = new HashMap<>();
    }

    public static synchronized QueueManager getInstance() {
        if (instance == null) {
            instance = new QueueManager();
        }
        return instance;
    }

    public void addQueue(Queue queue) {
        activeQueues.put(queue.getQueueId(), queue);
    }

    public Queue getQueue(int queueId) {
        return activeQueues.get(queueId);
    }

    public void removeQueue(int queueId) {
        activeQueues.remove(queueId);
    }

    public int getActiveQueueCount() {
        return activeQueues.size();
    }
}