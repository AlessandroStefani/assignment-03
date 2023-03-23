#ifndef __LIGHTSENSOR__
#define __LIGHTSENSOR__

class LightSensor {

  int pin;
  double valueInVolt;

  public:

  LightSensor(int pin);

  double getIntensity();

};

#endif
