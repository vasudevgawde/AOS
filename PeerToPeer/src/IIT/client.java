package IIT;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;


public class client 
{
	public static void main(String args[])
	{
		Socket s = null;
		Socket clientSoc = null;
		String serverIP= globalVariable.serverIP;
		int CLIENTPORT = globalVariable.clientPort;
		int SERVERPORT = globalVariable.serverPort;
		ServerSocket s1 = null;
		InputStream inp = null;
		OutputStream out = null;
		ObjectOutputStream oos = null;
		ObjectInputStream ois = null;
		Scanner sc = new Scanner(System.in);
		
		
		
		int whichCase;
		boolean ContiFlag = true;
		Thread st = new Thread(new ClientThread());
		st.start();
		// String fileName="";
		while (ContiFlag)
		{
			System.out.println("Enter the operation which you want to perform");
			System.out.println("1 : Register a file");
			System.out.println("2 : Lookup for a file");
			System.out.println("3 : Exit");
			
			String  a= sc.next();
			whichCase = Integer.parseInt(a);
			System.out.println("Your selection   :  " + whichCase);
			switch (whichCase) 
			{
				case 1: 
				{
						
					
					try
					{
						
					s = new Socket(serverIP, SERVERPORT);
					inp = s.getInputStream();
					out = s.getOutputStream();
					oos = new ObjectOutputStream(out);
					System.out.println("Registering files... Please wait");
					File folder = new File("./Files/");
					File[] listOfFiles = folder.listFiles();
					ArrayList<String> newListOffiles = new ArrayList<String>();
					int k = 0;
					for (int i = 0; i < listOfFiles.length; i++) {
						if (listOfFiles[i].isFile()) {
							newListOffiles.add(k++,listOfFiles[i].getName());
						}

					}
					String ipAddress = getCurrentPeerIp();
					createFileList cr = new createFileList(ipAddress,newListOffiles, "1");
					oos.writeObject(cr);
						
					}
					
					catch (Exception E)
					{
						System.out.println("Error registering files to indexing server..Please make sure the files are present or the server is running");	
					}
					finally
					{
						if (null != s) 
						{
							try {
									s.close();
								} 
							catch (IOException e) 
							{
								e.printStackTrace();
							}
						}

					try {
						if (inp != null)
							inp.close();
						if (out != null)
							out.close();
						if (oos != null)
							oos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					}					
					break;
				}
				case 2 :
				{
					
					try
					{
						
						
						s = new Socket(serverIP, SERVERPORT);
						inp = s.getInputStream();
						out = s.getOutputStream();
						oos = new ObjectOutputStream(out);
						ArrayList<String> fileToSearchArr = new ArrayList<String>();
						System.out.println("Please enter the file name...");
						Scanner br1 = new Scanner(System.in);
						fileToSearchArr.add(0,br1.next());
						System.out.println("the file to search " + fileToSearchArr);
						String NO_IP = "NO_IP";
						createFileList cr1 = new createFileList(NO_IP, fileToSearchArr,
								"2");
						oos.writeObject(cr1);
						
				
						ois = new ObjectInputStream(inp);
						ArrayList<String> listOfHost = (ArrayList<String>)ois.readObject();
						System.out.println("Please select the host to copy");
						for (int ab = 0; ab < listOfHost.size(); ab++) 
						{
							System.out.println("Press " + ab + " : "
									+ listOfHost.get(ab));
						}
						
						Integer option = Integer.parseInt(br1.next());
						System.out.println("creating connection with : " +listOfHost.get(option) );
						
						InputStream is= null;
						String fileNameToTra = fileToSearchArr.get(0);
						int bytesRead;	
						OutputStream outClient = null;
						
						try
						{
							clientSoc = new Socket(listOfHost.get(option), CLIENTPORT);
							outClient = clientSoc.getOutputStream();
							ObjectOutputStream dot = new ObjectOutputStream(outClient); 
							dot.writeObject(fileNameToTra);
							is = clientSoc.getInputStream();
							
					        } catch (IOException ex)
					        {
					           System.out.println("Unable to connect to destination peer");
					        }
							
					 

					        if (is != null) 
					        {

					            File file = new File("./Download/"+fileNameToTra);
					        	FileOutputStream fos = null;
					            BufferedOutputStream bos = null;
					            try 
					            {
					                fos = new FileOutputStream(file);
					                bos = new BufferedOutputStream(fos);

					               // do 
					                //{
					                byte[] aByte = new byte[is.read()];            
					                	bytesRead = is.read(aByte, 0, aByte.length);
				                        //baos.write(aByte);
				                        bos.write(aByte);
				                        bytesRead = is.read(aByte);
					                //} while (bytesRead != -1);

					                bos.flush();
					                bos.close();
					                clientSoc.close();
							
					                System.out.println("File downloaded Successfully.");

					            }
					            catch (Exception E)
					            {
								System.out.println("Unable to send establish connection with peer to transfer files");
					            }
								finally
								{
									
									
									
								}
					        	}
							}
							catch (Exception E)
							{
								System.out.println("Error searching for a file .Please make sure both the peers are up and running");
								
							}
					finally
					{
						try 
						{
							if (null != s)
								s.close();
							if (inp != null)
								inp.close();
							if (out != null)
								out.close();
							if (oos != null)
								oos.close();
							
						} catch (IOException e) 
						{
							e.printStackTrace();
						}
					}	
					break;
				}
				case 3:
				{
					ContiFlag =false;
					break;
					
				}
			}
	
		}
	}
	
	private static String getCurrentPeerIp() {
		try {
			Enumeration<NetworkInterface> inetAddresses = NetworkInterface.getNetworkInterfaces();
			while (inetAddresses.hasMoreElements()) {
				NetworkInterface ia = inetAddresses.nextElement();
				for (InterfaceAddress inAddress : ia.getInterfaceAddresses()) {
					if (inAddress.getAddress().isSiteLocalAddress()) {
						return inAddress.getAddress().toString().split("/")[1];
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}
}

class ClientThread extends Thread 
{
	public void run() 
	{
		ServerSocket Soc = null;
		try {

			Soc = new ServerSocket(globalVariable.clientPort);

		} catch (Exception E) {
			System.out.println("Enable to establish connection");
		}
		
		while (true) 
		{
			Socket socInt=null;
			FileInputStream fis = null;
			ObjectInputStream inputStream = null;
			
			try 
			{
				socInt = Soc.accept();
				System.out.println("Connection Created :");

							
				inputStream = new ObjectInputStream(socInt.getInputStream());
				
				String fileName = (String)inputStream.readObject();
				System.out.println("Accepted connection : " + socInt);
				System.out.println("Sending " + fileName + "...");
				System.out.println("Requested file name :" + fileName);
				
				BufferedOutputStream toClient = new BufferedOutputStream(socInt.getOutputStream());
				 if (toClient != null)
				 {
		                File myFile = new File("./Files/"+fileName);
		                byte[] mybytearray = new byte[(int) myFile.length()];
		                try 
		                {
		                    fis = new FileInputStream(myFile);
		                } catch (FileNotFoundException ex)
		                {
		                    System.out.println("Error occured while opening file"+ ex.getMessage());
		                }
		                BufferedInputStream bis = new BufferedInputStream(fis);

		                try 
		                {
		                	toClient.write((int)myFile.length());
		                    bis.read(mybytearray, 0, mybytearray.length);
		                    toClient.write(mybytearray, 0, mybytearray.length);
		                    toClient.flush();
		                    toClient.close();
		                    socInt.close();

		                    // File sent, exit the main method
		                    return;
		                }
		                catch(IOException E)
		                {
		                	System.out.println("Error while cleaning system cache");
		                }
				 }
			}
			catch (Exception E)
			{
				System.out.println("Error while sending file...Try again");
				
			}
			finally
			{
			try {
					if (null != socInt)
						socInt.close();
					if (fis != null)
						fis.close();
					if (inputStream != null)
						inputStream.close();
					if(Soc != null)
						Soc.close();
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		
			}
		}
			
	}
}
class createFileList implements Serializable {
	String ipAddress;
	ArrayList<String> arrayList;
	String operationFlag;

	createFileList(String ip, ArrayList<String> arr, String operation) {
		this.ipAddress = ip;
		this.arrayList = arr;
		this.operationFlag = operation;
	}
}