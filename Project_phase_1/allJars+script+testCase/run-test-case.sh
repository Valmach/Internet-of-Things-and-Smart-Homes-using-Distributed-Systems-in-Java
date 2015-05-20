#!/bin/bash
# Script to allow the user to allow the user to take input from the file lab2-test-input.csv automatically.
# path to the test-input.csv file is used as an argument to distingush between manual operation or automatic test case evaluation. 

gnome-terminal -x sh -c "java -jar GatewayServer.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar tempeSensor.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar HeaterSmart.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar bulbSmart.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar DoorSensor.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar BackendDatabase.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar motionSensor.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar PresenceSensor.jar configips.csv lab2-test-input.csv; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar UserOperation.jar configips.csv; bash"&

