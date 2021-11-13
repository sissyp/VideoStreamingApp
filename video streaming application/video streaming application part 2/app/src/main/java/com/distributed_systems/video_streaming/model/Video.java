package com.distributed_systems.video_streaming.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Video {
    public String name;
    public ArrayList<String> topics;

    public Video(String name, ArrayList<String> topics) {
        this.name = name;
        this.topics = topics;
    }

    public String hashtagsForVideo(String video_title){
        return "#hashtag";
    }

}
