import os
# Create your tests here.
dirToScreens = '/Users/jagadeesh/Applications/Programming/workspace20150328/emotionsui/data/'
files = os.listdir(dirToScreens)
print files
feedFiles = {}
        
for fileName in files:
     fileSplit = fileName.split('.')
     feedFiles[fileName] = fileSplit[0]
print feedFiles
