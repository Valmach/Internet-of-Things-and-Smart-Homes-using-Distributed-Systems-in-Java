#!/bin/bash
# Script to allow the user to allow the user to take input manually and test for leader election algorithm and
#time server

gnome-terminal -x sh -c "java -jar GatewayServer.jar configips.csv lab3_test; bash"&
sleep 3
gnome-terminal -x sh -c "java -jar GatewayServerReplica.jar configips.csv lab3_test; bash"&
sleep 3
gnome-terminal -x sh -c "java -jar motionSensor.jar configips.csv; bash"&

gnome-terminal -x sh -c "java -jar tempeSensor.jar configips.csv; bash"&

gnome-terminal -x sh -c "java -jar HeaterSmart.jar configips.csv; bash"&

gnome-terminal -x sh -c "java -jar bulbSmart.jar configips.csv; bash"&

gnome-terminal -x sh -c "java -jar DoorSensor.jar configips.csv; bash"&

gnome-terminal -x sh -c "java -jar BackendDatabase.jar configips.csv; bash"&


