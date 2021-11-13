import java.io.*;
import java.net.*;
import java.util.*;

public class BrokerActions extends Thread implements Runnable{

    int publisher_port;
    Socket connection ;
    String message = "";
    ObjectInputStream input;
    ObjectOutputStream output;
    HashMap <String, ArrayList<String>> data;
    ArrayList<String> videos = new ArrayList<String>();
    

    public BrokerActions(Socket connection, HashMap<String, ArrayList<String>> data, int publisher_port) {
        this.data = data;
        this.publisher_port = publisher_port;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            input = new ObjectInputStream(connection.getInputStream());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            try {
                String topic = (String)input.readObject();
                
                System.out.println("Broker wants the available videos for topic " + topic);
                System.out.print("Searching . . . ");
                videos = data.get(topic);
                System.out.println("Ok!");
                output.writeObject(videos); //publisher informs the broker about all available videos

                String video = (String)input.readObject();
                System.out.println("Broker wants video: " + video);
                System.out.println("Searching . . . ");
                message = searchVideo(video, topic, data);
                output.writeObject(message); // publisher informs broker whether the requested video exists
                output.flush();

                if(message.contains("Video exists")) { 

                    byte[] bytes = null;
                    BufferedInputStream fileInputStream = null;
                    String exactVideo = message.substring(13); //exact title of the video 
                    System.out.println(exactVideo);
                    try {
                        String filepath = "";
                        if(publisher_port == 1234){
                            filepath = "dataset/publisher1/";
                        }
                        else{
                            filepath = "dataset/publisher2/";
                        }
                        //publisher returns the chunks of the requested video
                        File file = new File(filepath + exactVideo );
                        System.out.println(file);
                        fileInputStream = new BufferedInputStream(new FileInputStream(file));
                        bytes = new byte[(int) file.length()];
                        fileInputStream.read(bytes);
                        System.out.println(bytes.length);
                        ArrayList<byte[]> chunksList = generateChunks(bytes); 
                        int numberOfChunks = chunksList.size(); 
                        output.writeObject(numberOfChunks);
                        output.flush();

                        for(int i = 0; i < numberOfChunks; i++){ 
                        System.out.println("Sending chunk number " + (i + 1));
                        output.writeObject(chunksList.get(i));
                        }

                    }
                    catch(FileNotFoundException f){
                        System.out.println("File not found");
                    }
                }
                else{
                    System.out.println("video does not exist");
                }
                
            } 
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } 
        catch (IOException e) {
            e.printStackTrace();
        } 
        finally {
            try {
                input.close();
                output.close();
            } 
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**  
     *  generateChunks splits a video into smaller chunks.
     *  @param bts is the bytes of the video file.
     *  @return list of all the chunks of the video.
     */

    public static ArrayList<byte[]> generateChunks(byte[] bts) {
        int blockSize = 512 * 1024;
        ArrayList<byte[]> list = new ArrayList<>();
        System.out.println(bts.length % blockSize);
        int blockCount = (bts.length + blockSize - 1) / blockSize;
        byte[] range = null;

        for (int i = 1; i < blockCount; i++) {
            int idx = (i - 1) * blockSize;
            range = Arrays.copyOfRange(bts, idx, idx + blockSize);
            list.add(range);
        }

        int end = -1;
        if (bts.length % blockSize == 0) {
            end = bts.length;
        } 
        else {
            end = bts.length % blockSize + blockSize * (blockCount - 1);
        }

        range = Arrays.copyOfRange(bts, (blockCount - 1) * blockSize, end);
        list.add(range);
        return list;
    }

    /**  
     *  searchVideo iterates through topics in order to find whether the requested topic
     *  exists and if it does it searches data hashmap in order to find if the video corresponds
     *  to the specific topic. If none of the above happens it returns a suitable message.
     *  @param video is the video we are searching for.
     *  @param topic is the topic we are searching for.
     *  @param data is hashmap that contains all hashtags and the videos related to them.
     */

    public static String searchVideo(String video, String topic, HashMap <String, ArrayList<String>> data) {
        String message;
        if (!data.containsKey(topic)) {
            // topic doesn't exist
            System.out.println("Topic doesn't exist");
            message = "Topic \"" + topic + "\" doesn't exist";
        }
        else {
            // topic exists
            boolean found = false;
            ArrayList<String> topicVideos = data.get(topic);
            for (int i = 0; i < topicVideos.size(); i++) {
                if (topicVideos.get(i).equalsIgnoreCase(video)) {
                    video = topicVideos.get(i); 
                    found = true;
                }
            }
            if (found) {
                message = "Video exists " + video;
            }
            else {
                message = "Video \"" + video + "\" for topic \"" + topic + "\" doesn't exist";
            }
        }
        return message;
    }
}