# Compiler stuff
CXX 		 = g++
CXXFLAGS = -std=c++11 -Wall -O3


# The files we'll be compiling
HDRS = Rainbow_table.h
SRCS = main.cpp
OBJS = $(SRCS:.cpp=.o) $(SRCS_SLN:.cpp=.o)
LIBS = -lssl -lcrypto
EXE  = hashbash


# Change flags to ignore the deprecated SHA functions if compiling
UNAME = $(shell uname -s)

ifneq ($(UNAME),Darwin)
  CXXFLAGS += -Werror
endif


# Rules
$(EXE): $(OBJS) $(HDRS)
	$(CXX) $(CXXFLAGS) -o $@ $(OBJS) $(LIBS)

%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $<

.PHONY: clean
clean: 
	-rm -f -r $(OBJS) *.o *~ *core* $(EXE)
