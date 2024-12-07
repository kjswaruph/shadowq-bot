package com.swaruph.model;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;
import net.dv8tion.jda.api.utils.ImageProxy;
import org.jetbrains.annotations.NotNull;

public class Rook {

    private final User user;
    private String riotId;
    private String gender;
    private String age;
    private String inGameRole;
    private String inGameRank;
    private List<String> agentPool;

    public Rook(User user) {
        this.user = user;
    }

    public Rook(User user, String riotId) {
        this.user = user;
        this.riotId = riotId;
    }

    public User getUser() {
        return user;
    }

    public String getDiscriminator() {
        return user.getDiscriminator();
    }

    public String getAsTag() {
        return user.getAsTag();
    }

    public String getAsMention() {
        return user.getAsMention();
    }

    public String getRiotId() {
        return riotId;
    }

    public void setRiotId(String riotId) {
        this.riotId = riotId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDiscordId() {
        return user.getId();
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getInGameRole() {
        return inGameRole;
    }

    public void setInGameRole(String inGameRole) {
        this.inGameRole = inGameRole;
    }

    public String getInGameRank() {
        return inGameRank;
    }

    public void setInGameRank(String inGameRank) {
        this.inGameRank = inGameRank;
    }

    public List<String> getAgentPool() {
        return agentPool;
    }

    public void setAgentPool(List<String> agentPool) {
        this.agentPool = agentPool;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Rook rook = (Rook) obj;
        return user.getId().equals(rook.user.getId());
    }

    @Override
    public int hashCode() {
        return user.getId().hashCode();
    }
}
