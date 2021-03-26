from django.urls import path

from . import views

urlpatterns = [
    path('insertdata', views.insertdata,name = 'insertdata'),
]
