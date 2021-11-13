import java.io.*;
import java.net.*;
import java.util.*;
import java.math.BigInteger;

public class PublisherHandler extends Thread implements Runnable{

    private static int port;
    private static String ip;
    private static String channel_name;
    private static String ip1, ip2;
    private static int port1, port2;
    private static String channel_name1,channel_name2;
    private static Socket connection;
    private static ArrayList <String> topics = new ArrayList<String>();
    private static ArrayList <String> topics1 = new ArrayList<String>();
    private static ArrayList <String> topics2 = new ArrayList<String>();

    public PublisherHandler(Socket connection){
        this.connection = connection;
    }

    public void run(){
        try{
            ObjectOutputStream output = new ObjectOutputStream(connection.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(connection.getInputStream());
            PublisherInformation info = (PublisherInformation) input.readObject();
            topics.removeAll(topics);
            topics.addAll(info.getTopics());
            ip = info.getPublisherIp();
            port = info.getPublisherPort();
            channel_name = info.getPublisherChannelname();
            
            if (info.getFirstPublisher() == true){ 
                ip1 = info.getPublisherIp();
                port1 = info.getPublisherPort();
                channel_name1 = info.getPublisherChannelname();
                System.out.println(channel_name1);
                for(String t :topics){
                    topics1.add(t);
                }
                System.out.println("Publisher 1 Ip: " + ip1);
                System.out.println("Publisher 1 Port: " + port1);
            }
            else{ 
                ip2 = info.getPublisherIp();
                port2 = info.getPublisherPort();
                channel_name2 = info.getPublisherChannelname();
                for(String t :topics){
                    topics2.add(t);
                }
                System.out.println("Publisher 2 Ip: " + ip2);
                System.out.println("Publisher 2 Port: " + port2);
            }
        }
        catch (IOException io){
            io.printStackTrace();
        }
        catch(ClassNotFoundException c){
            c.printStackTrace();
        }
    }

    public static ArrayList<String> loadTopics(){
        topics.removeAll(topics);
        for(String t: topics1){
            topics.add(t);
        }
        for(String t1 : topics2){
            topics.add(t1);
        }
        HashSet<String> topicsSet = new HashSet<String>();
        for(String t2: topics){
            topicsSet.add(t2);
        }
        topics.removeAll(topics);
        for(String t3: topicsSet){
            topics.add(t3);
        }
        return topics;
    }

    public static int getPort1(){
        return port1;
    }

    public static int getPort(){
        return port;
    }

    public static int getPort2(){
        return port2;
    }

    public static String getIp1(){
        return ip1;
    }

    public static String getIp(){
        return ip;
    }

    public static String getIp2(){
        return ip2;
    }

    public static String getChannelname1(){
        return channel_name1;
    }

    public static String getChannelname2(){
        return channel_name2;
    }

    public static ArrayList<String> getTopics1 (){
        return topics1;
    }

    public static ArrayList<String> getTopics2 (){
        return topics2;
    }

}