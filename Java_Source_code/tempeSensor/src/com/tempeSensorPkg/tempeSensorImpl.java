/**
 * Temperature Sensor to Report the current temperature
 */
package com.tempeSensorPkg;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;



public class tempeSensorImpl implements RMIDevicesInterfaces,Runnable {
    private static int currTemp=0;  // Static Variable to store the Temperature value 
    
    public static String ipAddress="localhost";
    private static LinkedHashMap<Double, Integer> tempEventValueHash = new LinkedHashMap<Double, Integer>();//store the test input event action 
    private static ArrayList<Double> timeEventArray = new ArrayList<Double>();
    public static double currentTempTimeStamp = 0;
    public static boolean startNextEventAction = false;
/**
 * query_state(int device_id)   is used to be accessed remotely by the Gateway
 */
    public int query_state(int device_id) throws RemoteException {
        if(device_id==Const.ID_SENSOR_TEMPERATURE){
            System.out.println("The Current Temperature is"+currTemp);
            return currTemp;
        }
        System.out.println("Function called with Wrong device ID");
        return -1;
    }
    /**
     * report(int cur)   is Helper Method to report the current temperature to the Gateway 
     * @param cur
     */
    public static void report(int cur){
        System.out.println("came here");
        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            gtwy.report_state(Const.ID_SENSOR_TEMPERATURE, cur);
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }
    }
   /**
    * readConfigIPFile()   is Helper Method to Read the IP address from the Configuration file
    */
    public static void readConfigIPFile(){
        String filename = Const.CONFIG_IPS_FILE;
        String workingDirectory = System.getProperty("user.dir");
        File file = new File(workingDirectory, filename);
       // System.out.println("Final filepath : " + file.getAbsolutePath());
        try {
            if (file.createNewFile()) {
                System.out.println("File is created!");
            } else {
                System.out.println("Read Gateway IP Address from Configuration file!");
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
     * reg(int type, String name)   is Helper Method to allow the Temperature Sensor to register at Gateway
     *    calls the register method on Gateway Remotely
     */
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
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE,ipAddress);
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }  
    }
    /**
     * CreateRegisterForGatewayLookup()   is Helper Method to create RMI registry at Temperature sensor.  
     */
    private static void CreateRegisterForGatewayLookup()
    {
        tempeSensorImpl htobj = new tempeSensorImpl();
        try{
            RMIDevicesInterfaces stub = (RMIDevicesInterfaces) UnicastRemoteObject.exportObject(htobj,0);
            Registry reg;
            try{
                reg = LocateRegistry.createRegistry(Const.TEMP_SENSOR_PORT);       //heater port 1099 here;
                System.out.println("Temp Sensor java RMI registry created.");
            }
            catch(Exception e){
                System.out.println(" Temp sensor Using existing registry");
                reg = LocateRegistry.getRegistry();
            }
            reg.rebind(Const.STR_LOOKUP_TEMP, stub);
        }catch(RemoteException e)
        {
            e.printStackTrace();
        }
    }
    /**
     * runTasks() is Helper Method to run in User interactive Mode
     */
    private static void runTasks()
    {
    	 readConfigIPFile();
         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
         
         CreateRegisterForGatewayLookup();  
         
         while(true){
         Scanner in = new Scanner(System.in);
         System.out.println("Enter Current Temprature");
         currTemp = Integer.parseInt(in.nextLine());
         
         Scanner in1 = new Scanner(System.in);
         System.out.println("Need to Report the State Enter Y or N");
         if(in1.nextLine().equals("Y")||in1.nextLine().equals("y") ){
        	 report(currTemp);
        	
         }
         }
    }
    /**
     * readTestInput() is Helper Method to read the input for test cases given by the test input file
     */
    private static void readTestInput()
    {
		String workingDirectory = System.getProperty("user.dir");
		String TestCaseFile = Const.TEST_INPUT_FILE;
		TestCaseFile = workingDirectory.concat("/"+TestCaseFile);
		System.out.print(TestCaseFile);
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";
		try {
	 
			br = new BufferedReader(new FileReader(TestCaseFile));
			int lineNumCount = 0;
			while ((line = br.readLine()) != null) {
			        // use comma as separator
				String[] lineInformation = line.split(cvsSplitBy);
				System.out.print(lineNumCount);
				++lineNumCount;
				if(2 >= lineNumCount)
				{
					if(2 == lineNumCount)
					{
						double timestamp = Double.parseDouble(lineInformation[0]);
						timeEventArray.add(timestamp);
					}
					continue;
				}
				double timestamp = Double.parseDouble(lineInformation[0]);
				timeEventArray.add(timestamp);
				int value = Integer.parseInt(lineInformation[1]);
				tempEventValueHash.put(timestamp, value);
				System.out.println("lineInformation [code= " + lineInformation[0] 
	                                 + " , name=" + lineInformation[1] + "]");
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
    /**
     * NotifyGatewayFinish() is Helper function to indicate the Gateway that it had updated temperature value.
     * So that the Gateway will be eligible to query
     */
    public static void NotifyGatewayFinish(){
        System.out.println("came here");
        Registry regs = null;
        try {
            regs = LocateRegistry.getRegistry(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null; 
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            gtwy.FinishCurrentTimeEventToGateway(1);
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }
    }
/**
 * runTestCases() is Helper function to run the test cases from an input file Without user interaction. 
 */
	private static void runTestCases()
	{		
    	readConfigIPFile();
    	
    	readTestInput();
    	
    	CreateRegisterForGatewayLookup();
        if(0 == currentTempTimeStamp)
        {
            System.out.println("currentTempTimeStamp tattat" + currentTempTimeStamp);
            reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_TEMPERATURE);
        }
        
        while(true)
        {
           System.out.println("currentTempTimeStamp ttad" + currentTempTimeStamp);

        	if(startNextEventAction)
        	{
	            System.out.println("startNextEventAction dd" + startNextEventAction);

	        	//report this current time event's temperature value;
	        	currTemp = tempEventValueHash.get(currentTempTimeStamp);
        		startNextEventAction = false;
        		//tell gateway to finish current event action
        		tempeSensorImpl r= new tempeSensorImpl(); 
                Thread t = new Thread(r);
                t.start();
        		if(currentTempTimeStamp == timeEventArray.get(timeEventArray.size()-1))
        		{
        			break;
        		}
        		
        	}
	    }
	}
	/**
	 * query_state(double time, int device_id) is remotely accessed by the Gateway to get the time stamp value.
	 */
	public int query_state(double time, int device_id) throws RemoteException {
		 if(device_id==Const.ID_SENSOR_TEMPERATURE){
	            System.out.println("The Current Temperature is"+tempEventValueHash.get(time));
	            return currTemp;
	        }
	        System.out.println("Function called with Wrong device ID");
			return -1; 
	}
/**
 *  Method to be Remotely Accessed by the Gateway to provide the Logical clock ticking.
 *  This is done in a new thread to avoid Blocking. 
 */
	@Override
	public void NotifySensorEventAction(double time) throws RemoteException {
		NotifySensorTemp n=new NotifySensorTemp(time);
		Thread nwThread= new Thread(n);
		nwThread.start();	}
	/**
	 * Method to create a seperate thread to run the NotifyGatewayFinish() method.
	 */
	public void run() {
		NotifyGatewayFinish(); 
	}
	
	 public static void main(String[] args) throws RemoteException {
			if(0 == args.length)
			{  
				 System.out.println("lack of input parameter");
				 return;
			}
			
			if(args.length==1 && args[0].equals(Const.CONFIG_IPS_FILE))
			{
				runTasks(); // Run Task1 and Task2 if only one command line Argument of configuration file
			}
			else if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
			{
				runTestCases(); //Run Test cases from the input file if two command line Arguments are present.
			}
	    }
	
}
