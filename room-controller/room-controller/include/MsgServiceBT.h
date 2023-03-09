#ifndef __MSGSERVICEBT__
#define __MSGSERVICEBT__

#include "Msg.h"
#include "SoftwareSerial.h"

class MsgServiceBT
{

public:
  MsgServiceBT(int rxPin, int txPin);
  void init();
  bool isMsgAvailable();
  Msg *receiveMsg();
  bool sendMsg(Msg msg);

private:
  String content;
  Msg *availableMsg;
  SoftwareSerial *channel;
};

#endif