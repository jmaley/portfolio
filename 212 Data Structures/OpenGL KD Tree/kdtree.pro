TEMPLATE	 = app

TARGET		 = kdtree

MOC_DIR		 = mocs
OBJECTS_DIR	 = objs

QT		+= opengl

CONFIG		+= warn_on release

INCLUDEPATH	+= .

HEADERS		+= point.h tree.h glwinobj.h gltexobj.h
SOURCES		+= point.cpp tree.cpp glwinobj.cpp gltexobj.cpp main.cpp
