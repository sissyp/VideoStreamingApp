package com.distributed_systems.video_streaming.util;

import android.content.Context;
import android.util.Pair;


import com.distributed_systems.video_streaming.network.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class InfoHandler {

    private static InfoHandler instance = null;
    private static UserStorage offlineStorage;


    private InfoHandler() {}

    public static InfoHandler getInstance(Context context) {
        if(instance == null) instance = new InfoHandler();
        offlineStorage = new UserStorage(context);
        return instance;
    }

    public JSONObject getInfo() throws Exception{
        JSONObject result = new JSONObject();
        Socket s = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try {
            s = new Socket(Constants.BROKER_IP, Constants.BROKER_PORT);
            output = new ObjectOutputStream(s.getOutputStream());
            input = new ObjectInputStream(s.getInputStream());
            String channel_name1 = (String) input.readObject();
            String channel_name2 = (String) input.readObject();
            //int broker_port1 = (int) input.readObject();
            //int broker_port2 = (int) input.readObject();
            ArrayList<String> topics1 = (ArrayList<String>) input.readObject();
            ArrayList<String> topics2 = (ArrayList<String>) input.readObject();
            String ip1 = (String) input.readObject();
            int port1 = (int)input.readObject();
            String ip2 = (String) input.readObject();
            int port2 = (int)input.readObject();

            //result.put("broker_port1",broker_port1);
            //result.put("broker_port2",broker_port2);
            result.put("topics1",topics1);
            result.put("topics2",topics2);
            result.put("ip1",ip1);
            result.put("port1",port1);
            result.put("ip2",ip2);
            result.put("port2",port2);
            result.put("channel_name1","ryan");
            result.put("channel_name2","mary");
        }
        catch (UnknownHostException unknownHost) {
            System.err.println("Preventing connection to unknown host...");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException c){
            c.printStackTrace();
        }
        finally {
            try {
                input.close();
                output.close();
                s.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}