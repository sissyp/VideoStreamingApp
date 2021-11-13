import java.io.*;
import java.net.*;
import java.util.*;
import java.math.BigInteger;
import java.util.stream.IntStream;

public class Publisher implements Serializable{

    private static int portP = 200;
    private static int connections;
    private static InetAddress addr;
    private static Socket sock = null;
    private static int publisher_port;
    private static String publisher_channelName;
    private static String publisher_ip;
    private static boolean firstPublisher;  
    private static ServerSocket server_socket;
    private static ArrayList <String> topics = new ArrayList<String>();
    private static ArrayList <String> topics1 = new ArrayList<String>();
    private static ArrayList <String> topics2 = new ArrayList<String>();
    private static HashMap <String, ArrayList<String>> data = new HashMap<String,ArrayList<String>>();
    private static HashMap <String, ArrayList<String>> data1 = new HashMap<String,ArrayList<String>>();
    private static HashMap <String, ArrayList<String>> data2 = new HashMap<String,ArrayList<String>>();
   
    public Publisher(String ip, int port, String channelName){
        publisher_ip = ip;
        publisher_port = port;    
        publisher_channelName = channelName;
    }
    
    public static void main(String[]args){
        try{
            addr = InetAddress.getByName("192.168.1.11");
            String myIp = addr.toString().replace("/","");
            Publisher publisherAppnode = new Publisher(args[0],Integer.parseInt(args[1]),args[2]);
            System.out.println(publisher_channelName);
            init();
            connectwithBroker(myIp,portP);
            acceptRequests();
        }
        catch(IOException io){
            io.printStackTrace();
        }
	}

    /**  
     *  init initializes data and topics for each publisher. Data is a hashmap that contains all videos
     *  of a specific topic. Topics is the list of all available hashtags and channelnames.
     */

    public static void init(){
        if(publisher_port == 1234){
            data1 = loadData("dataset/publisher1");
            for (String t : data1.keySet()){
                String key = t.toString();
                topics1.add(key);
            }
            topics.add(publisher_channelName);
            topics1.add(publisher_channelName);
            ArrayList<String> publisher1_videos = new ArrayList<String>();
            publisher1_videos.add("video_ex1.mp4");
            publisher1_videos.add("video_ex2.mp4");
            data1.put(publisher_channelName,publisher1_videos);
            data.put(publisher_channelName,publisher1_videos);
        }
        else{
            data2 = loadData("dataset/publisher2");
            for (String t : data2.keySet()){
                String key = t.toString();
                topics2.add(key);
            }
            topics.add(publisher_channelName);
            topics2.add(publisher_channelName);
            ArrayList<String> publisher2_videos = new ArrayList<String>();
            publisher2_videos.add("video_ex3.mp4");
            publisher2_videos.add("video_ex4.mp4");
            data2.put(publisher_channelName,publisher2_videos);
            data.put(publisher_channelName,publisher2_videos);
        }
    }

    /**  
     *  connectwithPublisher is the client side where each publisher shares its information with
     *  the main broker. Publishers.txt is a file that contains the number of publishers connected to
     *  the main broker's server. Each publisher increases the number of connections each time connectWithPublisher
     *  is called and replaces the previous number of connections included in the file with the new number 
     *  that he calculated.
     *  @param ip is the ip of the publisher.
     *  @param port is the port of the publisher.
     */
        
