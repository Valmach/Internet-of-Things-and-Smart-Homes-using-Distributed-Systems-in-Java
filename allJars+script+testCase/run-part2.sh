#!/bin/bash
# Script to allow the user to manually give input and check for user functioning of logical clock.


gnome-terminal -x sh -c "java -jar GatewayServer.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar tempeSensor.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar BackendDatabase.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar HeaterSmart.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar bulbSmart.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar DoorSensor.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar motionSensor.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar PresenceSensor.jar configips.csv part2; bash"&
sleep 1
gnome-terminal -x sh -c "java -jar UserOperation.jar configips.csv part2; bash"&

