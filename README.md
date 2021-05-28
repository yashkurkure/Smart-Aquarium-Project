# Smart-Aquarium-Project

The goal of this project is to make aquarium monitoring and maintenance easier. Using the Arduino UNO, tasks like light control, testing water quality, temperature control, fish feeding, etc., can be automated. This project has 4 main functionalities: timed fish feeding using a real-time clock, temperature monitoring, water level monitoring, and light control. There is a remote-control aspect over Wi-Fi, where the Wi-Fi module acts as a server and it communicates with the Arduino over serial and mobile device over local a loacal Wi-Fi network. You can control and monitor the Arduino via the android app. The app will allow you to control lights, feed fish, and set an alarm for feeding fish, monitor real-time water temperature and level.

See this for the final results and usage: https://youtu.be/kJp_m0JY0r8


## Required Components
-
-
-
-
-
.
.
.
.

## Some diargrams

![Communication and Network Diagram](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/other/communication_diargram.png)

The above image shows how the individual components communicatre with wach other. The arduino is the one controlling the I/O devices realted to the aquarium. The arduino communicates with the wifi module ESP8266-01. The wifi module is connected to a local network through which it can respond to http get requests. Almost all request that it gets are forwarded to the arduino over serial, to which the arduino reponds and then the wifi module sends the response to the client.

