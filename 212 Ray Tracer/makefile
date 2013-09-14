CC	= g++

INCLUDE	= -I.

CFLAGS	= -g -m32 -fopenmp

LDFLAGS = \
  -L. \
  -L/usr/lib

LDLIBS	= \
  -lc -lm

.cpp.o:
	$(CC) -c $(INCLUDE) $(CFLAGS) $<

SRCS = \
vector.cpp \
list.cpp \
pixel.cpp \
material.cpp \
object.cpp \
plane.cpp \
sphere.cpp \
model.cpp \
camera.cpp \
light.cpp \
ray.cpp \
photon.cpp \
timer.cpp \
kdtree.cpp \
main.cpp

OBJS = $(SRCS:.cpp=.o)

all: main

main: $(OBJS)
	$(CC) $(CFLAGS) $(INCLUDE) -o $@ $(OBJS) $(LDFLAGS) $(LDLIBS)

clean:
	rm -f *.o core
	rm -f *.a
	rm -f main
	rm -f *.ps *.pdf

MAKEDEP = $(wildcard gccmakedep)
ifeq ($(strip $(MAKEDEP)),)
depend: $(SRCS)
	gccmakedep -- $(CFLAGS) -- $(SRCS)
else
depend: $(SRCS)
	makedepend -- $(CLFAGS) -- $(SRCS)
endif

ENSCRIPTFLAGS = \
  --fancy-header=mya2ps \
  --columns=1 \
  --pretty-print=makefile \
  --ul-font=Times-Roman100 \
  --underlay="pm" \
  --portrait

PS2PDFFLAGS = \
  -dCompatibilityLevel=1.3 \
  -dMaxSubsetPct=100 \
  -dSubsetFonts=true \
  -dEmbedAllFonts=true \
  -dAutoFilterColorImages=false \
  -dAutoFilterGrayImages=false \
  -dColorImageFilter=/FlateEncode \
  -dGrayImageFilter=/FlateEncode \
  -dMonoImageFilter=/FlateEncode

ps:
	enscript $(ENSCRIPTFLAGS) Makefile -p makefile.ps
	enscript $(ENSCRIPTFLAGS) *.h -p interface.ps
	enscript $(ENSCRIPTFLAGS) *.c* -p implementation.ps

pdf:
	ps2pdf $(PS2PDFFLAGS) makefile.ps
	ps2pdf $(PS2PDFFLAGS) interface.ps
	ps2pdf $(PS2PDFFLAGS) implementation.ps
