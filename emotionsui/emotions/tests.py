import os

# Create your tests here.
path = '/Users/jagadeesh/Applications/Programming/workspace20150328/emotionsui/data/'
filesList = []
for files in os.walk(path):
    for filename in files:
           filesList.append(filename)
print (filename)