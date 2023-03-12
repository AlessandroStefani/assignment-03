import requests
import http.client

BODY = "***filecontents***"
conn = http.client.HTTPConnection("localhost", 8080)
conn.request("PUT", "/file", BODY)
response = conn.getresponse()
print(response.status, response.reason)

'''
payload = {"id":"5"}

r = requests.put('localhost', data=payload)
print(r.status_code)
print(r.text)
print(r.url)
'''