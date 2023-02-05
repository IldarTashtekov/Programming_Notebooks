import cv2 as cv
import numpy as np
from read_video_img import rescale_frame

img = cv.imread('src/image1.jpg')
img = rescale_frame(img, scale=2)

#1-Translation
def translate (img, x , y):
    trans_matrix = np.float32([[1,0,x],[0,1,y]])
    dimensions = (img.shape[1], img.shape[0])

    return cv.warpAffine(img, trans_matrix, dimensions)

#img = translate(img,20,100)


#2-Rotation
def rotate(img, angle, rotationPoint=None):
    (height, width) = img.shape[:2]

    if rotationPoint is None:
        rotationPoint = (width//2, height//2)

    rot_matrix = cv.getRotationMatrix2D(rotationPoint, angle, 1.0)
    dimensions = (width, height)

    return cv.warpAffine(img, rot_matrix, dimensions)

#img=rotate(img, 130)

#3- Flipping
#img = cv.flip(img,-1)

cv.imshow('Result', img)

cv.waitKey(0)
