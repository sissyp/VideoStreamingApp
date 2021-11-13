import java.io.*;
import java.util.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils{

     /**  
     *  getMD5 is the hash function MD5. 
     *  @param input is the input string.
     *  @return hexadecimal number for input string.
     */
    
    public static String getMD5(String input){

        try {
  
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
  
            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());
  
            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);
  
            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } 
  
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
    * sortByValue sorts the hashmap based on the value parameter
    * @param HashMap<Integer,BigInteger> hm is the hashmap we want to sort
    * @return sorted hashmap
     */

    public static HashMap<Integer, BigInteger> sortByValue(HashMap<Integer, BigInteger> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, BigInteger> > list =
               new LinkedList<Map.Entry<Integer, BigInteger> >(hm.entrySet());
  
        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<Integer, BigInteger>>() {
            public int compare(Map.Entry<Integer, BigInteger> o1, 
                               Map.Entry<Integer, BigInteger> o2)
            {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
          
        // put data from sorted list to hashmap 
        HashMap<Integer, BigInteger> temp = new LinkedHashMap<Integer, BigInteger>();
        for (Map.Entry<Integer, BigInteger> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }
    
    /**
    * hashForString returns a three digits number after calling hash function MD5
    * @param topic is the input String 
    * @return a three digits number
     */

    public static BigInteger hashForString(String topic){
        BigInteger modulus = new BigInteger("1000");
        BigInteger topic_hash = new BigInteger(getMD5(topic),16);
        topic_hash = topic_hash.mod(modulus);
        return topic_hash;
    }
}