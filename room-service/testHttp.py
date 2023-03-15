import requests

payload = {"luci":"off"}

r = requests.post('http://localhost/assignment-03/room-dashboard/dashboard.php', data=payload)
print(r.status_code)
t = r.text
print(t)
print(r.url)
tTOd=eval(t)
print(tTOd["tapparelle"])
