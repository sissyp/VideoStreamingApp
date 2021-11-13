import java.io.*;
import java.util.*;

public class PublisherInformation implements Serializable{

    private int publisher_port;
    private String publisher_ip;
    private String publisher_channelName;
    private boolean firstPublisher;
    private ArrayList<String> topics;

    public PublisherInformation(ArrayList<String> topics,String publisher_ip,int publisher_port,String publisher_channelname,boolean firstPublisher){
        this.topics = topics;
        this.publisher_ip = publisher_ip;
        this.publisher_port = publisher_port;
        this.publisher_channelName = publisher_channelName;
        this.firstPublisher = firstPublisher;
    }
    
    public ArrayList<String> getTopics(){
        return topics;
    }

    public String getPublisherIp(){
        return publisher_ip;
    }

    public String getPublisherChannelname(){
        return publisher_channelName;
    }

    public int getPublisherPort(){
        return publisher_port;
    }

    public void setTopics(ArrayList<String> topics){
        this.topics = topics;
    }

    public boolean getFirstPublisher(){
        return firstPublisher;
    }

    public void setFirstPublisher(boolean firstPublisher){
        this.firstPublisher = firstPublisher;
    }
}