/* 
 * FILE: point.cpp
 * NAME: Joe Maley
 * DATE: Nov 14, 2011
 * Sec.: 2
 */

#include <cstdlib>
#include <vector>
#include <iostream>
#include <iomanip>
#include <cmath>

#include "point.h"

double point_t::distance(point_t *&rhs)
{
	double xdiff, ydiff;

	xdiff = x - rhs->x;
	ydiff = y - rhs->y;

	return sqrt( pow(xdiff, 2) + pow(ydiff, 2) );
}

std::ostream &operator<<(std::ostream &s, point_t &rhs)
{
	s << std::showpoint << std::setprecision(5) << rhs.x << ", " << rhs.y;

	return s;
}

std::istream &operator>>(std::istream& s, point_t& rhs)
{
	s >> rhs.x;
	s >> rhs.y;

	return s;
}
