/**
 * Reports the Gate way the presence of any motion in the region . It is both push and pull based.
 */
package com.motionSensorPkg;

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
import java.util.LinkedHashMap;
import java.util.Scanner;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;


public class motionSensorImpl implements Runnable,RMIDevicesInterfaces {
    
    private static int motionState = Const.OFF;
    public static String ipAddress="localhost"; // Default ipAddress is localhost. In the program we take IpAddress from Machine and Override
  
    private static LinkedHashMap<Double, Integer> motionEventValueHash = new LinkedHashMap<Double, Integer>();
    private static ArrayList<Double> timeEventArray = new ArrayList<Double>();

    public static double currentMotionTimeStamp = 0;
    public static boolean startNextEventAction = false;
    
    /**
     * readConfigIPFile() is used to Read the Ip address of the Gateway from configuration file. 
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
                System.out.println("Read the IpAddress from the Configuration file");
            }
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        
        BufferedReader reader = null;
        try {
           // System.out.println("read begin：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                
                switch(line)
                {
                    case 1:
                        Const.GATEWAY_SERVER_IP = tempString;
                       // System.out.println("line " + line + ": " + tempString);
                        break;
                    default:
                        break;
                }
                line++;
            }
            
           // System.out.println(Const.GATEWAY_SERVER_IP+"outside");
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
     * reg(int type, String name)  is a Helper Method to register at the Gateway and send its ipAddress to the Gateway
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        GatewayAllInterfaces gtwy=null;
        
        try {
            gtwy = (GatewayAllInterfaces)regs.lookup(Const.STR_LOOKUP_GATEWAY);
            gtwy.register(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION,ipAddress);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }
        
    }
    /**
     * Creating a Seperate thread to push notifications to the Gateway
     */
    @Override
    public void run() {
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
	               if(motionState==1)
                       gtwy.report_state(Const.ID_SENSOR_MOTION, motionState);
	               Thread.sleep(5000);
            }
        } catch (Exception e) {
            System.out.println("exception");
            e.printStackTrace();
        }		    
    }
    
   /**
    * Gate way can remotely query the query_state(int device_id) method to get the current Temperature
    */
    public int query_state(int device_id) throws RemoteException {
        if(device_id==Const.ID_SENSOR_MOTION){
            System.out.println("The Current Temperature is"+motionState);	
            return motionState;
        }
        System.out.println("Function called with Wrong device ID");
        return -1;
    }
   /**
    * Creating a Registry so that RMI can be performed by Gateway
    */
    private static void CreateRegisterforGatewayLookup()
    {
        motionSensorImpl htobj = new motionSensorImpl();
        try{
            RMIDevicesInterfaces stub = (RMIDevicesInterfaces) UnicastRemoteObject.exportObject(htobj,0);
            Registry reg;
            try{
                reg = LocateRegistry.createRegistry(Const.MOTION_SENSOR_PORT);       //heater port 1099 here;
                System.out.println("Motion Sensor java RMI registry created.");
            }
            catch(Exception e){
                System.out.println("Motion sensor Using existing registry");
                reg = LocateRegistry.getRegistry();
            }
            reg.rebind(Const.STR_LOOKUP_MOTION, stub);
        }catch(RemoteException e)
        {
            e.printStackTrace();
        }
        
    }
    /**
     * runTasks()  is Helper method run Task2 specified in the assignment. The implementation of smart bulb through Gateway
     */
    public static void runTasks()
    {
    	 readConfigIPFile(); 
        // System.out.println(Const.GATEWAY_SERVER_IP+"outside");
         reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION);
         
         motionSensorImpl mrt = new motionSensorImpl();
         Thread t = new Thread(mrt);
         t.start();
         
         CreateRegisterforGatewayLookup();
         
         while(true){
             Scanner in= new Scanner(System.in);
             System.out.println("Please Enter 1 if Motion is Present Enter 0 if No motion");
             motionState = Integer.parseInt(in.nextLine());
         }
    }
    /**
     * readTestInput() is the Helper fucntion to get the input from the test file to check for test cases. 
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
				int value = Integer.parseInt(lineInformation[2]);
				motionEventValueHash.put(timestamp, value);
				System.out.println("lineInformation [code= " + lineInformation[0] 
	                                 + " , name=" + lineInformation[2] + "]");
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
     * Report the Gateway that reading of the input test case file is done 
     * @param time
     * @param reportStateorFinish
     */
    public static void reportStateOrFinishforTestCase(double time, boolean reportStateorFinish) {
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
            if(!reportStateorFinish)   //false is for report state
            {
               gtwy.report_state(Const.ID_SENSOR_MOTION, motionState, time);
            }
            else
            {
            	gtwy.FinishCurrentTimeEventToGateway(1);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("exception");
            e.printStackTrace();
        }		   
    }
    /**
     * Helper function to inform to run Test cases given in an input file.
     */
    private static void runTestCases()
	{	
    	//read IP configures
    	readConfigIPFile();
    	//read TestInputCsvFile
    	readTestInput();
    	
    	CreateRegisterforGatewayLookup();
    	
    	if(0 == currentMotionTimeStamp)
        {
              System.out.println("currentMotionTimeStamp qqq" + currentMotionTimeStamp);
              reg(Const.TYPE_SENSOR, Const.NAME_SENSOR_MOTION);
        }
    	
        while(true)
        {
            System.out.println("currentMotionTimeStamp adfqee" + currentMotionTimeStamp);
        	if(startNextEventAction)
        	{
                System.out.println("startNextEventAction aa" + startNextEventAction);
	           int currTimeStampMotion = motionEventValueHash.get(currentMotionTimeStamp);
	           if(currTimeStampMotion != motionState)
	           {
	        	   reportStateOrFinishforTestCase(currentMotionTimeStamp, false);   //report state;
	           }
       
            	reportStateOrFinishforTestCase(currentMotionTimeStamp, true);   //report finish;
        		startNextEventAction = false;
        		if(currentMotionTimeStamp == timeEventArray.get(timeEventArray.size()-1))
        		{
        			break;
        		}
        	}
	    }  
	}
    


	@Override
	/**
	 * The Gateway can access remotely query_state(double time, int device_id) method to check if there exists motion or not.
	 */
	public int query_state(double time, int device_id) throws RemoteException {
		// TODO Auto-generated method stub
	    if(device_id==Const.ID_SENSOR_MOTION){
            System.out.println("The Current Temperature is"+motionEventValueHash.get(time));	
            return motionState;
        }
        System.out.println("Function called with Wrong device ID");
        return -1;
	}

	@Override
	/**
	 * NotifySensorEventAction(double time) is called my the Gateway remotely so that motion sensor can proceed further
	 *  using Logical clock.
	 *  This is implemented using a new Thread to avoid Blocking call.
	 */
	public void NotifySensorEventAction(double time) throws RemoteException {
		NotifySensor n=new NotifySensor(time);
		Thread nwThread= new Thread(n);
		nwThread.start();
	}
	
    public static void main(String args[]){
  		if(args.length==0)
  		{  
  			 System.out.println("lack of input parameter");
  			 return;
  		}
  		
  		if(args.length==1 && args[0].equals(Const.CONFIG_IPS_FILE))
  		{
  			runTasks(); // One Command Line Argument means the Application can run in Manual Mode
  		}
  		else if(args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
  		{
  			runTestCases(); // Two Command Line Arguments means the Application can run in Test case file Mode, Automatically taking from test case file 
  		}
  		
      }
}