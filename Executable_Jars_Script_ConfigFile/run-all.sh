#!/bin/bash
# Script to allow the user to input in 6 user terminals
# "manual" is used as an argument to distingush between manual operation or automatic test case evaluation. 

gnome-terminal -x sh -c "java -jar GatewayServer.jar configips.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar motionSensor.jar configips.csv; bash"&
gnome-terminal -x sh -c "java -jar tempeSensor.jar configips.csv; bash"&
gnome-terminal -x sh -c "java -jar bulbSmart.jar configips.csv ; bash"&
gnome-terminal -x sh -c "java -jar HeaterSmart.jar configips.csv; bash"&
gnome-terminal -x sh -c "java -jar UserOperation.jar configips.csv; bash"&
