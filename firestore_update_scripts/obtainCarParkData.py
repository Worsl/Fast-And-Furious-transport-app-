import firebase_admin
import requests
import json
import csv

uri = "http://datamall2.mytransport.sg/ltaodataservice/"
accountKey = "uMkBgB59Q5G+gqRySJqzog=="
headers={'accountKey':'uMkBgB59Q5G+gqRySJqzog==', 'accept':'application/json'}

queryString = "CarParkAvailabilityv2"
pageNumber = 1

 # There are 5 pages of carparks (500 entries each)


carParks = []

for i in range(11): # iterate over 11 pages of busstop information
    queryURL = uri + queryString + "?page=" + str(pageNumber)
    response = requests.get(queryURL, headers=headers)
    responseJson = response.json()
    # loadedJson = json.load(responseJson)
    carParks.extend(responseJson["value"])
    pageNumber = pageNumber + 1


data = {'value': str(carParks)}
print(data)

from firebase_admin import credentials, initialize_app, storage, firestore
# Init firebase with your credentials
cred = credentials.Certificate("fastandfurious-363205-1f57d31b8424.json")
app = initialize_app(cred, {'storageBucket': 'fastandfurious-363205.appspot.com'})
db = firestore.client()

db.collection(u'CarParkData').document(u'bs').set(data)