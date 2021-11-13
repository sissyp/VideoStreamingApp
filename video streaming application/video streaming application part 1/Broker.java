import java.io.*;
import java.net.*;
import java.util.*;
import java.math.BigInteger;

public class Broker extends Thread implements Serializable{

    private static String topic;
    private static int portP = 200;
    private static int portC = 404;
    private static String ip1, ip2;
    private static int broker_port;
    private static InetAddress addr;
    private static String broker_ip;
    private static int port1, port2;
    private static int portForConsumer;
    private static Socket connection = null;
    private static ServerSocket server_socket;
    private static String [] ip = new String[3];
    private static String [] port = new String[3];
    private static ServerSocket server_socket_pull;
    private static ObjectOutputStream outToConsumer;
    private static ObjectInputStream inFromConsumer;
    private static String channel_name1,channel_name2;
    private static ArrayList <String> topics = new ArrayList<String>();
    private static ArrayList <String> topics1 = new ArrayList<String>();
    private static ArrayList <String> topics2 = new ArrayList<String>();
    private static ArrayList <ClientHandler> clients = new ArrayList <> ();
    public static HashMap<Integer, BigInteger> brokerList = new HashMap<Integer, BigInteger>();

    public Broker(String ip, int port){
        broker_ip = ip;
        broker_port = port;
    }

    public static void main(String[] args) throws IOException {
        brokerList = calculateKeys();
        addr = InetAddress.getByName("192.168.1.11");
        Broker broker = new Broker(args[0],Integer.parseInt(args[1]));
        if(broker_port == portP){
            broker.connectwithPublisher();
            broker.connectwithPublisher();
            broker.connectWithClient();
        }
        broker.pull(broker_port);
	}

    /**  
     *  connectWithPublisher creates the main broker's server, starts a thread for each publisher
     *  and stores the publishers' topics, ip and port. 
     */

