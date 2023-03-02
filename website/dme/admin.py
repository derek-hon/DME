from django.contrib import admin

from .models import DMEConfig, DME_Map, fitness
# Register your models here.

admin.site.register(DMEConfig)
admin.site.register(DME_Map)
admin.site.register(fitness)

