package com.GatewayServerImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;

public class GwServerInterfaceImpl implements GatewayAllInterfaces{
	
	    public static HashMap<String, Integer>  devices_name_id = new HashMap<String, Integer>();               //<key, value> is <name, id>
	    public static HashMap<Integer, Integer>  Device_States = new HashMap<Integer, Integer>();
	    public static String userMode="HOME";
	    
	    public static int TasksOrTestcaseFunction = 0;				//TasksOrTestcaseFunction = 0 for task1 and task2,  TasksOrTestcaseFunction = true for TestCase;
	    public static LinkedHashMap<Double, String> GatewayEventValueHash = new LinkedHashMap<Double, String>();		//store the test input event action 
	    private static ArrayList<Double> timeEventArray = new ArrayList<Double>();
	    public static LinkedHashMap<Double, String> GatewayOutputHash = new LinkedHashMap<Double, String>();
	    public static ArrayList<Double> GatewayOutputArrayStr = new ArrayList<Double>();
	    
	    public static HashMap<Integer,String> storeIpAddressSensorsDevices = new HashMap<Integer,String>();
	    
	    private static boolean beginRegisterTime0Finish = false;
	    private static int FinishOneEventSymbol = 0;
	    private static boolean ReceivedMotionReport = false;
	
	    protected GwServerInterfaceImpl() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void register(int type, String name, String ip) throws RemoteException {
		// TODO Auto-generated method stub
        System.out.println("log register type  " + type+ " name "+name);
		if(Const.TYPE_SENSOR == type)
        {
            if(name.equals(Const.NAME_SENSOR_TEMPERATURE))
            {
                devices_name_id.put(name, Const.ID_SENSOR_TEMPERATURE);          //temperature sensor's id 1
                Device_States.put(Const.ID_SENSOR_TEMPERATURE, 0);  //0 degree of temperature
                storeIpAddressSensorsDevices.put(Const.ID_SENSOR_TEMPERATURE,ip);
                
                System.out.println("log dadad type  " + type+ " name "+name);

                if(1 == TasksOrTestcaseFunction)
                {
                	//current time
                	// create a date
                	java.util.Date date= new java.util.Date();
                	Timestamp tStmp = new Timestamp(date.getTime());
	             	String time = tStmp.toString();
	                System.out.println(time);

	        		String cvsSplitBy = ":";
	        		 // use comma as separator
	        		String[] timeStr = time.split(cvsSplitBy);  
	        		String second = timeStr[timeStr.length-1];
	                System.out.println("dddd"+second);
	                
	                double s = System.currentTimeMillis() / 1000000000;
	                System.out.println("tttt"+s);

	              //  double currentRealTime
	                //curent timestamp which is 0 
	                double timeOutputKey = 0;
	                
	                String value = "0,0";						//initialized value, lazy!!!!!
	               	 //output to the OutputHash file
	                GatewayOutputHash.put(timeOutputKey, value);
	                GatewayOutputArrayStr.add(timeOutputKey);
	               beginRegisterTime0Finish = true;
	                
                }
            }
             if(name.equals(Const.NAME_SENSOR_MOTION))
             {
                 devices_name_id.put(name, Const.ID_SENSOR_MOTION);          //motion sensor's id 2
                 Device_States.put(Const.ID_SENSOR_MOTION, Const.NO_MOTION);
                 storeIpAddressSensorsDevices.put(Const.ID_SENSOR_MOTION,ip);//no motion;
             }
        }
       else if(Const.TYPE_SMART_DEVICE == type)
        {
             if(name.equals(Const.NAME_DEVICE_BULB))
             {
                 devices_name_id.put(name, Const.ID_DEVICE_BULB);
                 Device_States.put(Const.ID_DEVICE_BULB, Const.OFF);  //bulb off;
                 storeIpAddressSensorsDevices.put(Const.ID_DEVICE_BULB,ip);
             }
             if(name.equals(Const.NAME_DEVICE_OUTLET))
             {
                 devices_name_id.put(name, Const.ID_DEVICE_OUTLET);
                 Device_States.put(Const.ID_DEVICE_OUTLET, Const.OFF);  //smart outlet off;
                 storeIpAddressSensorsDevices.put(Const.ID_DEVICE_OUTLET,ip);
             }
        }
		
	}