    public void connectwithPublisher(){
        try{
            server_socket = new ServerSocket(portP, 50, addr); 
                System.out.println("Broker: Waiting for publisher");
                connection = server_socket.accept(); 
                System.out.println("Broker: Connected to publisher");
                PublisherHandler p = new PublisherHandler(connection);
                Thread publisherThread = new Thread(p);
                publisherThread.start();
                try {
                    publisherThread.join();
                }
                catch(Exception e) {
                    System.out.println("Interrupted");
                }
            topics1 = PublisherHandler.getTopics1();
            ip1 = PublisherHandler.getIp1();
            port1 = PublisherHandler.getPort1();
            topics2 = PublisherHandler.getTopics2();
            ip2 = PublisherHandler.getIp2();
            port2 = PublisherHandler.getPort2();
            topics = PublisherHandler.loadTopics();
            channel_name1 = PublisherHandler.getChannelname1();
            channel_name2 = PublisherHandler.getChannelname2();
            System.out.println(channel_name1);
        } 
        catch(IOException e){
            e.printStackTrace();
        } 
        finally {
            try {
                server_socket.close();
            } 
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
    
    /**  
     *  connectWithClient informs the other brokers about publishers' topics, ip and port, 
     *  creates a server in order to accept client's requests and informs the consumer about all the available
     *  brokers and topics. It reads the topic requested from the client and then writes the specific topic in
     *  publisher_information.txt in order to inform the other brokers about the requested topic. Finally, it calls 
     *  hashForString and portForHash in order to find the suitable broker for the client's topic and writes 
     *  the broker's port in the consumer.
     */

    public static void connectWithClient(){
        try{
            System.out.println("Broker is open. Ip: " + addr.toString().replace("/","") + ", Port: " + portC);
            server_socket_pull = new ServerSocket(portC, 50, addr);
                System.out.println("Broker: Waiting for client connection");
                connection = server_socket_pull.accept();
                System.out.println("Broker: Connected to client");
                try {
                    outToConsumer = new ObjectOutputStream(connection.getOutputStream());
                    inFromConsumer = new ObjectInputStream(connection.getInputStream());
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("First Connection");
                outToConsumer.writeObject(channel_name1);
                System.out.println(channel_name1);
                outToConsumer.writeObject(channel_name2);
                //int broker_port1 = portForHash(Utils.hashForString(channel_name1));
                //int broker_port2 = portForHash(Utils.hashForString(channel_name2));
                //outToConsumer.writeObject(broker_port1);
                //outToConsumer.writeObject(broker_port2);
                outToConsumer.writeObject(topics1); // return the available topics 
                outToConsumer.writeObject(topics2);
                outToConsumer.writeObject(ip1);
                outToConsumer.writeObject(port1);
                outToConsumer.writeObject(ip2);
                outToConsumer.writeObject(port2);
        }
        catch (IOException io) {
            io.printStackTrace();
        } 
    
        finally {
            try {
                server_socket_pull.close();
            } 
            catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    /**  
     *  portForHash finds the port of the broker responsible for a specific topic.
     *  @param topic_hash is the BigInteger value of the topic.
     *  @return the port of the suitable broker.
     */

    public static int portForHash(BigInteger topic_hash){
        int portValue = 0;
        for (Map.Entry<Integer, BigInteger> broker : brokerList.entrySet()){
            if ((topic_hash.compareTo(broker.getValue()) == -1) || (topic_hash.compareTo(broker.getValue()) == 0)){
                portValue = (broker.getKey()).intValue();
                break;
            }
        }
        return portValue;
    }

    /**  
     *  pull creates a server in order to accept requests from the client, reads all publishers' information 
     *  from publisher_information.txt and creates a clienthandler object 
     *  in order to find whether the requested topic exists and which publisher owns it. It after creates 
     *  and starts a thread for the specific client.
     *  @param port is the port of the broker responsible for the requested topic.
     */

    public static void pull(int port){
        try{
            System.out.println("Broker is open. Ip: " + addr.toString().replace("/","") + ", Port: " + port);
            server_socket_pull = new ServerSocket(port, 50, addr);
            while(true){
                System.out.println("Broker: Waiting for client connection");
                connection = server_socket_pull.accept();
                System.out.println("Broker: Connected to client");
                ClientHandler clientThread = new ClientHandler(connection, topics, ip1, port1, ip2, port2,topics1,topics2,topic);
                clients.add(clientThread);

                Thread t1 = new Thread(clientThread);
                t1.start();

                try {
                    t1.join();
                }
                catch( Exception e) {
                    System.out.println("Interrupted");
                }
            }
        }
        catch (IOException io) {
            io.printStackTrace();
        } 
        finally {
            try {
                server_socket_pull.close();
            } 
            catch (IOException io) {
                io.printStackTrace();
            }
        }
    }

    /**
    * calculateKeys creates the broker list by assigning to each of the broker objects a number that has emerged by calling
    * getMD5 which returns a hexademical number that we convert to BigInteger to ensure the best possible accuracy, which we 
    * we then divide with 1000 and the remainder from the division is the broker's number.
    * calculateKeys has no parameters and throws a FileNotFound in case it can't find the txt that contains the port numbers.
    * @return brokerlist, which is now filled with tuples (broker_name, broker_number), which means that the broker with broker_name is
    * responsible for all the hashtags that have a number smaller or equal to broker_number.
     */

    public static HashMap<Integer, BigInteger> calculateKeys() throws FileNotFoundException{

        readIpAndPorts();
        BigInteger modulus = new BigInteger("1000");
        BigInteger [] hash = new BigInteger[3];

        for (int i=0; i<hash.length; i++){ 

            hash[i] = new BigInteger(Utils.getMD5(port[i] + ip[i]),16);
            hash[i] = hash[i].mod(modulus);
            
        }

        for(int i=0; i<3; i++){
            brokerList.put(Integer.parseInt(port[i]), hash[i]);
        }
        brokerList = Utils.sortByValue(brokerList);
        
        return brokerList;
    }

    /**  
     *  readIpAndPorts reads from br_port_ip.txt file all the ports and ip of the brokers
     *  it is used as a helper function in calculateKeys in order to find the hash of ip + port of each broker.
     */

    public static void readIpAndPorts() throws FileNotFoundException{
        try {
            int i = 0;
            File obj = new File("br_port_ip.txt");
            Scanner file = new Scanner(obj);
            
            while (file.hasNextLine()) {
                if (i < 3){
                    port[i] = (file.nextLine());
                }
                else{
                    ip[i-3] = (file.nextLine());
                }
                i++;
            }
            
            file.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        
    }

}