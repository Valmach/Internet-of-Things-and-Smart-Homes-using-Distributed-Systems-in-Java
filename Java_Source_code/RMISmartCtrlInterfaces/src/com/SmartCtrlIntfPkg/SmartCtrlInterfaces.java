/**
 * Interfaces to be used by the Smart Devices to create a Registry and Allow the GAteway to change their State.
 */
package com.SmartCtrlIntfPkg;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SmartCtrlInterfaces extends Remote {
	public boolean change_state(int device_id, int state) throws RemoteException;
}
