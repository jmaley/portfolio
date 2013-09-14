CC = g++

INCLUDE = -I.

CFLAGS = -g -fopenmp -Wall

LDFLAGS = -L. -L/usr/lib

LDLIBS = -lc -lm


.cpp.o:
	$(CC) -c $(INCLUDE) $(CFLAGS) $<

OBJS = list.o pairs.o

all: main

main: main.cpp main.o $(OBJS)
	$(CC) $(CFLAGS) $(INCLUDE) -o $@ $@.o $(OBJS) $(LDFLAGS) $(LDLIBS)

clean:
	rm -f *.o core
	rm -f *.a
	rm -f main
	rm -f *.ps