Below is the screenshot of the app this project uses. You can find the code in the [Android_Code](https://github.com/yashkurkure/Smart-Aquarium-Project/tree/main/Arduino_Code) folder and the .apk file in [APK_file](https://github.com/yashkurkure/Smart-Aquarium-Project/tree/main/APK_file) folder.

![App ScreenShot](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/other/app.png)

## Building the project

Below there are multiple sections that you can read through, although
going in increasing numerical order is okay, it is advised you to go with
this order mentioned below for better understanding.
Follow the order:

- Section 1.1 [Refer ESP8266-01 Pinout]
- Section 2.1 [Set WiFi Module in program mode]
- Section 3 [Program the WiFi Module]
- Section2.2 [Connect the WiFi Module to the Arduino in working
mode]

At this point you are done with uploading code to the ESP8266-01. Now
for the Arduino.

- Section 4 [Upload code to arduino]
- Section 1.1 [Refer Stepper IC and LCD Pinout]
- Section 1 [Wire all the components]
- Section 1.2 [While wiring refer these diagrams]

At this point you have uploaded code to the Arduino, ESP8266-01 and
have the wirings done.
- Now install the APP [link to APK at end] on your android device
and follow Section 5.
- Next, look at the How to use section.

### Section 1: Wiring the Setup [Arduino Side]

| **Arduino Pin** | **To where** | **######** | **Arduino Pin** | **To where** |
| --- | --- | --- | --- | --- |
| **A0** | **Water level analog read (S - pin)<b>[Gets analog data]** | **######** | **5** | **LCD pin 11** |
| **A1** | **Light Relay Pin (Digital Write)<b>[Controls the lights]**| **######** |**6**| **LCD pin 10** |
| **A2** | **Water level power pin<b>(VCC - pin) [TURN ON/OFF]** | **######** | **7** | **LCD pin 5** |
| **A3** | **Temperature Sensor Digital Pin<b>[Read temperature]** | **######** | **8** | **LCD pin 3** |
| **A4** | **None** | **######** | **9** | **Stepper IC pin 15** |
| **A5** | **None** | **######** | **10** | **Stepper IC pin 10** |
| **0 [RX]** | **TX of ESP8266-01** | **######** | **11** | **Stepper IC pin 7** |
| **1 [TX]** | **RX of ESP8266-01** | **######** | **12** | **StepperIC pin 2** |
| **2** | **Button Used toggle LCD <b>between IP and Normal mode** | **######** | **13** | **None** |
| **3** | **LCD pin 13** | **######** | **SDA** | **SDA of RTC** |
| **4** | **LCD pin 12** | **######** | **SCL** | **SCL of RTC** |

- For Pin Numbering of LCD and Stepper IC look at Section 1.1
- For Diagrams look at Section 1.2


### Section 1.1: LCD, ESP8266-01 and Stepper Pin Numbering

- **Stepper IC:**
    - ![Stepper IC](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/pinouts/L293D_pins.png)

    - Use Section 1 to connect arduino to IC.
    - Use stepper diagram in Section1.2 for connecting IC to
stepper.

- **LCD:**
    - ![LCD Pinout](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/pinouts/lcd_pins.png)
    - For a more detailed view of lcd check section 1.2.

- **ESP8266-01**
    - ![]()

-  Keep these in mind as future sections would be using this terminology.

### Section 1.2: Diagrams

This sections has all connections diagrams for each arduino component.

- LCD ![LCD connections](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/connections/lcd_conn.png)
- RTC ![RTC connections](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/connections/rtc_conn.png)
- Lights ![Light connections](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/connections/light_conn.png)
- Temperature Sensor ![Temp. sensor connections](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/connections/temp_conn.png)
- Stepper motor ![Stepper connections](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/connections/stepper_conn.png)
- Water level sensor ![Water level sensor connections](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/connections/waterlevel_conn.png)


### Section 2.1: ESP8266 - 01 [Program Mode]

The program mode of the esp8266-01 is when the GPIO-0 is connected to ground and all other GPIO pins are disconnected. This mode of the WiFi module is used to upload code to it. We can do this by using the Arduino as a dummy and then using the Rx and Tx Pins to make a connection between the PC and the Wifi Module [More on Flashing the Software in next sections]. In this section let us see what connections we need for the program mode.

![Program mode ESP8266-01](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/esp8266/esp8266_program_mode.png)

Notice the highlighted areas in the above image.

- The RESET pin of the arduino is grounded - this makes the arduino act a dummy.
- RX Arduino -> ESP8266-01 RX pin
- TX Arduino -> ESP8266-01 TX pin
- GPIO 0 of ESP8266-01 is grounded. [Enables programming mode]

The setup is not for communication between the module and the Arduino, we are uploading code to the ESP, hence we extend the serial channel by connecting rx to rx and tx to tx.


### Section 2.2: ESP8266 - 01 [Working Mode]

Once we have the ESP8266-01 programmed use this section to make the appropriate connections to enable the WiFi module to talk with the arduino. The connections go as follows:

![Working mode ESP8266-01](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/esp8266/esp8266_working_mode.png)

The Difference between this and program mode:

- GPIO 0 is not grounded
- RX Arduino -> TX ESP8266-01
- TX Arduino -> RX ESP8266-01
- GPIO 2 -> LED [Used to indicate status of wifi module]


### Section 3: Flashing code onto the ESP8266-01

This is an important section, because we had to do a lot of online digging to get this working. The program mode section 2.1 and this section go hand in hand when it comes to uploading code to the esp8266-01.

Step 1: Set up the EPS8266-01 in program mode as shown in Section 2.1.

Step 2: Connect the Arduino to your computer, and open Arduino IDE.

Step 3: Open preferences and add the ESP board manager.

- Use Link: [http://arduino.esp8266.com/stable/package\_esp8266com\_index.json](http://arduino.esp8266.com/stable/package_esp8266com_index.json)

- ![Step 3](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/esp8266/setup_step3.png)

Step 4: Go to tools and selec the Boards Manager

- ![Step 4](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/esp8266/setup_step4.png)

Step 5: Install this open source package for ESP8266-01

- ![Step 5](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/esp8266/setup_step5.png)

Step 6: Choose the **Generic ESP8266 Modul** e Board from the list of available new boards.

- ![Step 6](https://github.com/yashkurkure/Smart-Aquarium-Project/blob/main/readme_img/esp8266/setup_step6.png)

(Now make sure your arduino is connected and all connections from Section 2.1 are propper )

Step 7: Flash the code from the file **server.ino** similar to how you do it for any other Arduino.

_Important Resource to check out:_ [_Installing ESP8266 in Arduino IDE (Windows, Mac OS X, Linux) | Random Nerd Tutorials_](https://randomnerdtutorials.com/how-to-install-esp8266-board-arduino-ide/)

### Section 4: Flash Software on the Arduino.

This is the easiest section, and majority of the audience are familiar with doing this.

Take the **smart_aquarium.ino** code and flash it to the arduino using the Arduino IDE.

### Section 5: Making the Final Connections.

Assuming that you have completed all the sections prior to this, connect the ESP8266-01 to the Arduino in working mode [Section 2.2], and make sure that the connections from Section 1 are made.

We have already provided the Android APK and the code. Next, once all connections are made, power on the arduino and then boot up the APP. More on how to use the setup in the guide ahead.