	@Override
	public void report_state(int device_id, int state) throws RemoteException {
		// TODO Auto-generated method stub
        System.out.println("report_state ' s enter here");

		switch(device_id)
		{
			case Const.ID_SENSOR_TEMPERATURE:
			{
                System.out.println("temperature sensor' s current temperature is : " + state);
				break;
			}
			case Const.ID_SENSOR_MOTION: 
				{
					  if(Const.ON == state)
		                {
		                    if(userMode.equals(Const.USER_MODE_HOME)){
		                        try {
		                            TimerforMotion.main();
		                        } catch (InterruptedException e) {
		                            // TODO Auto-generated catch block
		                            e.printStackTrace();
		                        }
		                    }
		                    if(userMode.equals(Const.USER_MODE_AWAY)){
		                        SendNotification.main();
		                    }
		                    System.out.println("motion sensor's is motion yes");
		                }
		                else
		                {
		                    System.out.println("motion sensor's is motion no");
		                }
		                break;
				}
			case Const.ID_DEVICE_BULB: 
			{
				 if(Const.ON == state)
	                {
	                    System.out.println("bulb device's state is on ");
	                }
	                else
	                {
	                    System.out.println("bulb device's state is off ");
	                }          
				break;
			}
			case Const.ID_DEVICE_OUTLET:
				{
					   if(Const.ON == state)
		                {
		                    System.out.println("smart outlet device's state is on ");
		                }
		                else
		                {
		                    System.out.println("smart outlet device's state is off ");
		                }    
					break;
				}
			default:break;
		}
		
		Device_States.put(device_id, state);          //if state changed, update
	}
	
