import datetime

startDay = datetime.time(8)
endDay = datetime.time(19)
now = datetime.datetime.now().time()
print(now<endDay)