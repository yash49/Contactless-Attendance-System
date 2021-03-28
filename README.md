# Ingenious-Hackathon

## Demo Video

<iframe width="560" height="315" src="https://www.youtube.com/embed/DteJqgMTaok" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>


<img src="https://user-images.githubusercontent.com/30389552/112749235-a7766d00-8fde-11eb-944f-47c0ab27af37.gif" width="300px" height="550px"/>       <img src="https://user-images.githubusercontent.com/30389552/112749235-a7766d00-8fde-11eb-944f-47c0ab27af37.gif" width="300px" height="550px"/>    <img src="https://user-images.githubusercontent.com/30389552/112749235-a7766d00-8fde-11eb-944f-47c0ab27af37.gif" width="300px" height="550px"/>

## Inspiration:

Owing to the current (COVID) situation it is essential to have a system, which has minimum contact, for attendance. Biometric attendance possesses the risk of spreading bacteria among its users. So in the pursuit of a solution, we came up with an idea of taking attendance with the help of a camera sensor. The current systems were capable of processing only one user's request at a time, which was very inefficient. Hence the inspiration came from the pursuit of a solution for no contact attendance system.

## What it does

<img src="https://user-images.githubusercontent.com/48802492/112749574-cd9d0c80-8fe0-11eb-91de-646e1e0bed1a.png"/>

## Technologies Used

Python-Django, Android, Firebase, Google Vision API

## Challenges we ran into

There were many challenges that we encountered while building the project like retrieving the face images from the camera and sending them to the server for processing, recognizing multiple faces in the same image. Initially, we were sending every captured image to the server for detection, but it was too costly by bandwidth, processing and it generated an alternative log for check-in check-out consequently until the user does not leave the frame. So we found a solution by using Google Vision API which will look for a face in the image and if there is a face available then and then only the image is sent to the server. So this was a game-changing optimation. The library we have used to Recognize Face is face-recognition which is used to Recognize faces from Python. It is built using dlib’s state-of-the-art face recognition built with deep learning. The model has an accuracy of 99.38% on the Labeled Faces in the Wild benchmark.


## Accomplishments that we're proud of

Working Android App for Registering data of users like name, email, image of users with email authentication to Server [ to Django API ] which will store in the database, the App also shows the attendance logs of the Users and Statistics.

Working Android App for Monitoring the doors of the buildings/offices/rooms/colleges/lecture halls. It will capture the image of users and send it to the server for validation [ to Django API ] only when the face is visible inside the camera frame [ Thanks to Google vision API ], The Django API will compare the image with registered faces and generate the Attendance log in Firebase. The API returns a JSON response.

## What's next for 

We don’t have any IOT device with us that’s why we had created an Android App for monitoring. In Future what we can do is, We can use an IOT device like Raspberry pi for Monitoring which will call the Django API. We can also have multiple devices for Monitoring at different gates/doors.

Advance Leave Ticket - Whenever any user is going to take a leave then he/she can put a ticket in advance the duration of leave along with the reason.

Push Notification - Whenever any user is expected to be at any organization and he/she is absent then a notification mechanism will alert the user of the event.


## What is required and How to run the System.

firebase Realtime Database
Database Name : ingenious

The firebase credentials JSON should be named as "mycreds.json" and placed in the Facerecognition folder[Project directory].
The URLS of the respected database should be updated in the Project.

You will also need the python and some additional packages.

Additional required packages
----------------------------
wheel
cmake
cv2
dlib
face_recognition
numpy
django
django-sslserverpickle
firebase_admin
shutil

Note :- If you get any error while installing dlib. Then Please Install Visual C++ tools for CMake in your Computer.




