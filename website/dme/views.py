import random
from django.shortcuts import render
from django.http import HttpResponse
from django.template import loader

from .models import DMEConfig, DME_Map

# Create your views here.

def index(request):
    return HttpResponse("DME index")

def documentation(request):
    return HttpResponse("Documentation.")

def viewResults(request, results):
    configurations = DMEConfig.objects.all()
    maps = DMEConfig.objects.values('dme_map__image')
    paths = ', '.join([dme.path for dme in maps])

    for config in configurations:
        map_set = config

    output = ""

    # testList = 
    # testList.append(DMEConfig.objects.filter(islands=dmeConfig.islands, date=dmeConfig.date, DMEConfig__map__islandNumber=random(dmeConfig.islands)).)
    # output = ', '.join([dme.map.image for dme in configuration_list])
    return HttpResponse(paths)

def viewResultThumbnail(request, dmeDate, islandNumber):
    image = DMEConfig.objects.filter(date=dmeDate, map__island_number=islandNumber).values('image')
    return HttpResponse(image)

def viewConfiguration(request, dme_id):
    response = "You're looking at configuration %s."
    return HttpResponse(response % dme_id)

def generateDME(request):
    return HttpResponse("Generate your DME configuration.")