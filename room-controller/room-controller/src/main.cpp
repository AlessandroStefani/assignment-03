#include "MsgServiceBT.h"
#include "MsgServiceSerial.h"
#include "Led.h"
#include <Servo.h>

#define RX_PIN 2
#define TX_PIN 3
#define SERVO_PIN 9
#define LED_PIN 13

void remoteActionBT();
void remoteActionSerial();
/*
 *  BT module connection:
 *  - RX is pin 2 => to be connected to TXD of BT module
 *  - TX is pin 3 => to be connected to RXD of BT module
 *
 */
MsgServiceBT msgServiceBT(RX_PIN, TX_PIN);
Led led(LED_PIN);
Servo blinds;

String msgBT = "";
String msgSerial = "";
String response = "";
/*
 * 0 - 100 range
 * 0 su, 100 giù
 */
int blindsStatus = 0;
/**
 * 0 = OFF
 * 1 = ON
 */
int lightsStatus = 0;

void setup()
{
  blinds.attach(SERVO_PIN);
  msgServiceBT.init();
  MsgService.init();
  Serial.begin(9600);
  while (!Serial)
  {
  }
  Serial.println("ready to go.");
}

void loop()
{
  if (msgServiceBT.isMsgAvailable())
  {
    remoteActionBT();
  }
  if (MsgService.isMsgAvailable())
  {
    remoteActionSerial();
  }
}

void remoteActionBT()
{
  Msg *msg = msgServiceBT.receiveMsg();
  msgBT = msg->getContent();
  if (msgBT == "on") // accensione luci
  {
    led.on();
    lightsStatus = 1;
    response = "Luci Accese";
    msgServiceBT.sendMsg(Msg(response));
    MsgService.sendMsg(response); // comunica a room-service il nuovo stato delle luci
  }
  else if (msgBT == "off") // spegnimento luci
  {
    led.off();
    lightsStatus = 0;
    response = "Luci Spente";
    msgServiceBT.sendMsg(Msg(response));
    MsgService.sendMsg(response); // comunica a room-service il nuovo stato delle luci
  }
  else if (msgBT.startsWith("servo:")) // movimento tapparelle
  {
    blindsStatus = msgBT.substring(6).toInt();
    if (blindsStatus >= 0 && blindsStatus <= 100)
    {
      blinds.write(map(blindsStatus, 0, 100, 0, 180));
      response = "Livello Tapparelle:";
      response += blindsStatus;
      msgServiceBT.sendMsg(Msg(response + "%"));
      MsgService.sendMsg(response); // comunica a room-service il nuovo stato delle tapparelle
    }
    else // comando tapparelle fuori range
    {
      msgServiceBT.sendMsg(Msg("inserire un numero da 0 a 100"));
    }
  }
  else // comando sconosciuto
  {
    msgServiceBT.sendMsg(Msg("Comando Sconosciuto: " + msgBT));
  }
  delete msg;
}

void remoteActionSerial()
{
  Msg *msg = MsgService.receiveMsg();
  msgSerial = msg->getContent();
  if (msgSerial == "on")
  {
    led.on();
    lightsStatus = 1;
    response = "Luci Accese";
    MsgService.sendMsg(response); // comunica a room-service il nuovo stato delle luci
  }
  else if (msgSerial.equals("off")) // spegnimento luci
  {
    led.off();
    lightsStatus = 0;
    response = "Luci Spente";
    MsgService.sendMsg(response); // comunica a room-service il nuovo stato delle luci
  }
  else if (msgSerial.startsWith("servo:")) // movimento tapparelle
  {
    blindsStatus = msgSerial.substring(6).toInt();
    if (blindsStatus >= 0 && blindsStatus <= 100)
    {
      blinds.write(map(blindsStatus, 0, 100, 0, 180));
      response = "Livello Tapparelle:";
      response += blindsStatus;
      MsgService.sendMsg(response); // comunica a room-service il nuovo stato delle tapparelle
    }
    else // comando tapparelle fuori range
    {
      MsgService.sendMsg("Valore fuori range [0-100]");
    }
  }
  else if (msgSerial == "night")
  {
    blindsStatus = 100;
    blinds.write(170); // sarebbe 180 ma il servo fa rumori strani quindi lo tengo a 170
    MsgService.sendMsg("Livello Tapparelle:100");
    lightsStatus = 0;
    led.off();
    MsgService.sendMsg("Luci Spente");
  }
  else // comando sconosciuto
  {
    MsgService.sendMsg("Comando Sconosciuto: " + msgSerial);
  }
  delete msg;
}