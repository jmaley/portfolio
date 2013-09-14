/****************************************************************************
** $Id: qt/main.cpp 3.1.2 edited Nov 8 2002 $
**
** Copyright (C) 1992-2000 Trolltech AS. All rights reserved.
**
** This file is part of an example program for Qt. This example
** program may be used, distributed and modified without limitation.
**
*****************************************************************************/
//
// Qt OpenGL example: Texture
//
// File: main.cpp
//
// The main() function
//

#include <QApplication>
#include <QGLFormat>

#include "point.h"
#include "tree.h"
#include "glwinobj.h"

int main(int argc, char **argv)
{
	QApplication::setColorSpec(QApplication::CustomColor);
	QApplication app(argc,argv);
	
	if(!QGLFormat::hasOpenGL()) {
		qWarning( "This system has no OpenGL support. Exiting." );
		return -1;
	}

	// Create OpenGL format
	QGLFormat f;
	f.setDoubleBuffer(TRUE); f.setRgba(TRUE); f.setDepth(TRUE);
	QGLFormat::setDefaultFormat(f);
	GLObjectWindow* w = new GLObjectWindow;

	// set size...
	w->resize( 640, 480 );

	//w->resize( 1280, 1024 );
	w->show();

	// ... or go full screen
	//w->showFullScreen();
	int result = app.exec();
	delete w;
	return result;
}