	public static void connectwithBroker(String ip, int port) throws IOException{ 
        Socket s = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        try{
            s = new Socket(ip,port);
            output = new ObjectOutputStream(s.getOutputStream());
            input = new ObjectInputStream(s.getInputStream());
            if (publisher_port == 1234){
                firstPublisher = true;
                topics = topics1;
            }
            else{
                firstPublisher = false;
                topics = topics2;
            }
        

            String myIp = addr.toString().replace("/","");
            System.out.println(publisher_channelName);
            PublisherInformation publisher_info = new PublisherInformation(topics,myIp,publisher_port,publisher_channelName,firstPublisher);
            output.writeObject(publisher_info);
        }

        catch (UnknownHostException unknownHost) {
            System.err.println("Preventing connection to unknown host...");
        }
        catch (IOException e) {
            e.printStackTrace();
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
    }

    /**  
     *  acceptRequests creates a server for each publisher in order to accept requests from the brokers.
     *  It creates a brokerActions object with the videos related to every hashtag or channelname
     *  and starts a thread for each broker's request.
     */

    public static void acceptRequests() throws IOException{
        try{
            String myIp = addr.toString().replace("/","");
            System.out.println("Publisher is open. Ip: " + myIp + ", Port: " + publisher_port);
            server_socket = new ServerSocket(publisher_port, 50, addr);
            
            while(true){
                System.out.println("Publisher: Waiting for broker connection");
                sock = server_socket.accept();
                System.out.println("Publisher: Connected to broker");
                if(publisher_port == 1234){
                    data = data1;
                }
                else{
                    data = data2;
                }
                BrokerActions add = new BrokerActions(sock, data, publisher_port);
                Thread t = new Thread(add);
                t.start();

                try {
                    t.join();
                }

                catch(Exception e) {
                    System.out.println("Interrupted");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {     
            try {
                server_socket.close();
            } 
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**  
     *  addHashtag adds a hashtag to a video and updates topics and data for each publisher.
     *  @param hashtag is the hashtag we want to add.
     */

    public static void addHashtag(String hashtag){
        topics.add(hashtag);
        if (publisher_port == 1234){
            topics1.add(hashtag);
            data1 = loadData("dataset/publisher1");
        }
        else{
            topics2.add(hashtag);
            data2 = loadData("dataset/publisher2");
        }
    }

    /**  
     *  removeHashtag removes a hashtag from a video and updates topics and data for each publisher.
     *  @param hashtag is the hashtag we want to remove.
     */

    public static void removeHashtag(String hashtag){
        topics.remove(hashtag);
        if (publisher_port == 1234){
            topics1.remove(hashtag);
            data1.remove(hashtag);
        }
        else{
            topics2.remove(hashtag);
            data2.remove(hashtag);
        }
    }

    /**  
     *  loadData initializes data and topics. It stores all the contents of a folder in an array of files 
     *  and checks whether the extension of a file is mp4 or txt. In the first case, in stores the names of
     *  all videos and in the second case it reads all hastags and channelnames and stores them in arraylist 
     *  topics. Finally, it matches each hashtag or channelname with a list of related video.
     *  @param path is the path of the folder.
     *  @return a hashmap with each topic as a key and an arraylist of videos as a value.
     */

    public static HashMap<String, ArrayList<String>> loadData(String path){
        
        File directory = new File(path);
        File[] listOfFiles = directory.listFiles();
        int numberOfVideos = listOfFiles.length / 2;
        
        String[][] video_data = new String[numberOfVideos][52];
        HashSet<String> topicNames = new HashSet<String>();
        int i = 0;
        int j = 1;
        int next = 0;
        
        for (File file : listOfFiles) {
            String extension = file.getName().substring(file.getName().indexOf(".") + 1);

            if (extension.equalsIgnoreCase("mp4")){
                String videoName = file.getName();
                video_data[i][0] = videoName;
                j++;
                next++;
            }
            else if(extension.equalsIgnoreCase("txt")){
                try(BufferedReader br = new BufferedReader(new FileReader(file))) {                   
                    for(String line; (line = br.readLine()) != null;) {  
                        topicNames.add(line);
                        video_data[i][j] = line;
                        j++;
                    }
                    next++;                             
                }
                catch(IOException io){
                    io.printStackTrace();
                }
            }
            if (next % 2 == 0){
                i++;
            }
            j = 1;
        }

        ArrayList<String> topicNamesList = new ArrayList<>(topicNames);
        int numberOfTopics =  topicNamesList.size();
        String topic;
        ArrayList<String> listOfVideos =  new ArrayList<String>();
        int col;

        for (int t = 0; t < numberOfTopics; t++){
            topic = topicNamesList.get(t);
            for (int row = 0; row < numberOfVideos; row++){
                
                for (col = 1; col < 52; col++){
                    if (video_data[row][col] == null)
                        break;
                    if (video_data[row][col].equals(topic)){
                        listOfVideos.add(video_data[row][0]);
                        break;
                    }
                }
            }
            data.put(topic, listOfVideos);
            listOfVideos = new ArrayList<String>();
        }
        topics = topicNamesList;
        return data;
    }       
}