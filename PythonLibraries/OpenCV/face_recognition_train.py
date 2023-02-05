import os
import cv2 as cv
import numpy as np

people = ['Ben Afflek', 'Elton John', 'Jerry Seinfield', 'Madonna', 'Mindy Kaling']

DIR = r'src\Faces\train'
haar_cascade = cv.CascadeClassifier('src/haarcascade_frontalface_default.xml')

#the image arrays of faces img
features = []
#the array of labels of every feature array
labels = []


def create_train():
    for person in people:
        #grab every path
        path = os.path.join(DIR, person)
        #create number label for every person
        label = people.index(person)

        #for every image in a folder
        for img in os.listdir(path):
            #se create path
            img_path = os.path.join(path, img)
            #read the image of the path
            img_array = cv.imread(img_path)

            if img_array is None:
                continue
            #haar cascade want gayscale images
            gray = cv.cvtColor(img_array, cv.COLOR_BGR2GRAY)
            #we use the faces
            faces_rect = haar_cascade.detectMultiScale(gray, scaleFactor=1.1, minNeighbors=4)
            #we grab the faces regions of interest
            for (x, y, w, h) in faces_rect:
                faces_roi = gray[y:y + h, x:x + w]
                #faces image array
                features.append(faces_roi)
                #labels array
                labels.append(label)


create_train()
print('Training done ---------------')


features = np.array(features, dtype='object')
labels = np.array(labels)

#instantiate the face recognizer model
face_recognizer = cv.face.LBPHFaceRecognizer_create()

# Train the Recognizer on the features list and the labels list
face_recognizer.train(features, labels)

face_recognizer.save('face_trained.yml')
np.save('features.npy', features)
np.save('labels.npy', labels)


