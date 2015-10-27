John Schutz, Rebecca Nelson, Adam Boole

The first thing to take into account is that the Server and Client_Peer are seperate projects. They are both compiled and run sepeartely.


++++++++++
+ SERVER +
++++++++++

Start by compiling all of the .java files in the Server project and run the resulting Server.class
	-This requires 1 arguement
		1) Port to listen on (example "8335")
The Server will now be listening for clients.
When a connection is recieved it will report the connection and the connetents in its shared folder will be displayed.
Every heartbeat it recieves is reported along with the IP address of the client that sent it.
	**We decided a Server is not meant to ever be turned off, so there is no implemented method to turn it off.
	**Use whatever means desired to end program when finished (command line ctrl+c, NetBeans "Stop" button, etc)




+++++++++++++++
+ CLIENT_PEER +
+++++++++++++++

Next, compile and run the .java files in Client_Peer. Run the resulting Client_Peer.class 
	-This requires 3 arguements. 
		1) Server's IP address OR host name (either works)
		2) Port that it is listening on (our example above 8335)
		3) the folder you wish to share from (we have a folder named "sharable" in Client_Peer/src/ so "sharable" should suffice)

Once connected there are 3 options: Download, Status, Exit
	This will be refered to as the "Home option"

Type the name of the one you wish to use (not case sensitive)
	-Download
		You will be prompted for the file you are searching for, type its name (don't forget extension).
		If the file is shared by one or more peers, you will recieve a list of peers that have the file.
		Enter the number next to the peer you wish to download from (you won't be in that list, so you can't download from yourself).
		Enter -1 if you wish to exit without downloading.
			**If the files is not being shared, you will be notified and sent back to the Home options.
	-Status
		This option will return the list of files you have initiated a download of and current status (downloading or complete).
		You will be automatically be returned to the Home options
	-Exit
		Exits program