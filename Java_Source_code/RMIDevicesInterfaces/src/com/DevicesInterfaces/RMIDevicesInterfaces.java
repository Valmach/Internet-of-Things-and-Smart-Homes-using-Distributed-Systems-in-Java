/**
 * Interfaces implemented by Devices which can be accessed by Gateway
 */
package com.DevicesInterfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RMIDevicesInterfaces extends Remote{
	public int query_state(int device_id) throws RemoteException;
	public int query_state(double time, int device_id) throws RemoteException;
	public void NotifySensorEventAction(double time) throws RemoteException;

}

