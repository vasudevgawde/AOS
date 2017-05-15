package IIT;


import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;



public class server {
	static int PORT =globalVariable.serverPort;
	private static ConcurrentHashMap<String, ArrayList<String>> hash1 = new ConcurrentHashMap<String, ArrayList<String>>();
	public static void main(String args[]) throws Exception 
	{
		ServerSocket s1 = null;
		try{
		
		System.out.println("The server is running on PORT :" + PORT);
		Socket Socke = null;
		s1 = new ServerSocket(PORT);
		while (true)
		{
			Socke = s1.accept();

			SingleThread st = new SingleThread(Socke, hash1);
			st.start();
		}
		}catch (Exception E)
		{
			System.out.println("Error starting indexing server.");
		}
		finally {
			if (null != s1) {
				try {
					s1.close();
				} catch (IOException e) {
					System.out.println("Error in Closing Socket.");
					e.printStackTrace();
				}
			}
		}
	}
}
class SingleThread extends Thread {
	Socket Soc=null;
	ConcurrentHashMap<String, ArrayList<String>> hash;
	public SingleThread(Socket testSoc,
			ConcurrentHashMap<String, ArrayList<String>> hash) 
	{
		this.Soc = testSoc;
		this.hash = hash;	
	}
	public void run() 
	{
		ObjectInputStream ois;
		ObjectOutputStream oos;
		InputStream inp;
		OutputStream out;
		try
		{
			inp = Soc.getInputStream();
			ois = new ObjectInputStream(inp);
			createFileList crInt =(createFileList)ois.readObject();
			
			String operationCase = (crInt.operationFlag);
			
			if (operationCase.equals("1"))
			{
				try{
				registerFilesInHash(crInt.ipAddress,crInt.arrayList);
				for (java.util.Map.Entry<String, ArrayList<String>> entry : hash
						.entrySet()) 
				{
	
					System.out.println(entry.getValue()+"\n My File :  " + entry.getKey());
				}
				}
				catch (Exception E)
				{
					System.out.println("Error registering files");
				}
				finally
				{
				try {
					if (null != Soc)
						Soc.close();
					if (inp != null)
						inp.close();
					if (ois != null)
						ois.close();
			
				} catch (IOException e) {
					e.printStackTrace();
				}
				}
			
			}
			else
			{
				try{
				
				out= Soc.getOutputStream();
				oos = new ObjectOutputStream(out);
				String fileName = crInt.arrayList.get(0);
				ArrayList<String> arrIP = getClientIP(fileName);
				oos.writeObject(arrIP);
				}
				catch (Exception E)
				{
					System.out.println("Error searching files in index server");
				}
				
				finally
				{
				try {
					if (null != Soc)
						Soc.close();
					if (inp != null)
						inp.close();
					if (ois != null)
						ois.close();
			
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
				}
		}
		catch (Exception E)
		{
			System.out.println("Error at index server"  + E.getMessage());
		}
	}
	public void registerFilesInHash(String clientIP, ArrayList<String> listOfFilerNAmes) {
		System.out.println("Registering Client Files: " + clientIP);
		for (int i =0; i< listOfFilerNAmes.size(); i++) {
			String name = listOfFilerNAmes.get(i);
			if (!hash.containsKey(name)) {
				ArrayList<String> clientIps = new ArrayList<String>();
				hash.put(name, clientIps);
				clientIps.add(clientIP);
			} else {
				hash.get(name).add(clientIP);
			}
		}
		System.out.println("Client Files registered successfully.");
	}
	public ArrayList<String> getClientIP(String fName) {
		if (hash.containsKey(fName)) {
			return hash.get(fName);
		}
		return null;
	}
	
	
}



