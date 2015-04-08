package com.bulbSmart;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;
//import com.tempeSensorPkg.tempeSensorImpl;

public class bulbSmart implements SmartCtrlInterfaces {

	public static int Bulb_state=0;
	public static String ipAddress="localhost";
	
	public static void readConfigIPFile()
	{
		 String filename = Const.CONFIG_IPS_FILE;
			String workingDirectory = System.getProperty("user.dir");
			File file = new File(workingDirectory, filename);
			try {
				if (file.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("Read Gateway IP from Configuration File!");
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
				 System.out.print("ip" + Const.GATEWAY_SERVER_IP);
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
	
	@Override
	public boolean change_state(int device_id, int state)
			throws RemoteException {
        if(device_id==Const.ID_DEVICE_BULB && state==1){
        	Bulb_state=1;
        	System.out.println("The Bulb is ON");
        	return true;
        }
       
        System.out.println("The Bulb is OFF");
        
		return false;
	}
	public static void reg(int type, String name){
		try {
			String s=InetAddress.getLocalHost().toString();
			String[] ip=s.split("/"); 
			ipAddress=ip[ip.length-1];
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
				gtwy.register(Const.TYPE_SENSOR, Const.NAME_DEVICE_BULB,ipAddress);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}	
		report(Bulb_state);
	}
	public static void report(int cur){
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
		} catch (RemoteException e) {
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
		                      gtwy.report_state(Const.ID_DEVICE_BULB, Bulb_state);
	    	            }
			            Thread.sleep(2000);
	    	        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				System.out.println("exception");
				e.printStackTrace();
			}				
	}
	
	//public static  
	public static void main(String[] args) throws RemoteException {
		if(0 == args.length)
		{  
			 System.out.println("lack of input parameter");
			 return;
		}
		
		if(args.length==1 && args[0].equals(Const.CONFIG_IPS_FILE))
		{
			readConfigIPFile();  // Run Task1 and Task2 if only one command line Argument of configuration file
		}	bulbSmart bulb = new bulbSmart();
		try{
			SmartCtrlInterfaces stub = (SmartCtrlInterfaces) UnicastRemoteObject.exportObject(bulb,0);
			Registry reg;
			
			try{
				reg = LocateRegistry.createRegistry(Const.SMART_BULB_SENSOR_PORT);       //heater port 1099 here;
				System.out.println("Smart Bulb java RMI registry created.");
			}
			catch(Exception e){
            	System.out.println(" Smart Bulb Using existing registry");
            	reg = LocateRegistry.getRegistry();
			}
			reg.rebind(Const.STR_LOOKUP_SMART_BULB, stub);
		}catch(RemoteException e) 
		{
        	e.printStackTrace();
        }
		reg(Const.TYPE_SMART_DEVICE,Const.NAME_DEVICE_BULB);
		
		report(Bulb_state);
			
	}


}
