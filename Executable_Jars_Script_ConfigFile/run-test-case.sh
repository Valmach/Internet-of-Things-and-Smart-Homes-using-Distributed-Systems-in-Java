#!/bin/bash
# Script to allow the user to allow the user to take input from the file test-input.csv automatically.
# path to the test-input.csv file is used as an argument to distingush between manual operation or automatic test case evaluation. 

gnome-terminal -x sh -c "java -jar GatewayServer.jar configips.csv test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar motionSensor.jar configips.csv test-input.csv; bash"&
gnome-terminal -x sh -c "java -jar tempeSensor.jar configips.csv test-input.csv; bash"&
