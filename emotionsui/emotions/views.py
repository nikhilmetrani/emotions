from django.shortcuts import render
import json
import sys, os

path = '/Users/jagadeesh/Applications/Programming/workspace20150328/emotionsui/data/'
def index(request):

    errormessage = ''
    feedFiles = {}
    product1JsonObject = {}
    product2JsonObject = {}
    product1 = ''
    product2 = ''
    try:
        
        files = os.listdir(path)
        for fileName in files:
            if fileName != 'empty.json':
                fileSplit = fileName.split('.')
                feedFiles[fileName] = fileSplit[0]
        #print feedFiles.pop(0)
        product1=request.POST.get('product1','empty.json')
        product1JsonText = open(path+product1, 'r').read()
    
        product2=request.POST.get('product2','empty.json')
        product2JsonText = open(path+product2, 'r').read()
        
        product1JsonObject = json.loads(product1JsonText);
        
        product2JsonObject = json.loads(product2JsonText);
        
        
    except IOError as error:
        errormessage = "Oops!  An error occurred.  Try again..."
        print error
    except UnicodeDecodeError as error: 
        errormessage = "Oops!  An error occurred. ry again..."
        print error
    except:
        errormessage = "Oops!  An error occurred.  Try again..."
        print "Unexpected error:", sys.exc_info()[0]
       
    product1Name = product1JsonObject.get('product')
    product2Name = product2JsonObject.get('product')
    
    product1HappyIndexList = product1JsonObject.get('happyindex')
    product2HappyIndexList = product2JsonObject.get('happyindex')
    
    context = {'error_message' : errormessage,'product1':product1,'product2':product2, 'feedFiles':sorted(feedFiles.iteritems()), 'product1Name':product1Name, 'product2Name' : product2Name, 
               'product1HappyIndexList' : product1HappyIndexList, 'product2HappyIndexList':product2HappyIndexList}
    return render(request, 'emotions/index.html', context)
    