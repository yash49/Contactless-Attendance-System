from django.shortcuts import render
from django.http import HttpResponse
# Create your views here.


from django.views.decorators.csrf import csrf_exempt

# Added 2 rules in windows firewall - 1) inbound port 8000 1) outbound port 8000

@csrf_exempt
def insertdata(request):
    return HttpResponse("Hello there")