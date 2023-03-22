#ifndef __LED__
#define __LED__

class Led {

  int pin;
  bool statusL;

  public:
  Led(int pin, int mode);
  void turnOn();
  void turnOff();
  bool statusLed();

};

#endif
