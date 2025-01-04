package com.swaruph.RookTownBot.actions;

import java.io.IOException;

import com.swaruph.RookTownBot.config.ValorantConfig;
import net.socketconnection.jva.ValorantAPI;
import net.socketconnection.jva.enums.Rank;
import net.socketconnection.jva.models.player.PlayerCard;
import net.socketconnection.jva.player.ValorantPlayer;

public class ValorantAction {
    private ValorantConfig valorantConfig;
    private ValorantAPI valorantAPI;
    private ValorantPlayer valorantPlayer;
    private Rank rank;
    private int level;
    private int mmrChange;
    private int region;
    private PlayerCard playerCard;

    public ValorantAction() throws IOException {
        this.valorantConfig = new ValorantConfig();
        this.valorantAPI = new ValorantAPI(valorantConfig.getToken());
        this.valorantPlayer = new ValorantPlayer(valorantAPI);
    }

    public int getRegion() {
        return region;
    }

    public int getMmrChange() {
        return mmrChange;
    }

    public int getLevel() {
        return level;
    }

    public Rank getRank() {
        return rank;
    }

    public String getLargeBanner() {
        return playerCard.getLarge();
    }

    public String getSmallBanner() {
        return playerCard.getSmall();
    }


}
