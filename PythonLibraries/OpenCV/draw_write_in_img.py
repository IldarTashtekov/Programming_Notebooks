import cv2 as cv
import numpy as np
from read_video_img import rescale_frame


def read_video():
    #read video, 0=webcam, 1=the 1rst cam of computer, 2=the 2nd camara and so on,  file path= video
    capture = cv.VideoCapture('src/video1.mp4')
    #show video
    while True:
        isTrue, frame = capture.read()
        #cv.imshow('Video', frame)

        #resize video
        frame_resized = rescale_frame(frame, scale=0.2)
        #show the video
        cv.imshow('video', frame_resized)

        #is pulse d  the while loop is ended
        if cv.waitKey(20) & 0xFF==ord('d'):
            break

    capture.realse()
    cv.destroyAllWindows()


#blank image
blank = np.zeros((500,500,3), dtype='uint8')
cv.imshow('Blank', blank)

'''
#paint image in certain color with rgb chanels
blank[:] = 0,255,0
cv.imshow('Green', blank)

blank[:] = 0,0,0


#we can make the same with a certain arange of pixels
#blank[200:300,300:400] = 255,0,0
#cv.imshow('BlueSquare', blank)

#draw rectangle
        #base image, origin,   end,                                   color, line thicknes
cv.rectangle(blank, (0,0), (blank.shape[1]//2, blank.shape[0]//2), (0,255,0), thickness=2)

cv.imshow('Rectangle', blank)

blank[:] = 0,0,0

#draw a circle            center                       radius color
cv.circle(blank,(blank.shape[1]//2, blank.shape[0]//2), 100, 255, thickness=3 )
cv.imshow('Cyrcle', blank)

blank[:] = 0,0,0

#draw a line
cv.line(blank, (0,0), (130,blank.shape[0]), 255, thickness=4)
cv.imshow('Line', blank)
'''

blank[:] = 0,0,0
#write text            text          coordinates          FONT            font size   color thiknes
cv.putText(blank,'Hello OpenCV',(225,225), cv.FONT_HERSHEY_TRIPLEX, 1, (0,140,0), 2)
cv.imshow('text', blank)


cv.waitKey(0)
