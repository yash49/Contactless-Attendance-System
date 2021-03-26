from django.shortcuts import render
from django.http import JsonResponse
from django.core.files.storage import FileSystemStorage
from django.views.decorators.csrf import csrf_exempt

import os
import pickle
import face_recognition

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

##############################################################


def encodeSingleFace(filename,name):

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

    if request.method == 'GET':
        return JsonResponse({'result':'Fail','message':'This is a GET Request. Use Post Request'})

    data = request.POST

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

            # create and append the new encoding to the cached files
            encodeSingleFace(imagefilename,str(id)+"_"+str(name))
    
            return JsonResponse({'result':'Success','message':'User data has been stored successfully.'})
        
        except:
            return JsonResponse({'result':'Fail','message':'Some Error has occured.'})

    return JsonResponse({'result':'Fail','message':'Bad Request.'})