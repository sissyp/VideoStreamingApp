import java.io.*;
import java.net.*;
import java.util.*;
import java.math.BigInteger;

public class Consumer {

    int port;
    String ip;
    String channelName;
    private static int brokerPort;
    private static int portC = 404;
    private static ArrayList<String> topicsRegistered = new ArrayList<String>();

    public Consumer(String ip, int port, String channelName){
        this.ip = ip;
        this.port = port;  
        this.channelName = channelName;  
    }
    
    public void run(){
        try{
            waitForPublisher();
            if(port == 1234){
                connectwithBroker("127.0.0.1",portC);
                consumerRequest("127.0.0.1",brokerPort);
            }
        }
         catch(IOException io){
            io.printStackTrace();
        }
    }

    /**  
     *  waitForPublisher makes the consumers' threads to wait until broker has learned
     *  all the information from each publisher. In order to make sure that both publishers 
     *  are connected to the brokers we read the contents of publishers.txt file which 
     *  contains the number of publishers' connections.
     */

    public void waitForPublisher(){
        try{
            File obj = new File("publishers.txt");
            Scanner file = new Scanner(obj);
            int connections2 = Integer.parseInt(file.nextLine());
            while(connections2<2){
                try {
                    Thread.sleep(15000);
                    File obj2 = new File("publishers.txt");
                    Scanner file2 = new Scanner(obj2);
                    connections2 = Integer.parseInt(file2.nextLine());
                } 
                catch(FileNotFoundException f){
                    System.out.println("file not found");
                }
                catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); 
                    System.out.println("Thread interrupted"); 
                }
            }
        }
        catch(FileNotFoundException f){
            System.out.println("file not found");
        }
    }

    /**  
     *  connectwithBroker is the client side where the consumers gets from the main broker
     *  the list with all brokers and all available topics. It asks the consumer to choose 
     *  between the abailable topics and informs the main broker about the consumer's request.
     *  Finally, it stores in brokerPort the port of the broker who is responsible for the 
     *  specific topic.
     *  @param ipC is the ip of the main broker.
     *  @param portC is the port of the main broker.
     */

    public void connectwithBroker(String ipC, int portC){
        Socket s = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        ByteArrayOutputStream in = null;
        try{
            s = new Socket(ipC,portC);
            output = new ObjectOutputStream(s.getOutputStream());
            input = new ObjectInputStream(s.getInputStream());
            
            HashMap<Integer,BigInteger> brokerList = (HashMap<Integer,BigInteger>) input.readObject();
            ArrayList <String> topics1 = (ArrayList <String>)input.readObject();
            ArrayList <String> topics2 = (ArrayList <String>)input.readObject();
            
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
    }

    /**  
     *  consumerRequest lists all the available videos of the requested topic and asks the consumer 
     *  to choose which video he wants to watch. It then returns the chunks of the specific video 
     *  and merges them in one video stored in consumer_video.mp4 file.
     *  @param ip is the ip of the broker responsible for the requested topic.
     *  @param port is the port of the broker responsible for the requested topic.
     */

    public static void consumerRequest(String ip, int port) throws IOException{
        Socket s = null;
        ObjectOutputStream output = null;
        ObjectInputStream input = null;
        ByteArrayOutputStream in = null;
        String numberOfChunks = "";
        int number_of_chunks = 0;
        File ofile = new File("consumer_video.mp4");
        FileOutputStream fos;
        ArrayList<byte[]> chunksList = new ArrayList<byte[]>();
        try{
            s = new Socket(ip,port);
            output = new ObjectOutputStream(s.getOutputStream());
            input = new ObjectInputStream(s.getInputStream());
        
            ArrayList<String> videos = (ArrayList<String>)input.readObject();
            for(String v: videos){
                System.out.println(v);
            }
            System.out.println("Choose a specific video");
            Scanner sc = new Scanner(System.in);
            String video = sc.nextLine();
            output.writeObject(video);
            
            String c = (String)input.readObject();
            numberOfChunks = (String)input.readObject();
            System.out.println("Number of chunks: " + numberOfChunks);
            number_of_chunks = Integer.parseInt(numberOfChunks);
            for(int i=0; i< number_of_chunks ; i++){
                byte[] newChunk = (byte[]) input.readObject();
                chunksList.add(newChunk);
            }
            try{
                fos = new FileOutputStream(ofile); 
                for (byte[] b : chunksList){
                    fos.write(b);
                    fos.flush();
                }
                fos.close();
                fos = null;
            }
            catch (IOException io) {
                io.printStackTrace();
            }
                    
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
    }      
}