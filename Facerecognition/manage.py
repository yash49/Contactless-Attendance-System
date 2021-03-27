#!/usr/bin/env python
"""Django's command-line utility for administrative tasks."""
import os
import sys
import face_recognition
import pickle

def filesFromPath(path):
    f = [] 
    for (root,dirs,files) in os.walk(path): 
        f = files
        break

    valid_members = []

    for member in f:
        tmp = [member.split('.')[0],path+'/'+member]
        valid_members.append(tmp)

    return valid_members

def createEncoding():
    path = 'UserImages'

    if(not os.path.exists(path)):
        os.mkdir(path)

    valid_members = filesFromPath(path)

    print(valid_members)

    valid_encoding = []
    valid_names = []

    for i in valid_members:

        # Loading the image
        image = face_recognition.load_image_file(i[1])

        # encoding image
        image_encoding = face_recognition.face_encodings(image)[0]

        # append encoded image in the list
        valid_encoding.append(image_encoding)

        # append the name of the person in the list
        valid_names.append(i[0])

        print('Image of {} has been encoded'.format(i[0]))

        print('-----------------------------------------------------')


    # yo = open('valid_members', 'wb') 
    # pickle.dump(valid_members, yo)                      
    # yo.close() 

    yo = open('valid_encoding', 'wb') 
    pickle.dump(valid_encoding, yo)                      
    yo.close() 

    yo = open('valid_names', 'wb') 
    pickle.dump(valid_names, yo)                      
    yo.close() 


def main():
    os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'Facerecognition.settings')
    try:
        from django.core.management import execute_from_command_line
    except ImportError as exc:
        raise ImportError(
            "Couldn't import Django. Are you sure it's installed and "
            "available on your PYTHONPATH environment variable? Did you "
            "forget to activate a virtual environment?"
        ) from exc
    execute_from_command_line(sys.argv)


if __name__ == '__main__':
    
    #createEncoding()


    main()
