/* 
 * FILE: gltexobj.cpp
 * NAME: Joe Maley
 * DATE: Nov 20, 2011
 * Sec.: 2
 */

#include <iostream>
#include <iomanip>
#include <fstream>
#include <string>
#include <cmath>
#include <GL/gl.h>
#include <GL/glu.h>
#include <GL/glext.h>
#include <QImage>
#include <QTimer>
#include <QEvent>
#include <QString>
#include <QFileDialog>
#include <QMouseEvent>

#include "point.h"
#include "tree.h"
#include "gltexobj.h"

const int redrawWait = 50;

//const int redrawWait = 0;
GLTexobj::GLTexobj( QWidget* parent ) :
		QGLWidget( parent )
{
	// create a GLTexobj widget
	timer = new QTimer( this );
	connect( timer, SIGNAL(timeout()), SLOT(update()) );
	timer->setInterval(redrawWait);
	timer->setSingleShot(FALSE);
	timer->start();

	setMouseTracking(true);

	drawingBox = false;
	k = 3;
	radius = HUGE_VAL;
	currentQuery = 0;
}

GLTexobj::~GLTexobj()
{
	// release allocated resources
	makeCurrent();
}

void GLTexobj::initializeGL()
{
	glClearColor( 0.0, 0.0, 0.0, 0.0 );
	glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	glEnable(GL_BLEND);
}

void GLTexobj::resizeGL( int w, int h )
{
	// set up the OpenGL view port, matrix mode, etc.
	glViewport(0,0,w,h);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	gluOrtho2D(-w,w,-h,h);
	//gluPerspective(45.0,5.12/3.84,0.1,100.0);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void GLTexobj::paintGL()
{
	// draw mouse point: 
	glDrawBuffer(GL_BACK);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glColor4f(1.0, 1.0, 1.0, 1.0);
	glBegin(GL_QUADS);
		glVertex2f(x - 5, y - 5);
		glVertex2f(x + 5, y - 5);
		glVertex2f(x + 5, y + 5);
		glVertex2f(x - 5, y + 5);
	glEnd();

	if (!pts.empty())
	{
		glColor4f(1.0, 0.0, 0.0, 1.0);
		glPointSize(5.0);

		std::vector<point_t *>::iterator itr;
		for (itr = pts.begin(); itr != pts.end(); itr++)
		{
			glBegin(GL_POINTS);
			glVertex2f((**itr)[0],
                       (**itr)[1]);
			glEnd();
		}
	}

	kdtree.render();

	if (currentQuery == 1 && !kdtree.empty())
	{
		glColor4f(1.0, 1.0, 0.0, 1.0);
		glPointSize(7.0);

		glBegin(GL_POINTS);
		glVertex2f((*nearest)[0],
                   (*nearest)[1]);
		glEnd();
	}
	else if (currentQuery == 2 && !kdtree.empty())
	{
		GLUquadric* qobj;
		if (!qobj)
			qobj = gluNewQuadric();

		glPushMatrix();
		glTranslatef(query[0], query[1], 0.0);
		gluDisk(qobj, radius - 2, radius, 360, 1);
		glPopMatrix();


		glColor4f(1.0, 1.0, 0.0, 1.0);
		glPointSize(7.0);

		std::vector<point_t *>::iterator itr;
		for (itr = knearest.begin(); itr != knearest.end(); itr++)
		{
			glBegin(GL_POINTS);
			glVertex2f((**itr)[0],
                       (**itr)[1]);
			glEnd();
		}
	}
	else if (currentQuery == 3)
	{
		glColor4f(0.0, 1.0, 0.0, 0.5);
		glBegin(GL_QUADS);
			glVertex2f(x2, y2);
			glVertex2f(x1, y2);
			glVertex2f(x1, y1);
			glVertex2f(x2, y1);
		glEnd();

		glColor4f(1.0, 1.0, 0.0, 1.0);
		glPointSize(7.0);

		std::vector<point_t *>::iterator itr;
		for (itr = range.begin(); itr != range.end(); itr++)
		{
			glBegin(GL_POINTS);
			glVertex2f((**itr)[0],
                       (**itr)[1]);
			glEnd();
		}
	}
}

void GLTexobj::mapCoords(int ex, int ey)
{
	x = ex;
	y = height() - ey;

	// normalize coordinates (puts them in range [0,1])
	double dx = (double)x/width();
	double dy = (double)y/height();

	// scale coorindates by 2 then shift to the right by 1, scale again by w,h
	dx = width()*(2.0*dx - 1.0);
	dy = height()*(2.0*dy - 1.0);

	// set integer coordinates
	x = (int)dx;
	y = (int)dy;
}

void GLTexobj::mouseMoveEvent(QMouseEvent* e)
{
	//std::cerr << "(" << e->x() << "," << e->y() << ")" << std::endl;
	mapCoords(e->x(), e->y());

	if(drawingBox)
	{
		x2 = x;
		y2 = y;
	}

	e->accept();
	updateGL();
}

void GLTexobj::mousePressEvent(QMouseEvent* e)
{
	mapCoords(e->x(), e->y());

	if (e->button() == Qt::LeftButton)
	{
		pts.push_back(new point_t(x, y));

		currentQuery = 0;
	}
	else if(e->button() == Qt::MidButton && e->modifiers() == Qt::NoModifier)
	{
		radius = HUGE_VAL;

		query[0] = x;
		query[1] = y;

		//nearest = NULL;
		kdtree.nn(query, nearest, radius);

		currentQuery = 1;
	}
	else if(e->button() == Qt::MidButton && e->modifiers() == Qt::ShiftModifier)
	{
		radius = HUGE_VAL;

		query[0] = x;
		query[1] = y;

		knearest.clear();
		kdtree.knn(query, knearest, radius, k);

		currentQuery = 2;
	}
	else if(e->button() == Qt::RightButton)
	{
		drawingBox = true;
		x1 = x; y1 = y;
		x2 = x; y2 = y;

		range.clear();

		currentQuery = 3;
	}
	else
	{
		currentQuery = 0;
	}

	e->accept();
	updateGL();	
}

void GLTexobj::mouseReleaseEvent(QMouseEvent* e)
{
	mapCoords(e->x(), e->y());

	if(e->button() == Qt::RightButton)
	{
		x2 = x; y2 = y;
    	point_t min, max;
    	min[0] = x1 < x2 ? x1 : x2;  max[0] = x1 > x2 ? x1 : x2;
    	min[1] = y1 < y2 ? y1 : y2;  max[1] = y1 > y2 ? y1 : y2;

		drawingBox = false;
		kdtree.range(min, max, range);
	}

	e->accept();
	updateGL();
}

void GLTexobj::update()
{
	updateGL();
}

void GLTexobj::clear()
{
	pts.clear();
	range.clear();
	knearest.clear();
	kdtree.clear();
	currentQuery = 0;
	updateGL();
}

void GLTexobj::build()
{	
	kdtree.insert(pts, point_t(-width(),-height()),point_t(width(),height()));
	updateGL();
}
