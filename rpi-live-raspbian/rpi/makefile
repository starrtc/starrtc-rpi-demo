CPP=arm-linux-gnueabihf-gcc
CFLAG=-fPIC -g -finline-functions -Wall -W -pipe \
		   -Wreturn-type -Wtrigraphs -Wformat -Wparentheses -Wpointer-arith \
		   -D_GNU_SOURCE -Wno-deprecated -fpermissive -lpthread -lstarRTC -lstarRTCCore -lcurl -lstdc++ -lmmal -lbcm_host

INCLUDE=/root/rpi-demo/temp/rpi-demo/starrtc/include
LIB=/root/rpi-demo/temp/rpi-demo/starrtc/lib
DIR=./

CUTL_PATH=/usr/local/curl

INCLUDE_PATH=-I$(INCLUDE)/ -I$(CUTL_PATH)/include
LIB_PATH=-L$(LIB)/

CUTL_LIB_PATH=-L$(CUTL_PATH)/lib
THIRD=/opt/vc/lib

THIRD_PATH=-L$(THIRD)
SRC=$(wildcard *.cpp)
OBJS=$(patsubst %.cpp, %.o, $(SRC))

PROC=rpi-demo

$(PROC):	$(OBJS)
	$(CPP) $(CFLAG) $(OBJS) -o $(PROC) $(INCLUDE_PATH) $(LIB_PATH) $(CUTL_LIB_PATH) $(THIRD_PATH)

$(DIR)/%.o:	%.cpp
	$(CPP) $(CFLAG) -c $(INCLUDE_PATH) $< -o $@

clean:
	rm -f $(PROC) $(OBJS)