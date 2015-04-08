package com.GatewayServerImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import com.GatewayInterface.Const;
import com.GatewayInterface.GatewayAllInterfaces;

public class GateWayServer {

	public static void main(String[] args) throws InterruptedException, IOException
	{ 
		if(args.length==0)
		{  
			 System.out.println("lack of input parameter");
			 return;
		}
		GwServerInterfaceImpl GatewayExec  = new GwServerInterfaceImpl();
		if(args.length==1 && args[0].equals(Const.CONFIG_IPS_FILE))
		{
			GatewayExec.runTasks();
		}
		else if(args.length==2 && args[0].equals(Const.CONFIG_IPS_FILE) && args[1].equals(Const.TEST_INPUT_FILE))
		{
			GatewayExec.runTestCases();
		}

	}
}
