package com.distributed_systems.video_streaming.model;

import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PublishersInfo {
    public String channelName;
    public Pair<String, Integer> publisherConnectionInfo;
    //public int brokerConnectionInfo;

    public PublishersInfo(String channelName, Pair<String, Integer> publisherConnectionInfo){ //int brokerConnectionInfo) {
        this.channelName = channelName;
        this.publisherConnectionInfo = publisherConnectionInfo;
        //this.brokerConnectionInfo = brokerConnectionInfo;
    }

    public static ArrayList<PublishersInfo> parseIt(JSONObject json){
        try{
            String channel_name1 = json.getString("channel_name1");
            String channel_name2 = json.getString("channel_name2");
            String publisher1_ip = json.getString("ip1");
            Integer publisher1_port = json.getInt("port1");
            String publisher2_ip = json.getString("ip2");
            Integer publisher2_port = json.getInt("port2");
            //int broker_port1 = json.getInt("broker_port1");
            //int broker_port2 = json.getInt("broker_port2");

            PublishersInfo info1 = new PublishersInfo(channel_name1,new Pair<>(publisher1_ip, publisher1_port));
            PublishersInfo info2 = new PublishersInfo(channel_name2,new Pair<>(publisher2_ip,publisher2_port));

            ArrayList<PublishersInfo> info = new ArrayList<>();
            info.add(info1);
            info.add(info2);

            return info;
        }
        catch (JSONException e){
            return null;
        }
    }



}