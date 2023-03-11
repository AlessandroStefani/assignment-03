import requests

payload = {"id":"5"}

r = requests.post('http://localhost/assignment-03/data.json', data=payload)
print(r.status_code)
print(r.text)
print(r.url)