package com.swaruph.RookTownBot.model;

import net.dv8tion.jda.api.entities.User;

import static com.swaruph.RookTownBot.RookTownBot.rookDB;

public class Rook {

    private String puuid;
    private String discordId;
    private User user;
    private String name;

    public Rook(String puuid){
        this.puuid = puuid;
    }

    public Rook(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public Rook(User user) {
        this.user = user;
    }

    public Rook(String puuid, String playerName) {
        this.puuid = puuid;
        this.discordId = rookDB.getDiscordIdByPuuid(puuid);
        this.name = playerName;
    }

    public String getPuuid() {
        return puuid;
    }

    public String getDiscordId() {
        return discordId;
    }


    public String getAsMention() {
        return user.getAsMention();
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

    public String getPlayerName() {
        return name;
    }
}
