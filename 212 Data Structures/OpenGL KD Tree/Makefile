UNAME		= $(shell uname)

ifeq ($(UNAME),Linux)
TARGET		= linux
PLATFORM	= unix
#export QTDIR	= /usr/lib/qt4
export QTDIR	= /usr/share/qt4
QMAKE		= $(QTDIR)/bin/qmake
SPEC		= $(QTDIR)/mkspecs/linux-g++-64
endif

ifeq ($(UNAME),Darwin)
TARGET		= darwin
PLATFORM	= macx
export QTDIR	= /usr
QMAKE		= $(QTDIR)/bin/qmake
SPEC		= /usr/local/Qt4.6/mkspecs/macx-g++40
endif

all: mkfiles apps

apps:
	make -f Makefile.qmake

clean: mkfiles
	make -f Makefile.qmake clean
	rm -rf Makefile.qmake
	rm -rf mocs objs
	rm -rf *.app

mkfiles:
	$(QMAKE) -makefile -$(PLATFORM) -spec $(SPEC) -o Makefile.qmake

ENSCRIPTFLAGS = \
  --fancy-header=mya2ps \
  --columns=1 \
  --pretty-print=makefile \
  --ul-font=Times-Roman100 \
  --underlay="image-texture" \
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

pdf: ps
	ps2pdf $(PS2PDFFLAGS) makefile.ps
	ps2pdf $(PS2PDFFLAGS) interface.ps
	ps2pdf $(PS2PDFFLAGS) implementation.ps
