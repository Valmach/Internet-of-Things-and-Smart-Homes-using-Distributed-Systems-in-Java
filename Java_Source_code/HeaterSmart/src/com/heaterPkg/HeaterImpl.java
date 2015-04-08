/**
 * Main class to run Smart Heater Outlet
 * Switches itselft off when temperature is below 0 degrees and Switches it self off when temperature is above 0 degrees
 */
package com.heaterPkg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;
//import com.GatewayServerImpl.GwServerInterfaceImpl;

public class HeaterImpl implements SmartCtrlInterfaces, Runnable{
	
	public static int HeaterState = Const.OFF;
	public static String ipAddress="localhost";
	
	
	/**
	 *  RegisterForGateWay() is Helper Method to create Registry so that Gateway can perform RMI. 
	 */
   private static void RegisterForGateWay()
   {
	   try {
			String s=InetAddress.getLocalHost().toString();
			String[] ip=s.split("/"); 
			ipAddress=ip[ip.length-1];
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HeaterImpl htobj = new HeaterImpl();
		try{
			SmartCtrlInterfaces stub = (SmartCtrlInterfaces) UnicastRemoteObject.exportObject(htobj,0);
			Registry reg;
			try{
				reg = LocateRegistry.createRegistry(Const.HEATER_PORT);       //heater port 1099 here;
				System.out.println("HeaterImpl java RMI registry created.");
			}
			catch(Exception e){
           	System.out.println(" HeaterImpl Using existing registry");
           	reg = LocateRegistry.getRegistry();
			}
			reg.rebind(Const.STR_LOOKUP_HEATER, stub);
		}catch(RemoteException e) 
		{
       	e.printStackTrace();
       }
   }
   /**
    * readConfigIPFile() is the Helper function to read the ipAddress of GAteway from the configure file.
    */
	public static void readConfigIPFile()
	{
		 	String filename = Const.CONFIG_IPS_FILE;
			String workingDirectory = System.getProperty("user.dir");
			File file = new File(workingDirectory, filename);
			try {
				if (file.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("Read the Gateway IP Address from the Configuration File");
				}
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	 
	        BufferedReader reader = null;   
	        try {    
	            reader = new BufferedReader(new FileReader(file));   
	            String tempString = null;   
	            int line = 1;   
	            while ((tempString = reader.readLine()) != null) {    
	                switch(line)
	                {
		                case 1:
		                	Const.GATEWAY_SERVER_IP = tempString;
		                	break;
		                default:
		                		break;
	                }
	                line++;
	            }
	            reader.close();   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            if (reader != null) {   
	                try {   
	                    reader.close();   
	                } catch (IOException e1) {   
	                }   
	            }   
	        }   
	}
	/**
	 * change_state(int device_id, int state) is accessed Remotely by the Gateway to 
	 */
	public boolean change_state(int device_id, int state) throws RemoteException {
		// TODO Auto-generated method stub
		
		//only smart device are considered to be on/off by user process or gateway
		if(device_id == Const.ID_DEVICE_OUTLET)
		{
			if(state == Const.OFF)
			{
				HeaterState = Const.OFF;
			     System.out.println("The Heater is OFF");
			}
			else
			{
				HeaterState = Const.ON;
			    System.out.println("The Heater is ON");
			}
			return true;
		}
		else
		{
		     System.out.println("device ID is wrong");
			return false;
		}
}
	

	    public void run() {
	        Registry regs = null;
	        try {
	            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
	        } catch (RemoteException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	        GatewayAllInterfaces gtwy=null;
	        
	        try {
	            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
	    	        while(true){
	    	            Scanner in1= new Scanner(System.in);
	    	            System.out.println("Need to Report the State Enter Y or N");
	    	            if(in1.nextLine().equals("Y"))
	    	            {
		                      gtwy.report_state(Const.ID_DEVICE_OUTLET, HeaterState);
	    	            }
			            Thread.sleep(2000);
	    	        }
	            //System.out.println("came here3");
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            System.out.println("exception");
	            e.printStackTrace();
	        }		
	        
	    }
	
	public static void reg(int type, String name){
        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            gtwy.register(type, name,ipAddress);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }
    }
	public static void main(String[] args) throws RemoteException {
		if(0 == args.length)
		{  
			 System.out.println("lack of input parameter");
			 return;
		}
		
		if(args.length==1 && args[0].equals(Const.CONFIG_IPS_FILE))
		{
			readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
		}
        reg(Const.TYPE_SMART_DEVICE, Const.NAME_DEVICE_OUTLET);

        RegisterForGateWay();			//for the change state function
        
		HeaterImpl mrt = new HeaterImpl();
	        Thread t = new Thread(mrt);
	        t.start();
		
	}
}
