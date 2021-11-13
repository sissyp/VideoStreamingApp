# VideoStreamingApp

The aim of the specific project was the implementation of a video streaming Application, which supports the uploading and downloading of videos. 
The code was written in visual studio code. The ip: 192.168.1.11 and different ports for every node were used so as the code could run in any computer.

For the purposes of the project, 3 broker nodes and 2 appnodes were created, while the hash function MD5 was used to distribute  
the topics to the brokers, so as to every broker will be responsible for approximately the same number of topics. 
The video files can be found at the specific link : https://drive.google.com/drive/folders/1sHM1mU-y0siq8Pvi9bAifnN-m-BmAHPD?usp=sharing .
To run the code it is important that the whole dataset folder to be downloaded, including its subfolders and videos.
From the downloaded folder the dataset subfolder should be removed and placed in the folder that contains the project files.

The broker that listens to port 200 has been defined as the main broker, who is responsible for the communication with the appnodes (producers and consumers).

Instructions in order to run and execute the code in visual studio code: 

New Terminal -> javac *.java -> java Broker 192.168.1.11 200 

New Terminal -> java Broker 192.168.1.11 888

New Terminal -> java Broker 192.168.1.11 4675

New Terminal -> java Publisher 192.168.1.11 1234 channel_name1

New Terminal -> java Publisher 192.168.1.11 743 channel_name2 

where channel_name1, channel_name2 are the names of the channels for each user, 
while in order to run the code at the same computer we should replace the ip: 192.168.1.11 with the computer's specific ip.
The same process should be repeated for the Constants file for BROKER_IP.
