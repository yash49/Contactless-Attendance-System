from django.shortcuts import render
from django.http import JsonResponse
from django.core.files.storage import FileSystemStorage
from django.views.decorators.csrf import csrf_exempt

import os
import pickle
import face_recognition

import time
import random

import cv2
import numpy as np

import asyncio

############################### FIREBASE ###############################

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db

cred = credentials.Certificate('mycreds.json')
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://ingenious-1221c-default-rtdb.firebaseio.com/'
})

def insertUser(email,name):

    if(firebase_admin.get_app()==None):
        cred = credentials.Certificate('mycreds.json')
        firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://ingenious-1221c-default-rtdb.firebaseio.com/'
        })

    reference=db.reference('users')
    tmp = reference.get()
    
    keys = list(tmp.keys())
    keys.remove('GS2')
    #print(keys)
    
    id = 0
    keys = list(map(int, keys))
    if(len(keys)>0):
        id = max(keys)+1
    
    tmp[str(id)] = dict()
    
    tmp[str(id)]['email'] = str(email)
    tmp[str(id)]['name'] = str(name)
    
    reference=db.reference('users')
    reference.set(tmp)
    
    return id

import time

def insertLog(id):

    cur = -1
    past = -1

    if(firebase_admin.get_app()==None):
        cred = credentials.Certificate('mycreds.json')
        firebase_admin.initialize_app(cred, {
            'databaseURL': 'https://ingenious-1221c-default-rtdb.firebaseio.com/'
        })
    
    timestamp = str(int(time.time()))
    cur = timestamp
    reference=db.reference('logs')
    tmp = reference.get()
    
    if(str(id) not in tmp):
        tmp[str(id)] = dict()
    
    status = 1
    keys = list(tmp[str(id)].keys())
    keys = list(map(int, keys))
    # print(keys)
    
    if(len(keys)>0):
        maxx = max(keys)
        past = int(maxx)
        status = 1-int(tmp[str(id)][str(maxx)])
    
    tmp[str(id)][timestamp] = str(status) 
  
    reference=db.reference('logs')
    reference.set(tmp)

##############################################################


async def encodeSingleFace(filename,name):


    if(not os.path.exists('valid_encoding')):
        valid_encoding = []
        yo = open('valid_encoding', 'wb') 
        pickle.dump(valid_encoding, yo)                      
        yo.close() 

    if(not os.path.exists('valid_names')):
        valid_names = []
        yo = open('valid_names', 'wb') 
        pickle.dump(valid_names, yo)                      
        yo.close() 

    path = 'UserImages'

    yo = open('valid_encoding', 'rb')      
    valid_encoding = pickle.load(yo) 

    yo = open('valid_names', 'rb') 
    valid_names = pickle.load(yo) 

    
    # Loading the image
    image = face_recognition.load_image_file(filename)

    # encoding image
    image_encoding = face_recognition.face_encodings(image)[0]

    # append encoded image in the list
    valid_encoding.append(image_encoding)

    # append the name of the person in the list  Ex. 0_Yash Shah
    valid_names.append(name)

    print('Image of {} has been encoded'.format(name))

    print('-----------------------------------------------------')

    yo = open('valid_encoding', 'wb') 
    pickle.dump(valid_encoding, yo)                      
    yo.close() 

    yo = open('valid_names', 'wb') 
    pickle.dump(valid_names, yo)                      
    yo.close() 



@csrf_exempt
def insertdata(request):

    print("in insert data")

    if request.method == 'GET':
        return JsonResponse({'result':'Fail','message':'This is a GET Request. Use Post Request'})

    data = request.POST   

    print("in insert data ")
    print(data)

    if request.method == 'POST' and request.FILES['image'] and ('name' in data) and ('email' in data):

        try:

            name = data['name']
            email = data['email']
            image = request.FILES['image']

            id = insertUser(email,name)

            #check if dir exists
            path = 'UserImages'

            if(not os.path.exists(path)):
                os.mkdir(path)

            imagefilename = path+"/"+str(id)+"_"+str(name)+".jpg"
            fs = FileSystemStorage()
            fs.save(imagefilename, image)

            print('encoding the face')
            # create and append the new encoding to the cached files
            asyncio.run(encodeSingleFace(imagefilename,str(id)+"_"+str(name)))
            print('encoding done')
    
            return JsonResponse({'result':'Success','message':'User data has been stored successfully.'})
        
        except:
            return JsonResponse({'result':'Fail','message':'Some Error has occured.'})

    return JsonResponse({'result':'Fail','message':'Bad Request.'})



def getRandomFileName():
    x = 'a b c d e f g h i j k l m n o p q r s t u v w x y z 0 1 2 3 4 5 6 7 8 9'.split(" ")
    r = ''
    for i in range(10):
        random.shuffle(x)
        r+=x[0]
    ts = time.time()
    tmpfilename = r+(''.join(str(ts).split('.')))+'.jpg'
    return tmpfilename

def getSingleEncoding(path):
    frame = cv2.imread(path)
    frame = cv2.resize(frame, (0, 0), fx=1, fy=1)
    frame = frame[:, :, ::-1]
    face_locations = face_recognition.face_locations(frame)
    frame = face_recognition.face_encodings(frame, face_locations)
    return frame

def getAllMatchingFaces(encodings,valid_encoding,valid_names):

    ls = []
    
    for face_encoding in encodings:
    
        matches = face_recognition.compare_faces(valid_encoding, face_encoding)
        name = "unregistered"
        
        face_distances = face_recognition.face_distance(valid_encoding, face_encoding)
        
        best_match_index = np.argmin(face_distances)
        
        if(matches[best_match_index]):
            name = valid_names[best_match_index]
        ls.append(name)
        
    return ls

@csrf_exempt
def verifyuser(request):

    if request.method == 'GET':
        return JsonResponse({'result':'Fail','message':'This is a GET Request. Use Post Request'})

    if request.method == 'POST' and request.FILES['image']:

        imagefilename = getRandomFileName()
        try:

            image = request.FILES['image']   
            fs = FileSystemStorage()
            fs.save(imagefilename, image)
            print('#########################################',imagefilename)
            # compare with the existing data and create entry in firebase , also return json response [name ,email,id]
    
            yo = open('valid_encoding', 'rb')      
            valid_encoding = pickle.load(yo) 
            yo.close()
            
            yo = open('valid_names', 'rb') 
            valid_names = pickle.load(yo) 
            yo.close()
            
            matchedFaces = []
            frame = getSingleEncoding(imagefilename)
            # print(valid_names)
            matchedFaces = getAllMatchingFaces(frame,valid_encoding,valid_names)
            print('#########################################',matchedFaces)
            
            # generate logs and response
            res = ''

            for user in matchedFaces:
                if(user=='unregistered'):
                    continue
                tmp = user.split('_')
                res+=tmp[0]
                insertLog(int(tmp[0]))
                res+=','
                res+=tmp[1]
                res+=','

            if(res==''):
                res = "-1,unregistered,"

            # remove tmp file
            if(os.path.exists(imagefilename)):    
                os.remove(imagefilename)

            return JsonResponse({'result':'Success',"message":res})
        
        except:
            if(os.path.exists(imagefilename)):    
                os.remove(imagefilename)
            return JsonResponse({'result':'Fail','message':'Some Error has occured.'})

    return JsonResponse({'result':'Fail','message':'Bad Request.'})