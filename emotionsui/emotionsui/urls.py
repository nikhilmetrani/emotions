from django.conf.urls import include, url
from django.contrib import admin
from emotions import views
urlpatterns = [
    url(r'^$', views.index, name='index'),
    url(r'^emotions/', include('emotions.urls', namespace='emotions')),
    url(r'^admin/', include(admin.site.urls)),
]