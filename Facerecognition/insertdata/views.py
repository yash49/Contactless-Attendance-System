from django.shortcuts import render
from django.http import JsonResponse
from django.core.files.storage import FileSystemStorage
from django.views.decorators.csrf import csrf_exempt

import os

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
            filename = fs.save(imagefilename, image)
    
            return JsonResponse({'result':'Success','message':'User data has been stored successfully.'})
        
        except:
            return JsonResponse({'result':'Fail','message':'Some Error has occured.'})

    return JsonResponse({'result':'Fail','message':'Bad Request.'})