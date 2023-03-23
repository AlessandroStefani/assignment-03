#ifndef __PIR__
#define __PIR__

#define CALIBRATION_TIME_SEC 10
#define TIME_1 2000 // max time in which nobody has been detected (in milliseconds)

class PIR {

  double timeNotDetected;
  double ts;

  int pin;
  bool detectedStatus;
  enum PIRState { DETECTED, NOT_DETECTED } state;

  private:

  void calibratePIR();

  public:

  PIR(int pin);

  void changeState();
  bool isSomeoneDetected();
  bool isSomeoneDetectedT1();
  enum PIRState getState();
};

#endif
