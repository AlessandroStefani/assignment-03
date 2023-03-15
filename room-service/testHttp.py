import requests

payload = "luci"

r = requests.get('http://localhost/assignment-03/room-dashboard/dashboard.php', params=payload)
print(r.status_code)
t = r.text
print(t)
print(r.url)
'''
tTOd=eval(t)
print(tTOd["tapparelle"])
'''
