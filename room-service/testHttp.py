import requests

payload = {"tapparelle":"5"}

r = requests.post('http://localhost/assignment-03/room-dashboard/dashboard.php', data=payload)
print(r.status_code)
print(r.text)
print(r.url)
