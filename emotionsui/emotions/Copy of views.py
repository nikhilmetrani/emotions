from django.shortcuts import render
import ujson, urllib, datetime
import sys
# Create your views here.
def index(request):
    
    earthquakes = []
    newsItems = []
    magrangelist = []
    errormessage = ''
    try:
        target, eqdate, eqmagnitude = getTargetURL(request)
        f = urllib.urlopen(target)
        eqjsonstring = f.read().decode("utf-8")
        f.close()
        earthquakesFullData = ujson.loads(eqjsonstring);
        magnitudeRangeMap = {'0-1':0, '1-2':0, '2-3':0, '3-4':0, '4-5':0, '5-6':0, '6-7':0, '7-8':0, '> 8':0 }
        for eqitem in earthquakesFullData['features']:
            properties = eqitem['properties']
            magValue = properties['mag'];
            if magValue is not None:
                coordinates = eqitem['geometry']['coordinates']
                if(magValue >0 and magValue < 1):
                    magnitudeRangeMap['0-1'] = magnitudeRangeMap['0-1'] + 1
                elif(magValue >=1 and magValue < 2):
                    magnitudeRangeMap['1-2'] = magnitudeRangeMap['1-2'] + 1
                elif(magValue >=2 and magValue < 3):
                    magnitudeRangeMap['2-3'] = magnitudeRangeMap['2-3'] + 1
                elif(magValue >=3 and magValue < 4):
                    magnitudeRangeMap['3-4'] = magnitudeRangeMap['3-4'] + 1
                elif(magValue >=4 and magValue < 5):
                    magnitudeRangeMap['4-5'] = magnitudeRangeMap['4-5'] + 1
                elif(magValue >=5 and magValue < 6):
                    magnitudeRangeMap['5-6'] = magnitudeRangeMap['5-6'] + 1
                elif(magValue >=6 and magValue < 7):
                    magnitudeRangeMap['6-7'] = magnitudeRangeMap['6-7'] + 1
                elif(magValue >=7 and magValue < 8):
                    magnitudeRangeMap['7-8'] = magnitudeRangeMap['7-8'] + 1
                elif magValue >=8:
                    magnitudeRangeMap['> 8'] = magnitudeRangeMap['> 8'] + 1
                dateValue = datetime.datetime.fromtimestamp(properties['time']/1000.0).strftime('%Y-%m-%d %H:%M:%S')
                earthquakes.append({'mag': properties['mag'], 'place': properties['place'], 'millis':properties['time'],
                                'time': dateValue, 'title': properties['title'], 'lat': coordinates[1],  'lng': coordinates[0]})
        magrangelist = sorted(magnitudeRangeMap.iteritems())
        newsItems = getNews(eqdate)
    except IOError as error:
        errormessage = "Oops!  An error occurred.  Try again..."
        print error.errno
    except UnicodeDecodeError: 
        errormessage = "Oops!  An error occurred.  Please enter valid Date YYYY-MM-DD"
    except:
        errormessage = "Oops!  An error occurred.  Try again..."
        print "Unexpected error:", sys.exc_info()[0]
       
        
    context = {'error_message' : errormessage, 'news_items': newsItems, 'earthquakes_list': earthquakes, 'magnitude_range_map': magrangelist,'eqdate':eqdate, 'eqmagnitude':eqmagnitude}
    return render(request, 'emotions/index.html', context)

def getTargetURL(request):
    eqdate=request.POST.get('eqdate')
    if eqdate is None or eqdate == '':
        eqdate = datetime.date.today().strftime('%Y-%m-%d')
    eqmagnitude=request.POST.get('eqmagnitude','all')
    #print eqmagnitude
    maxminMag = ''
    if(eqmagnitude == 'all'):
        maxminMag = ''
    else:
        maxminMag = '&minmagnitude='+eqmagnitude
    target = 'http://earthquake.usgs.gov/fdsnws/event/1/query?starttime='+eqdate+'T00:00:00&endtime='+eqdate+'T23:59:59&orderby=time&format=geojson'+maxminMag
    #print target
    return target, eqdate, eqmagnitude

def getNews(eqdate):
    #eqdate = '2015-06-30'
    startDate = datetime.datetime.strptime(eqdate, "%Y-%m-%d").date()
    enddate = startDate + datetime.timedelta(days=4)
    newsapiURL = 'http://content.guardianapis.com/search?&api-key=a8zre7dgchh6cwf6pt9xmv7j&section=environment%20%7C%20world&from-date='+startDate.strftime('%Y-%m-%d')+'&to-date='+enddate.strftime('%Y-%m-%d')+'&order-by=relevance&page-size=20&q=earthquake'
    f = urllib.urlopen(newsapiURL)
    eqjsonstring = f.read().decode("utf-8")
    f.close()
    earthquakesNewsJson = ujson.loads(eqjsonstring);
    #print earthquakesNewsJson
    newsitems = []
    for newsItem in earthquakesNewsJson['response']['results']:
        #print newsItem
        newsitems.append({'webTitle':newsItem['webTitle'] , 'webUrl':newsItem['webUrl'] ,'webPublicationDate':newsItem['webPublicationDate'] })
    #return HttpResponse(ujson.dumps(newsitems), content_type="application/json")
    return newsitems
    