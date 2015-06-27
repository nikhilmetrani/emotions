import os
import urllib
# Create your tests here.

def files():
    dirToScreens = '/Users/jagadeesh/Applications/Programming/workspace20150328/emotionsui/data/'
    files = os.listdir(dirToScreens)
    print files
    feedFiles = {}
            
    for fileName in files:
         fileSplit = fileName.split('.')
         feedFiles[fileName] = fileSplit[0]
    print feedFiles


target_url = 'https://s3-us-west-2.amazonaws.com/aws-emotions-rishi/testData/empty.json'
txt = urllib.urlopen(target_url).read()
print txt
