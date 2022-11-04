import firebase_admin
import requests
import json
import csv

uri = "http://datamall2.mytransport.sg/ltaodataservice/"
accountKey = "uMkBgB59Q5G+gqRySJqzog=="
headers={'accountKey':'uMkBgB59Q5G+gqRySJqzog==', 'accept':'application/json'}

queryString = "BusStops"
pageNumber = 1

busStops = []

busStopsList = [10499, 12201, 13021, 14201, 15059, 
                15071, 15109, 16009, 16149, 17241, 
                17289, 17381, 18069, 20271, 21071, 
                21361, 21371, 21379, 22009, 22451, 
                22459, 22481, 22489, 22501, 22509, 
                22511, 22519, 22521, 22529, 24541, 
                24581, 25321, 25331, 25371, 26131, 
                26289, 27011, 27021, 27031, 27041, 
                27051, 27061, 27069, 27071, 27091, 
                27099, 27101, 27109, 27121, 27129, 
                27169, 27171, 27179, 27181, 27189, 
                27199, 27209, 27211, 27219, 27231, 
                27241, 27251, 27261, 27281, 27291, 
                27301, 27309, 27311, 27321, 27329]


for i in range(11): # iterate over 11 pages of busstop information
    queryURL = uri + queryString + "?page=" + str(pageNumber)
    response = requests.get(queryURL, headers=headers)
    responseJson = response.json()
    # loadedJson = json.load(responseJson)
    for eachBusStop in responseJson["value"]:
        if int(eachBusStop["BusStopCode"]) in busStopsList:
            busStops.append(eachBusStop)

    
    pageNumber = pageNumber + 1

print(len(busStops))
data = {'value': str(busStops)}


from firebase_admin import credentials, initialize_app, storage, firestore
# Init firebase with your credentials
cred = credentials.Certificate("fastandfurious-363205-1f57d31b8424.json")
app = initialize_app(cred, {'storageBucket': 'fastandfurious-363205.appspot.com'})
db = firestore.client()

db.collection(u'BusStopsData(70)').document(u'bs').set(data)


print("Upload Complete BusStops(70)")