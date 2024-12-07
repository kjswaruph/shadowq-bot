package com.swaruph.actions;

import java.util.List;

import com.swaruph.model.Queue;
import com.swaruph.model.Rook;

public class QueueAction {

    public Queue getRooksQueue(Queue currentQueue) {
        return currentQueue;
    }

    public void addRookInQueue(Queue currentQueue, Rook user) {
        if (!currentQueue.isFull()) {
            currentQueue.addRook(user);
        } else {
            throw new IllegalStateException("Queue is already full");
        }
    }

    public void removeRookInPlayer(Queue currentQueue, Rook user) {
        currentQueue.removeRook(user);
    }

    public boolean isRookInQueue(Queue currentQueue, Rook user) {

        return currentQueue.contains(user);
    }

    public String getRooksList(Queue currentQueue) {
        if (currentQueue.size() == 0) {
            return "No one is in the queue yet";
        }
        List<Rook> rooks = currentQueue.getRooks();
        StringBuilder playerList = new StringBuilder();
        for (Rook player : rooks) {
            playerList.append(player.getAsMention()).append("\n");
        }
        return playerList.toString();
    }
}
