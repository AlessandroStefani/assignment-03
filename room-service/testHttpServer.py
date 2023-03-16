import http.server

s = "Livello Tapparelle:123"
lv = s.split(":")[1]
print (lv)

dashMsg = {"tapparelle":"31"}

serCom = "servo:" + dashMsg["tapparelle"]

print(serCom)