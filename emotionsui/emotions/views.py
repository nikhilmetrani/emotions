from django.shortcuts import render
import ujson, urllib, datetime
import sys


path = '/Users/jagadeesh/Applications/Programming/workspace20150328/emotionsui/data/'
def index(request):
    
    product1=request.POST.get('product1','iphone6')
    product1JsonText = open(path+product1+'.json', 'r').read()
    
    
    product2=request.POST.get('product2','sgalaxy6')
    product2JsonText = open(path+product2+'.json', 'r').read()
    
    errormessage = ''
    try:
        
        product1JsonObject = ujson.loads(product1JsonText);
        
        product2JsonObject = ujson.loads(product2JsonText);
        
        
    except IOError as error:
        errormessage = "Oops!  An error occurred.  Try again..."
        print error.errno
    except UnicodeDecodeError: 
        errormessage = "Oops!  An error occurred."
    except:
        errormessage = "Oops!  An error occurred.  Try again..."
        print "Unexpected error:", sys.exc_info()[0]
       
    product1Name = product1JsonObject['Product']
    product2Name = product2JsonObject['Product']
    
    product1HappyIndexList = product1JsonObject['happyindex'];
    product2HappyIndexList = product2JsonObject['happyindex'];
    
    print product1HappyIndexList
    
    context = {'error_message' : errormessage, 'product1Name':product1Name, 'product2Name' : product2Name, 
               'product1HappyIndexList' : product1HappyIndexList, 'product2HappyIndexList':product2HappyIndexList}
    return render(request, 'emotions/index.html', context)
    