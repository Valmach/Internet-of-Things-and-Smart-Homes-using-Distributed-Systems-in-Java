# Internet-of-Things-and-Smart-Homes-using-Distributed-Systems-in-Java
Various Sensors Communicate using RMI in java, clock synchronization is achieved

# Multithread Based Model

The application is based on Multithreaded Model. Every single action is categorized as separate thread spawn by the Gateway to handle Blocking calls. 
The total application is implemented on Java Platform using RMI.  
Once the Application is started, all the Components register at Gateway.



#Instructions for running the Code:
We have decentralized our code into various Java Packages where each Package corresponds either to a component (the gateway, a sensor or a device). In order to avoid the complexity and exceptions due to the supporting code being in different packages, In addition to the source code we have also provided the executable jar files for each component above described. We have Submitted Source code to verify code and Jar files to execute.

IP Address Recognition and Allocation:
We are needed to provide only the IP Address of the Gateway in the Configuration file (configips.csv). The Default IP Address in configips.csv is local host.  This IP Address is needed for all the other components. Each Component can figure out the value of its IP Address when initiated and will register at the Gateway. Gateway stores the IP Address and can access the other components when required.

Test-input file:
The Test-input file “test-input.csv” can be used to assign test cases for the application when operated by running the automated scripts instead of user input of test cases.

