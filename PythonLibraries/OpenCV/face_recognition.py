import cv2 as cv
import numpy as np


people = ['Ben Afflek', 'Elton John', 'Jerry Seinfield', 'Madonna', 'Mindy Kaling']

haar_cascade = cv.CascadeClassifier('src/haarcascade_frontalface_default.xml')

#features = np.load('features.npy', allow_pickle=True)
#labels = np.load('labels.npy', allow_pickle=True)

face_recognizer = cv.face.LBPHFaceRecognizer_create()
face_recognizer.read('face_trained.yml')

#test image
img = cv.imread('src/foto.jpg')
img=cv.cvtColor(img, cv.COLOR_BGR2GRAY)


#detect the face on image
faces_rect = haar_cascade.detectMultiScale(img, scaleFactor=1.1, minNeighbors=4)
# we grab the faces regions of interest
for (x, y, w, h) in faces_rect:
    faces_roi = img[y:y + h, x:x + w]

    label, confidence = face_recognizer.predict(faces_roi)
    print('label is equal to ', label, 'with confidence of ', confidence)

    cv.putText(img, str(people[label]), (20,20), cv.FONT_HERSHEY_COMPLEX, 1.1, (0,255,0),2)

cv.imshow('Detected Face ', img)

cv.waitKey(0)
