import cv2 as cv
from read_video_img import rescale_frame


img = cv.imread('src/yo_twink.jpg')
img = rescale_frame(img, scale=1.1)

g_img = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

haar_cascade = cv.CascadeClassifier('src/haarcascade_frontalface_default.xml')

faces_rect = haar_cascade.detectMultiScale(g_img, minNeighbors=3)
print(f'num of faces found={len(faces_rect)}')

for (x,y,w,h) in faces_rect:
    cv.rectangle(img, (x,y), (x+w,y+h), (0,255,0), thickness=2)

cv.imshow('detected faces', img)



cv.waitKey(0)