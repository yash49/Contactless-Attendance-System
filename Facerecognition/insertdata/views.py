from django.shortcuts import render
from django.http import HttpResponse
# Create your views here.


from django.views.decorators.csrf import csrf_exempt


@csrf_exempt
def insertdata(request):
    return HttpResponse("Hello there")