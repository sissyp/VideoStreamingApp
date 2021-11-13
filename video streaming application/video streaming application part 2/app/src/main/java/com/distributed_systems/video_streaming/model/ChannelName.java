package com.distributed_systems.video_streaming.model;

import org.json.JSONException;
import org.json.JSONObject;

public class ChannelName {
    public String name;

    public ChannelName(String name) {
        this.name = name;
    }

    public static ChannelName parseIt(JSONObject jsonObject){
        try {
            String name = jsonObject.getString("channel_name");

            return new ChannelName(name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