    @Override
    public void change_mode(String mode) throws RemoteException {
        if(mode.equals(Const.USER_MODE_AWAY)){
            userMode=Const.USER_MODE_AWAY;
        }
        if(mode.equals(Const.USER_MODE_HOME)){
            userMode=Const.USER_MODE_HOME;
        }
    }
    
    
    public static void readConfigIPFile()
	{
		 String filename = Const.CONFIG_IPS_FILE;
			String workingDirectory = System.getProperty("user.dir");
			File file = new File(workingDirectory, filename);
			try {
				if (file.createNewFile()) {
					System.out.println("File is created!");
				} else {
					System.out.println("Gate Way Started");
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
	
	
	public static void runTasks() throws InterruptedException
	{
		//read configure IPs from file
		readConfigIPFile();	
        //receive report_state,  the gateway as a server  
		MultiThreadReceive mltThRev = new MultiThreadReceive(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);		
		mltThRev.start();
		mltThRev.join();
		
		//iterate check
		while(true)
		{
		    //query state,  the gateway as a client
			 System.out.print("Change Smart Device State, Please enter 1\n");
			 System.out.print("Query Sensor Device State, Please enter 2\n");
			 System.out.print("Enter your option: ");
			 //  open up standard input
			 
			 Scanner in = new Scanner(System.in);
			 int option = in.nextInt();
		      //  read the username from the command-line; need to use try/catch with the
		      //  readLine() method
		    System.out.println("Thanks for the option, " + option);
		    if(1 == option)															//change state;
		    {
				 System.out.print("change Smart Bulb State, Please enter 3\n");
				 System.out.print("change Smart Heater State, Please enter 4\n");
				 
				 Scanner inCommand = new Scanner(System.in);
				 int deviceId = inCommand.nextInt();
				 switch(deviceId)
				 {
					 case 3: 
					 {
						 do{
								 System.out.print("Want to Change to OFF, Please enter 0\n");
								 System.out.print("Want to Change to ON, Please enter 1\n");
								 Scanner inCommd = new Scanner(System.in);
								 int state = inCommd.nextInt();
								 if((state == Const.ON) || (state == Const.OFF))
								 {
								//	 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(true, Const.CLIENT_SMART_BULB_IP,
								//			 Const.SMART_BULB_SENSOR_PORT, deviceId, state, 0);
									 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(true,storeIpAddressSensorsDevices.get(Const.ID_DEVICE_BULB),
											 Const.SMART_BULB_SENSOR_PORT, deviceId, state, 0);
									 mThreadChangeSt.start();
									 mThreadChangeSt.join();
									 break;
								 }
								 else
								 {
									 System.out.print("Wrong input, Please input again\n\n");
								 }
						 }while(true);
						 break;
					}
					 case 4: 
					 {
						 do{
							 System.out.print("Want to Change to OFF, Please enter 0\n");
							 System.out.print("Want to Change to ON, Please enter 1\n");
							 Scanner inCommd = new Scanner(System.in);
							 int state = inCommd.nextInt();
							 if((state == Const.ON) || (state == Const.OFF))
							 {
								 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(true, storeIpAddressSensorsDevices.get(Const.ID_DEVICE_OUTLET),
										 Const.HEATER_PORT, deviceId, state, 0);
								 mThreadChangeSt.start();
								 mThreadChangeSt.join();
								 break;
							 }
							 else
							 {
								 System.out.print("Wrong input, Please input again\n\n");
							 }
							 }while(true);
						 break;
					 }
					 default: 
					 {
							 System.out.print("Wrong input, Please input again\n\n");
							 break;		 
					 }
				 }
		    }
		    else if(2 == option)								//query state;
		    {

				 System.out.print("Query Temperature State, Please enter 1\n");
				 System.out.print("Query Motion State, Please enter 2\n");
				 Scanner cmd = new Scanner(System.in);
				 int deviceId = cmd.nextInt();
				 switch(deviceId)
				 {
					 case 1: 
					 {
						 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE),
								 Const.TEMP_SENSOR_PORT, deviceId, 0, 0);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 break;
					 }
					 case 2: 
					 {
						 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
								 Const.MOTION_SENSOR_PORT, deviceId, 0, 0);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 break;
					 }
					 default: 
					 {
						 System.out.print("Wrong input, Please input again\n\n");
						 break;		 
					 }
				 }
		    }
		    else
		    {
				 System.out.print("Wrong input, Please input again\n\n");
		    }
		}
	}
	
	
	public static void readTestCaseInput()
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
						String eventValue = "";
						GatewayEventValueHash.put(timestamp, eventValue);
					}
					continue;
				}
				double timestamp = Double.parseDouble(lineInformation[0]);
				timeEventArray.add(timestamp);
				String eventValue = "";
				if(lineInformation.length >= 4)
				{
					eventValue = lineInformation[3]; 
					System.out.println("lineInformation [timestamp= " + lineInformation[0] 
                            + " , eventValue =" + lineInformation[3]);
				}
				GatewayEventValueHash.put(timestamp, eventValue);
				System.out.println("lineInformation [timestamp= " + lineInformation[0]);
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
	
	public static void runTestCases() throws InterruptedException, IOException
	{		
		TasksOrTestcaseFunction = 1;            //testCase now
		//read configure IPs from file
		readConfigIPFile();	
		//read TestInputCsvFile
		readTestCaseInput();
		
        //receive register at timestamp 0,  the gateway as a server  
		MultiThreadReceive mltThRev = new MultiThreadReceive(Const.GATEWAY_SERVER_IP, Const.GATEWAY_PORT);		
		mltThRev.start();
		mltThRev.join();
		
		//update the time to do next time
		while(!beginRegisterTime0Finish)   //wait to register
		{
			System.out.println("beginRegistering");

		}
			System.out.println("beginRegiste ttttt");

		int currLoop = 1;
		while(true)
		{
		//	System.out.println("beginRegiste");
			//notify sensor to register
			double currtime = timeEventArray.get(currLoop);
			NotifySensorAction(storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE), Const.TEMP_SENSOR_PORT, currtime, Const.ID_SENSOR_TEMPERATURE);
			NotifySensorAction(storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION), Const.MOTION_SENSOR_PORT, currtime, Const.ID_SENSOR_MOTION);

			while(2 != FinishOneEventSymbol)
			{
	            System.out.println("FinishOneEventSymbol" + FinishOneEventSymbol);
			}
			FinishOneEventSymbol = 0;
			//gateway begin to have action
			String gatewayAction = GatewayEventValueHash.get(currtime);
            if(!gatewayAction.equals(""))
            {
	            System.out.println(gatewayAction);
	            if(!gatewayAction.contains(";"))
	            {
	            	if(gatewayAction.equals(Const.GW_TEMP_ACTION))   //if it's Q(Temp), query temp
	            	{
	            		 MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE),
								 Const.TEMP_SENSOR_PORT, Const.ID_SENSOR_TEMPERATURE, 1, currtime);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 
						 String outValue = "";
						 if(GatewayOutputHash.containsKey(currtime))
				        {
				            String temp = GwServerInterfaceImpl.GatewayOutputHash.get(currtime);
				            try{
				            outValue = temp + "," +Integer.toString(Device_States.get(Const.ID_SENSOR_MOTION));
				            }catch(Exception e){
				            	
				            }
				        }
				        System.out.println(" currtime Temp here" + currtime + "outValue " + outValue);
				               	 //output to the OutputHash file
				        GatewayOutputHash.put(currtime, outValue);
	            	}
	            	else if(gatewayAction.equals(Const.GW_MOTION_ACTION))		//if it's Q(Motion), query Motion
	            	{
	            		MultiThreadRequest mThreadChangeSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
								 Const.MOTION_SENSOR_PORT, Const.ID_SENSOR_MOTION, 1, currtime);
						 mThreadChangeSt.start();
						 mThreadChangeSt.join();
						 String outValue = "";
						 if(GatewayOutputHash.containsKey(currtime))
				        {
				            String motion = GwServerInterfaceImpl.GatewayOutputHash.get(currtime);
				            outValue = Integer.toString(Device_States.get(Const.ID_SENSOR_TEMPERATURE))+ "," +motion;
				        }
				        System.out.println(" currtime motion here" + currtime + "outValue " + outValue);
				               	 //output to the OutputHash file
				        GatewayOutputHash.put(currtime, outValue);
	            	}
	            }
	            else 			//  Q(Temp) and  Q(Motion) both 
	            {
		           	 MultiThreadRequest mThreadTempSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_TEMPERATURE),
							 Const.TEMP_SENSOR_PORT, Const.ID_SENSOR_TEMPERATURE, 1, currtime);
		           	mThreadTempSt.start();
		           	mThreadTempSt.join();
					 
					 MultiThreadRequest mThreadMotionSt = new MultiThreadRequest(false, storeIpAddressSensorsDevices.get(Const.ID_SENSOR_MOTION),
							 Const.MOTION_SENSOR_PORT, Const.ID_SENSOR_MOTION, 1, currtime);
					 mThreadMotionSt.start();
					 mThreadMotionSt.join();
	            	
	            }
            }
            else                         // null no action
            {
            	int v1 = Device_States.get(Const.ID_SENSOR_TEMPERATURE);
            	int v2 =  Device_States.get(Const.ID_SENSOR_MOTION);
            	String outValue = Integer.toString(v1) + "," +Integer.toString(v2);
            	GatewayOutputHash.put(currtime, outValue);
            	GatewayOutputArrayStr.add(currtime);
            }
            
            //
            long intervalTime = (long) (timeEventArray.get(currLoop)-timeEventArray.get(currLoop-1));
            TimeUnit.SECONDS.sleep(intervalTime);
            currLoop++;
			if(currLoop == timeEventArray.size())
			{
				System.out.println("Program finished to exit");
				//print
				 Iterator<Map.Entry<Double, String>> iterator = GatewayOutputHash.entrySet().iterator() ;
			        while(iterator.hasNext()){
			            Map.Entry<Double, String> entryEvent = iterator.next();
			            System.out.println("GatewayOutputHash " + entryEvent.getKey() +" : "+ entryEvent.getValue());
			        }     
			      
			        //write file;
					File fout = new File("test-output-cached.txt");
					FileOutputStream fos = new FileOutputStream(fout); 
					OutputStreamWriter osw = new OutputStreamWriter(fos);
					BufferedWriter bwriter = new BufferedWriter(osw); 
					for (int i = 0; i < GatewayOutputArrayStr.size(); i++) {
						if(GatewayOutputHash.containsKey(GatewayOutputArrayStr.get(i)))
						{
							String	OutputString = GatewayOutputArrayStr.get(i) + ":" + GatewayOutputHash.get(GatewayOutputArrayStr.get(i));
							bwriter.write(OutputString);
							bwriter.newLine();
						}
				}
					bwriter.close(); 
					osw.close();
					fos.close();
				break;
			}
				}
	
	}
	
	
	private static void NotifySensorAction(String ClientId, int port, double time, int device_id)
	{
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(ClientId, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(Const.ID_SENSOR_TEMPERATURE == device_id)
		{	
			 RMIDevicesInterfaces stTempObj = null;
			try {
					stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
			} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			try {
			 stTempObj.NotifySensorEventAction(time);

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if(Const.ID_SENSOR_MOTION == device_id)
		{	
			 RMIDevicesInterfaces stTempObj = null;
			try {
					stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_MOTION);
			} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			int notifyRes = 0;
			try {
                stTempObj.NotifySensorEventAction(time);
				System.out.println(device_id + " motion notify result" + notifyRes);

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void  querySensorTestCase(String ClientId, int port, double time, int device_id)
	{
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(ClientId, port);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(Const.ID_SENSOR_TEMPERATURE == device_id)
		{	
			 RMIDevicesInterfaces stTempObj = null;
			try {
					stTempObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_TEMP);
			} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
			int tempDegree = 0;
			try {
				tempDegree = stTempObj.query_state(time, device_id);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(device_id + "Query result" + tempDegree);
		}
			 
	}

	@Override
	public void FinishCurrentTimeEventToGateway(int finishOne)
			throws RemoteException {
		// TODO Auto-generated method stub
		FinishOneEventSymbol += finishOne;
		System.out.println("FinishOneEventSymbol value " + FinishOneEventSymbol);
		
	}

	@Override
	public void report_state(int device_id, int state, double time)
			throws RemoteException {
		if(Const.ID_SENSOR_MOTION == device_id)
		{
			ReceivedMotionReport = true;
			Device_States.put(device_id, state);
		}
		// TODO Auto-generated method stub
		
	}
	
}
