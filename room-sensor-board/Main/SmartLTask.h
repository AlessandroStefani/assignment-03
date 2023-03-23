#ifndef __SMARTLTASK__
#define __SMARTLTASK__

#include "Led.h"
#include "PIR.h"
#include "LightSensor.h"

class SmartLTask {

  PIR* pir;
  Led* led;
  LightSensor* LS;
  enum { ON, TURNING_OFF, OFF } state;

  public:

  SmartLTask(int pin, PIR* pir, LightSensor* lightSensor);
  void tick();
  void sendApproveMessage();

};

#endif
