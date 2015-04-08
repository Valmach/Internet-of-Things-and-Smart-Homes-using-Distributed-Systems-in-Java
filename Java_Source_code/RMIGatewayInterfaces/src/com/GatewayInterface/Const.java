/**
 * This class has the information of All the constant values used through out the application.
 */
package com.GatewayInterface;

public class Const {
    
    public static final String USER_MODE_AWAY="AWAY";
    public static final String USER_MODE_HOME="HOME";
    
    public static final  int TASK1_TEMP_LOW_THRESHOLD = 1;
    public static final  int TASK1_TEMP_HIGH_THRESHOLD = 2;
    
    public static final int TYPE_SENSOR = 0;
    public static final int TYPE_SMART_DEVICE =1;
    
    public static final String CONFIG_IPS_FILE = "configips.csv";
    public  static final String  NAME_SENSOR_TEMPERATURE = "TEMPERATURE";
    public  static final String  NAME_SENSOR_MOTION ="MOTION";
    public static final String NAME_DEVICE_BULB = "BULB";
    public  static final String NAME_DEVICE_OUTLET = "OUTLET";
    
    public static final int ID_SENSOR_TEMPERATURE = 1;
    public static final int ID_SENSOR_MOTION = 2;
    public static final int ID_DEVICE_BULB = 3;
    public static final int ID_DEVICE_OUTLET = 4;
    
    public static final int NO_MOTION = 0;
    public static final int  YES_MOTION_ = 1;
    
    public static final int ON = 1;
    public static final int OFF = 0;
    
    public final static String STR_LOOKUP_TEMP="tempeSensorImpl";
    public final static String STR_LOOKUP_HEATER = "HeaterImpl";
    public final static String STR_LOOKUP_SMART_BULB = "bulbSmart";
    public final static String STR_LOOKUP_GATEWAY = "GwServerInterfaceImpl";
    public final static String STR_LOOKUP_MOTION = "motionSensorImpl";
    public final static String STR_LOOKUP_USER_OPERATION = "UserOperation";
    
    public final static int GATEWAY_PORT = 2228;
    public final static int SENSOR_PORT = 2225;
    public final static int HEATER_PORT = 1099;
    public final static int TEMP_SENSOR_PORT =1100;
    public final static int MOTION_SENSOR_PORT =1108;
    public final static int SMART_BULB_SENSOR_PORT =1095;
    public final static int USER_OPERATION_PORT=1221;
    
    public  static String GATEWAY_SERVER_IP = "localhost";
    public  static String CLIENT_SENSOR_TMPERATURE_IP = "localhost";
    public  static String CLIENT_SMART_HEATER_IP = "localhost";
    public  static String CLIENT_SENSOR_MOTION_IP = "localhost";
    public  static String CLIENT_SMART_BULB_IP="localhost";
    public  static String USER_OPERATION_IP="localhost";
    
    public final static String TEST_INPUT_FILE = "test-input.csv";
    
    public final static String GW_TEMP_ACTION = "Q(Temp)";
    public final static String GW_MOTION_ACTION = "Q(Motion)";

}