import cv2 as cv



def read_image():
# function to read a image
    img = cv.imread('src/image1.png')

    img=rescale_frame(img, scale=0.25)
#function to show the image, the params are , window name and image variable
    cv.imshow('name',img)

#espera hasta que pulsas cualquier tecla
    cv.waitKey(0)


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

#useful to resize images and videos and live_videos
def rescale_frame(frame, scale=0.5):

    width = int(frame.shape[1] * scale )
    heigth = int(frame.shape[0] * scale )
    dimensions = (width, heigth)

    return cv.resize(frame, dimensions, interpolation=cv.INTER_AREA)

#this method only resize live video
def change_resolution(width, height):
    capture = cv.VideoCapture('path')

    capture.set(3,width)
    capture.set(4,height)



#read_image()