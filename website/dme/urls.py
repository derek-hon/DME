from django.urls import path

from . import views

urlpatterns = [
    #default page
    #/dme/
    path('', views.index, name='index'),

    #viewing the configurations
    #/dme/results/
    path('results/', views.viewResults, name='results'),

    #Viewing specific configuration
    #/dme/results/5/
    path('results/<int:dme_id>/', views.viewConfiguration, name='configuration'),

    #documentation page
    #/dme/documentation
    path('documentation/', views.documentation, name='documentation'),
]