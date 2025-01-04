package com.swaruph.RookTownBot.actions;

import java.io.IOException;

import com.swaruph.RookTownBot.config.ValorantConfig;
import net.socketconnection.jva.ValorantAPI;
import net.socketconnection.jva.player.ValorantPlayer;

public class GetStartedAction {
    ValorantConfig valorantConfig = new ValorantConfig();
    ValorantAPI valorantAPI ;
    public GetStartedAction() {
        try {
            valorantAPI = new ValorantAPI(valorantConfig.getToken());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public boolean getStarted(String username, String tag, String age, String description) {

        ValorantPlayer valorantPlayer;
        boolean isSuccess;
        try {
            valorantAPI = new ValorantAPI(valorantConfig.getToken());
            valorantPlayer = new ValorantPlayer(valorantAPI).fetchData(username, tag);
            isSuccess = true;
        }catch (IOException e){
            isSuccess = false;
            e.printStackTrace();
        }
        return isSuccess;
    }
    public ValorantPlayer getPlayer(String username, String tag) throws IOException {
        ValorantPlayer valorantPlayer = new ValorantPlayer(valorantAPI).fetchData(username, tag);
        return valorantPlayer;
    }
}
