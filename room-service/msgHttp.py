import requests

#data è un dizionario stile {"tapparelle":"23"} o {"luci":"on"}
def post(data):
    r = requests.post('http://localhost/assignment-03/room-dashboard/dashboard.php', data=data)
    return r.status_code

#param dovrebbe essere luci, tapparelle, storico
def get(param):
    r = requests.get('http://localhost/assignment-03/room-dashboard/dashboard.php', params=param)
    return r.text
