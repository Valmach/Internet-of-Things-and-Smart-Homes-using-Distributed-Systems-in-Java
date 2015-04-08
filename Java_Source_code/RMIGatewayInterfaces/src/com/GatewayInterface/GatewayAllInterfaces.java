/**
 * Interfaces implemented by Gateway which can be accessed by Sensors or Devices
 */
package com.GatewayInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface GatewayAllInterfaces extends Remote {
	
  public  void register(int type, String name,String ip) throws RemoteException;
  public  void report_state(int device_id,  int state) throws RemoteException;
  public  void change_mode(String mode) throws RemoteException;
  public void report_state(int device_id, int state, double time) throws RemoteException;
  public  void FinishCurrentTimeEventToGateway(int finishOne) throws RemoteException;
}
