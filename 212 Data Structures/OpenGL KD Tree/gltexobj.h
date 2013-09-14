/* 
 * FILE: gltexobj.h
 * NAME: Joe Maley
 * DATE: Nov 20, 2011
 * Sec.: 2
 */

#ifndef GLTEXOBJ_H
#define GLTEXOBJ_H

#include <QGLWidget>
#include <QImage>
#include <QMouseEvent>

#include "point.h"
#include "tree.h"

typedef enum { NVIDIA, ATI } vendor_t;

class GLTexobj : public QGLWidget
{
	Q_OBJECT

public:
	GLTexobj( QWidget* parent );
	~GLTexobj();

public slots:
	void mouseMoveEvent(QMouseEvent* e);
	void mousePressEvent(QMouseEvent* e);
	void mouseReleaseEvent(QMouseEvent* e);

	void clear();
	void build();

	void update();

protected:
	void initializeGL();
	void paintGL();
	void resizeGL(int w, int h);
	void mapCoords(int ex, int ey);

private:
	QTimer* timer;

	bool drawingBox;
	int k;
	double radius;
	int currentQuery;
	
	double x, y;											// mouse
	double x1, x2, y1, y2;									// range box
	std::vector<point_t *> pts; 							// 2d points
	kdtree_t<point_t, point_t *, PointAxisCompare> kdtree;	// kd tree
	point_t query;											// query point
	point_t *nearest;										// nearest point
	vector<point_t *> knearest;								// k-nearest list
	vector<point_t *> range;								// range list
};

#endif
