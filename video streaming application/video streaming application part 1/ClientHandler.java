import java.io.*;
import java.net.*;
import java.util.*;
import java.math.*;
import java.math.BigInteger;

public class ClientHandler extends Thread implements Runnable{

    private String topic;
    private String ip1, ip2;
    private int port1, port2;
    private String message = "";
    private boolean firstPublisher;
    private ArrayList<String> topics;
    private ArrayList<String> topics1;
    private ArrayList<String> topics2;
    private ObjectOutputStream outToConsumer;
    private ObjectInputStream inFromConsumer;

    public ClientHandler(Socket connection, ArrayList <String> topics, String ip1, int port1, String ip2, int port2, ArrayList<String> topics1, ArrayList<String> topics2, String topic) {
        this.topics = topics;
        this.ip1 = ip1;
        this.port1 = port1;
        this.ip2 = ip2;
        this.port2 = port2;
        this.topics1 = topics1;
        this.topics2 = topics2;
        this.topic = topic;
        try {
            outToConsumer = new ObjectOutputStream(connection.getOutputStream());
            inFromConsumer = new ObjectInputStream(connection.getInputStream());
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {

        try {
            if (!topics.contains(topic)) { // if topic doesn't exist
                System.out.println("Topic doesn't exist");
                message = "Topic \"" + topic + "\" doesn't exist";
                outToConsumer.writeObject(message); 
            } 
            else { // if topic exists
                System.out.println("Topic exists");
                if (topics1.contains(topic)) { // first publisher
                    firstPublisher = true;
                } 
                else { // second publisher
                    firstPublisher = false;
                }
                push();
            }
        }
        catch (IOException io) {
            io.printStackTrace();
        } 
        finally {
            try {
                inFromConsumer.close();
                outToConsumer.close();
            } 
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    /**  
     *  push finds whether the requested topic corresponds to the firts or the second publisher, informs
     *  the responsible publisher about the consumer's topic, reads all the available videos of the specific topic 
     *  from the publisher, writes the list of all those videos to the consumer, reads the name of the video that 
     *  the consumer wants to watch and informs the publisher responsible for the specific video. Finally, it 
     *  reads each chunk of the video and sends it to the consumer.
     */

    public void push(){
        Socket s = null;
        ObjectOutputStream outToPublisher = null;
        ObjectInputStream inFromPublisher = null;
        try {
            if(firstPublisher){ // requested topic corresponds to the first publisher
                System.out.println("First Publisher");
                System.out.println("Ip: " + ip1 + " Port: " + port1);
                s = new Socket(ip1, port1);
            }
            else{ // requested topic corresponds to the second publisher
                System.out.println("Second Publisher");
                System.out.println("Ip: " + ip2 + " Port: " + port2);
                s = new Socket(ip2, port2);
            }
            
            // for the connection with publisher, not with consumer
            outToPublisher = new ObjectOutputStream(s.getOutputStream());
            inFromPublisher = new ObjectInputStream(s.getInputStream());
            outToPublisher.writeObject(topic);
            try {
                ArrayList<String> videos = (ArrayList<String>) inFromPublisher.readObject();
                outToConsumer.writeObject(videos); // send available videos back to consumer
                String video = (String) inFromConsumer.readObject();
                outToPublisher.writeObject(video);
            } 
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
            try {
                String response = (String) inFromPublisher.readObject();
                System.out.println("Publisher: " + response);

                if(response.contains("Video exists")) { // video exists
                    outToConsumer.writeObject("Video exists"); // inform consumer that video exists, so that he will be prepared to receive the chunks
                    outToConsumer.flush();
                    int numberOfChunks = (int) inFromPublisher.readObject();
                    String chunks_string = String.valueOf(numberOfChunks);
                    outToConsumer.writeObject(chunks_string); // inform consumer about the number of chunks to wait for
                    System.out.println("Number of chunks: " + numberOfChunks);

                    ArrayList<byte[]> chunksList = new ArrayList<byte[]>();
                    for(int i = 0; i < numberOfChunks; i++){
                        System.out.println("Sending chunk number " + (i+1));
                        byte[] newChunk = (byte[]) inFromPublisher.readObject();
                        chunksList.add(newChunk);
                        outToConsumer.writeObject(newChunk);
                    }
                            
                }
                else{ // video doesn't exist
                    outToConsumer.writeObject(response);
                }
            } 
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            
        } 
        catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } 
        catch (IOException ioException) {
            ioException.printStackTrace();
        } 
        finally {
            try {
                inFromPublisher.close();
                outToPublisher.close();
                s.close();
            } 
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}