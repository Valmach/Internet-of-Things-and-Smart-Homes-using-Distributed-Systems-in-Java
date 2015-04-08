package com.GatewayServerImpl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.sql.Timestamp;

import com.DevicesInterfaces.RMIDevicesInterfaces;
import com.GatewayInterface.Const;
import com.SmartCtrlIntfPkg.SmartCtrlInterfaces;

public class MultiThreadRequest extends Thread {
	
	private int device_id;
	private int port;
	private String ClientId;      //locate device's id
	private boolean queryChangeFlag;		//=false is query, true is change state
	private int state_changed;             //for query_state, state_changed is as TasksOrTestcaseFunction, TasksOrTestcaseFunction = 1 for TestCase;
	private double currenTimeStamp;
	
	public MultiThreadRequest(boolean queryChangeFlag, String clientId, int port, int device_id, int state_changed, double time)
	{
		this.queryChangeFlag = queryChangeFlag;
		this.port = port;
		this.ClientId = clientId;
		this.device_id = device_id;
		this.state_changed = state_changed;
		this.currenTimeStamp = time;
	}
	
	public void run()
    {
		System.out.println("MultiThreadRequest begin here");
		Registry regs = null;
		try {
				regs = LocateRegistry.getRegistry(ClientId, port);
			//	regs = LocateRegistry.getRegistry(ClientId);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Client Implemententation's class namecliImpl
		 switch(device_id)
		 {
			 case Const.ID_DEVICE_OUTLET: 				//change state
			 {
				 SmartCtrlInterfaces stHeaterObj = null;
					try {
						stHeaterObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_HEATER);
					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boolean result = false;
					try {
							result = stHeaterObj.change_state(device_id, state_changed);
					} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
					}
					if(result)
					{
						System.out.println(device_id + "'s state is changed successfully");
					}
					System.out.println("MultiThreadRequest change_state enter here");
					break;
			 	}
			 case Const.ID_DEVICE_BULB: 			//change state
			 {
				 SmartCtrlInterfaces stBulbObj = null;
					try {
						stBulbObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_SMART_BULB);
					} catch (RemoteException | NotBoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					boolean result = false;
					try {
							result = stBulbObj.change_state(device_id, state_changed);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(result)
					{
						System.out.println(device_id + "'s state is changed successfully");
					}
				//	System.out.println("MultiThreadRequest change_state enter here");
				 break;
			 }
			 case Const.ID_SENSOR_TEMPERATURE: 			//query state
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
					tempDegree = stTempObj.query_state(device_id);
					System.out.println(device_id + "Query result" + tempDegree);
					GwServerInterfaceImpl.Device_States.put(device_id, tempDegree);
					if(0 == state_changed)			//for Task1
					{
						NotifiyHeaterOperation(tempDegree);
					}
					else if(1 == state_changed)    //for TA's Testcase Function only
					{
						String outValue = Integer.toString(tempDegree);
		               if(GwServerInterfaceImpl.GatewayOutputHash.containsKey(currenTimeStamp))
		               {
		            	   String motion = GwServerInterfaceImpl.GatewayOutputHash.get(currenTimeStamp);
		            	   outValue = outValue.concat(","+motion);
		               }
		               System.out.println(" currenTimeStamp Temp here" + currenTimeStamp + "outValue " + outValue);
		               	 //output to the OutputHash file
		                GwServerInterfaceImpl.GatewayOutputHash.put(currenTimeStamp, outValue);
		                GwServerInterfaceImpl.GatewayOutputArrayStr.add(currenTimeStamp);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 break;
			 }
			 case Const.ID_SENSOR_MOTION: 			//query state
			 {
				 RMIDevicesInterfaces stMotionObj = null;
				try {
					stMotionObj = (RMIDevicesInterfaces)regs.lookup(Const.STR_LOOKUP_MOTION);
				} catch (RemoteException | NotBoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int result = 0;
				try {
						result = stMotionObj.query_state(device_id);
						GwServerInterfaceImpl.Device_States.put(device_id, result);
						if(0 == state_changed)						//for Task2
						{
						 if(result==Const.ON && GwServerInterfaceImpl.userMode.equals(Const.USER_MODE_HOME)){
								TimerforMotion.main();
							 }
							 if(result==Const.ON && GwServerInterfaceImpl.userMode.equals(Const.USER_MODE_AWAY)){
								SendNotification.main();
							 }
						}
						else if(1 == state_changed)   	//for TA's Testcase Function only
						{
							String motionValue = Integer.toString(result);		//motion no or yes
							String outValue = motionValue;
					        if(GwServerInterfaceImpl.GatewayOutputHash.containsKey(currenTimeStamp))
					        {
					            String temp = GwServerInterfaceImpl.GatewayOutputHash.get(currenTimeStamp);
					            outValue = temp.concat("," + motionValue);
					        }
					        System.out.println(" currenTimeStamp motion here" + currenTimeStamp + "outValue " + outValue);
					          //output to the OutputHash file
					        GwServerInterfaceImpl.GatewayOutputHash.put(currenTimeStamp, outValue);
			                GwServerInterfaceImpl.GatewayOutputArrayStr.add(currenTimeStamp);
						}
						System.out.println(device_id + "Query motion result " + result);
						
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 break;
			 }
			 default:break;
    	}
    }
	
	public static void NotifiyHeaterOperation(int tempDegree)
	{	
			Registry regs = null;
				try {
					regs = LocateRegistry.getRegistry(Const.CLIENT_SMART_HEATER_IP, Const.HEATER_PORT);
				//	regs = LocateRegistry.getRegistry(ClientId);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
				SmartCtrlInterfaces stHeaterObj = null;
			try {
				stHeaterObj = (SmartCtrlInterfaces)regs.lookup(Const.STR_LOOKUP_HEATER);
				
			} catch (RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		if(tempDegree < Const.TASK1_TEMP_LOW_THRESHOLD)
		{
				//turn on the heater 
				boolean notifyResult = false;
				try {
					notifyResult = stHeaterObj.change_state(Const.ID_DEVICE_OUTLET, Const.ON);
					if(notifyResult)
					{
						System.out.println("heater is ON now");
						//don't forget to update state
						GwServerInterfaceImpl.Device_States.put(Const.ID_DEVICE_OUTLET, Const.ON);
					}
			} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		else if(tempDegree > Const.TASK1_TEMP_HIGH_THRESHOLD)
		{
				//turn off the heater 
				boolean notifyResult = false;
				try {
					notifyResult = stHeaterObj.change_state(Const.ID_DEVICE_OUTLET, Const.OFF);
					if(notifyResult)
					{
						System.out.println("heater is OFF now");
						//don't forget to update state
						GwServerInterfaceImpl.Device_States.put(Const.ID_DEVICE_OUTLET, Const.OFF);
					}
			} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		}
		return;
	}

	
}
