#ifndef __LED__
#define __LED__

class Led
{
private:
    int pin;
public:
    Led(int pin);
    void on();
    void off();
};

#endif