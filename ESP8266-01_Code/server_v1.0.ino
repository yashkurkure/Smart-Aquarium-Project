/*
 * This is the code for the WiFi Module 
 *  
 * The job of the Wifi Module (ESP8266-01) is to receive HTTP requests over the local Wifi network
 *  and then then forward that request to the Arduino over the serial connection. The Arduino then 
 *  processes the request and generates a response to the request(if required) and send it to
 *  the Wi-Fi module over serial. After which the Wi-Fi module responds back to the requester over 
 *  the local network.
 * 
 * 
 * Before uploading this code, ensure that you assgin variables "ssid" and "password" variables
 *  with your wifi credentials. 
 *  [There is no framework to assign a wifi network via a user interface]
 *  [Everytime you change your WiFi network, you will have to change the 
 *    mentioned variables, recompile and reupload the code.]
 */


#include <ESP8266WiFi.h>
 

//GPIO 
const int ledPin = 2; // GPIO2 of ESP8266 - LED used to indicate the status of the module


//WIFI
WiFiServer server(80); //Initialize a server
const char* ssid = "MY_WIFI";//type your ssid
const char* password = "0123456789";//type your password

//WIFI Book keeping
String currlocalIP; //stores the local IP of the wifi module (assignned by the router)
WiFiClient client; //To store a connection to the client




//String rec;//remove this if not used
 


/**
 * setup() function
 * Called once when the WiFi module boots up
 * 
*/
void setup() {

  //LED: Initialize LED.
  pinMode(ledPin, OUTPUT);


  //LED: Turn LED ON when below initializing process begins
  digitalWrite(ledPin, HIGH);

  //Begin serial for communication with arduino
  Serial.begin(9600);
  delay(500);

  //Begin WiFi connection
  WiFi.begin(ssid, password);
  delay(500);
  while (WiFi.status() != WL_CONNECTED)//Connect to Wifi
  {
    delay(500); //wait till the WiFi is connected.
  }
  delay(500);
  
  //Start server on ESP8266
  server.begin();

  //LED: Turn LED OFF when initializing is done.
  digitalWrite(ledPin, LOW);

  Serial.flush();
}


/**
 * loop() function
 * This is function is called continuously by the arduino
 * Statements in here get executed like they are in an inifinite loop; 
*/
void loop() 
{

  //Check if there is a client request
  String clientRequest = onClientRequest();
  if(clientRequest != "N/A")
  {
    //send response to client
    String cleanClientRequest = cleanUpRequestString(clientRequest);
    String response = generateClientResponse(cleanClientRequest);
    sendToClient(response);
    return;
  }

  //Check if there is a response from the arduino
  String arduinoRequest = onArduinoRequest();
  if(arduinoRequest != "N/A")
  {
    
    //send response to arduino
    if(arduinoRequest.indexOf("getip") != -1)
    sendToArduino( "!"+  WiFi.localIP().toString() + "!");
    
    return;
  }
}


/**
 * cleanUpRequestString()
 * 
 * This function cleans up any noise from the string.
 * The information we need is enclosed in "!" symbols.
 * 
 * So a message from the the client to turn OFF the lights looks like !L:OFF!
 *  and to turn ON the client will send !L:ON!"
 * 
 * This function cleans up anything recieved before and after the opening and closing "!"
*/
String cleanUpRequestString(String request)
{
  String clean = "";
  boolean append = false;
  
  for(int i = 0; i < request.length(); i ++)
  {
    if(request.charAt(i) == '!' && append == false)
    {
      append = true;
      }

    else if(request.charAt(i) == '!' && append == true)
     {
      append = false;
      clean = clean + request.charAt(i);
      break;
      }

     if(append == true)
     {
      clean = clean + request.charAt(i);
      }
    
    }
    return clean;
  }


/**
 * generateClientResponse()
 * 
 * This client takes in a client request string and generates 
 * the response forthe client
*/
String generateClientResponse(String request)
{
  //Since the arduino is cconnneted to the I/O devices of the system
  //the client's response is always forwarded to the arduino, and then the 
  //reponse from the arduino is what this function actualy returns.
  String response = getArduinoResponseToRequest(request);
  return response;
}



/**
 * generateArduinoResponse()
 * 
 * This function is called when the arduino is the requestor for some
 * information from the Wi-Fi mmodule.
 * 
 * Information such as the local IP on the WiFi
*/
String generateArduinoResponse(String request)
{
  //If the request is to get the IP 
  if(request.indexOf("getip") != -1)
  {
    return WiFi.localIP().toString();
  }

  else return  "N/A";
}



/**
 * onClientRequest()
 * 
 * -Checks if there is a client
 *    -If there is, then we get that request and return it.
 *    -If there is no client, we simply return "N/A" as a string.
*/
String onClientRequest()
{
  //Get client instance
  client = server.available();
  
  //Check if the client is there
  if (!client) {
    client.flush();
    return "N/A"; //if not return "N/A" indicating nno client
  }

  //If client present, wait tail they send datat
  while(!client.available()){
    delay(1);
  }

  //Read the data
  String clientRequest = client.readStringUntil('\r');
  client.flush();

  //Return the data as a string.
  return clientRequest;
 }


/**
 * onArduinoRequest()
 * 
 * -Checks if the arduino is trying to send data via the serial connection.
 * `    -If yes, we read it and return it.
 *      -If no, we return "N/A" as a string.
*/
String onArduinoRequest()
{
  //Check if the arduino is trying to send data
  if(!Serial.available())
  {
    Serial.flush();
    return "N/A"; //If no, then return "N/A" indicating there is not data.
  }

  //Get the data from the serail connection
  String arduinoRequest = (String) Serial.readString();
  Serial.flush();

  //Return the data as a string
  return arduinoRequest;
 }

/**
 * sendToAdrduino()
 * 
 * Writes data to the serial connection of the arduino.
*/
void sendToArduino(String out)
{
  Serial.println(out);
  }

/**
 * sendToClient()
 * 
 * Sends data to the client.
 * This is the reponse to the http get request that the clients 
 * make (From the android app)
*/
void sendToClient(String out)
{
  client.println("HTTP/1.1 200 OK");
  client.println("Content-Type: text");
  client.println(""); //  do not forget this one
  client.println("");
  client.print(out);
  
  }

/**
 * getArduinoResponseToRequest()
 * 
 * This function is called when the client request if for the arduino.
 * 
*/
String getArduinoResponseToRequest(String request)
{
  //Print the request to the arduino
  Serial.println(request);

  //Wait for the arduino to be ready to send a response
  while(!Serial.available()){
    digitalWrite(ledPin, HIGH); //while we are waiting, turn the led ON, to indicate this state.
    }

  //get the response fromt eh arduino
  String arduinoResponse = Serial.readString();
  digitalWrite(ledPin, LOW); //turn the led off, to indicate we have got the response
  
  return arduinoResponse; //return the response string
}
