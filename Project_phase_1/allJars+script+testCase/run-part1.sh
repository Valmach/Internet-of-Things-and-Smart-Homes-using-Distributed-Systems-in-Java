#!/bin/bash
# Script to allow the user to allow the user to take input manually and test for leader election algorithm and
#time server

gnome-terminal -x sh -c "java -jar GatewayServer.jar configips.csv part1; bash"&
sleep 3
gnome-terminal -x sh -c "java -jar tempeSensor.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar HeaterSmart.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar bulbSmart.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar DoorSensor.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar BackendDatabase.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar motionSensor.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar PresenceSensor.jar configips.csv part1; bash"&
gnome-terminal -x sh -c "java -jar UserOperation.jar configips.csv part1; bash"&

