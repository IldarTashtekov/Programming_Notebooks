import cv2 as cv
import numpy as np
from read_video_img import rescale_frame

img = cv.imread('src/image1.jpg')
img = rescale_frame(img, scale=1.5)

#1. converting to grayscale function
#img = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

#2. gaussian blur function
#                      img, kernel size, border type
#img = cv.GaussianBlur(img,(7,7), cv.BORDER_DEFAULT)

#3. edge detector
img = cv.Canny(img, 170, 25)

#4. other edge detection method
def contour_detections(img):
    # create blank image
    blank = np.zeros(img.shape, dtype='uint8')
    g_img = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    #thresh
    ret, thresh = cv.threshold(g_img, 125, 255, cv.THRESH_BINARY )

    #get contours
    contours, hierarchies = cv.findContours(thresh, cv.RETR_LIST, cv.CHAIN_APPROX_SIMPLE)

    #draw contours in blanck image
                              #base, contours,  -1=all contours, color, thiknes of line
    img_cont = cv.drawContours(blank, contours, -1, (0, 0, 255), 2)
    cv.imshow(' contours ',img_cont)

def extra_edge_detections(img):
    g_img = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
    #laplacian edge detection
    lap = cv.Laplacian(g_img, cv.CV_64F)
    lap = np.uint8(np.absolute(lap))
    cv.imshow('Laplacian', lap)

    #sobel edge detection
    sobel_x = cv.Sobel(g_img, cv.CV_64F, 1, 0)
    sobel_y = cv.Sobel(g_img, cv.CV_64F, 0, 1)
    cv.imshow('Sobel x', sobel_x)
    cv.imshow('Sobel y', sobel_y)

    conbined_sobel = cv.bitwise_or(sobel_x,sobel_y)
    cv.imshow('Combi sobel', conbined_sobel)

#5. dilating image
#img = cv.dilate(img, (9,9), iterations=5)

#6. eroding
#img = cv.erode(img, (9,9), iterations=10)

#7. resize
#img = cv.resize(img, (400,200), interpolation = cv.INTER_CUBIC)

#8. cropping
#img = img[50:200, 100:400]


#contour_detections(img)
#extra_edge_detections(img)

cv.imshow('Result', img)

cv.waitKey(0)
