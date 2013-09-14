/* 
 * FILE: glwinobj.cpp
 * NAME: Joe Maley
 * DATE: Nov 20, 2011
 * Sec.: 2
 */

#ifndef GLOBJWIN_H
#define GLOBJWIN_H

#include <QWidget>

class GLObjectWindow : public QWidget
{
	Q_OBJECT

public:
	GLObjectWindow( QWidget* parent = 0, Qt::WindowFlags f = 0 );
};
#endif
