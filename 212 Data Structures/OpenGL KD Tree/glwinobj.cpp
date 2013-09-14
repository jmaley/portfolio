/* 
 * FILE: glwinobj.cpp
 * NAME: Joe Maley
 * DATE: Nov 20, 2011
 * Sec.: 2
 */

#include <iostream>
#include <QApplication>
#include <QMenuBar>
#include <QMenu>
#include <QVBoxLayout>

#include "point.h"
#include "tree.h"
#include "glwinobj.h"
#include "gltexobj.h"

GLObjectWindow::GLObjectWindow( QWidget* parent, Qt::WindowFlags f ) :
QWidget( parent, f )
{
	// Create an OpenGL widget: (doubleBuffer | rgba | depth) set globally
	GLTexobj* c = new GLTexobj(this);
	std::cout << "doubleBuffer: " << c->format().doubleBuffer() << " "
		<< "rgba: " << c->format().rgba() << " "
		<< "depth: " << c->format().depth() << " "
		<< std::endl;

	// c->setMouseTracking(true);

	// create the file menu
	QMenu *file = new QMenu("File",this);
	//file->addAction("Open...",c,SLOT(imageOpen()),Qt::CTRL+Qt::Key_O);
	//file->addAction("Save As...",c,SLOT(imageSaveAs()),Qt::CTRL+Qt::Key_S);

	// doesn’t seem to be needed in Qt 4.6
	file->addAction("Quit",QApplication::instance(),SLOT(quit()),
	Qt::CTRL+Qt::Key_Q);

	// create the edit menu
	QMenu *edit = new QMenu("Edit",this);
	edit->addAction("Clear",c,SLOT(clear()),Qt::CTRL+Qt::Key_C);
	edit->addAction("Build",c,SLOT(build()),Qt::CTRL+Qt::Key_B);

	// create a menu bar
	QMenuBar *m = new QMenuBar(this);
	m->addSeparator();
	m->addMenu(file);
	m->addMenu(edit);

	// top level layout (with 0,0 border)
	QVBoxLayout* vlayout = new QVBoxLayout(this);

	// no border, no margin
	vlayout->setSpacing(0);
	vlayout->setMargin(0);
	
	// add menu bar and GL window
	vlayout->setMenuBar(m);
	vlayout->addWidget(c);
}
