from django.shortcuts import render
import json
import urllib

target_s3url = 'https://s3-us-west-2.amazonaws.com/aws-emotions-rishi/testData/'
def index(request):

    errormessage = ''
    feedFiles = {}
    product1JsonObject = {}
    product2JsonObject = {}
    product1 = ''
    product2 = ''
    try:
        products = urllib.urlopen(target_s3url+'products.json').read()
        print products;
        files = products.split(',')
        for fileName in files:
            if fileName != 'empty.json':
                fileSplit = fileName.split('.')
                feedFiles[fileName] = fileSplit[0]
        #print feedFiles.pop(0)
        
        product1=request.POST.get('product1','empty.json')
        #product1JsonText = open(path+product1, 'r').read()
        product1JsonText = urllib.urlopen(target_s3url+product1).read()
    
        product2=request.POST.get('product2','empty.json')
        #product2JsonText = open(path+product2, 'r').read()
        product2JsonText = urllib.urlopen(target_s3url+product2).read()
        
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
        print "Unexpected error"
       
    product1Name = product1JsonObject.get('product')
    product2Name = product2JsonObject.get('product')
    
    product1HappyIndexList = product1JsonObject.get('happyindex')
    product2HappyIndexList = product2JsonObject.get('happyindex')
    
    context = {'error_message' : errormessage,'product1':product1,'product2':product2, 'feedFiles':sorted(feedFiles.iteritems()), 'product1Name':product1Name, 'product2Name' : product2Name, 
               'product1HappyIndexList' : product1HappyIndexList, 'product2HappyIndexList':product2HappyIndexList}
    return render(request, 'emotions/index.html', context)


def reLoadProducts(request):
    s3file = request.GET.get('filename','empty.json')
    context = {'products':urllib.urlopen(target_s3url+s3file).read()}
    return render(request, 'emotions/worked.html', context)
    
    