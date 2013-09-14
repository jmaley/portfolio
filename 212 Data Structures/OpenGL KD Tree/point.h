/* 
 * FILE: point.h
 * NAME: Joe Maley
 * DATE: Nov 14, 2011
 * Sec.: 2
 */

#ifndef POINT_H
#define POINT_H

#include <iostream>

class point_t
{
public:
	point_t() : x(0.0), y(0.0) { };
	point_t(double a, double b) : x(a), y(b) { };

	double distance(point_t *&rhs);

	int dim() const { return 2; };

	const double &operator[](int k) const { return k == 0 ? x : y; };
	double &operator[](int k) { return k == 0 ? x : y; };
	//double &operator[](int k) const { return (k == 0 ? x : y; };

	//double &operator[](int k) { return x; };
	//const double &operator[](int k) const { return x; };

	friend std::ostream& operator<<(std::ostream &s, point_t &rhs);
	friend std::ostream& operator<<(std::ostream &s, point_t *rhs)
	{ return(s << (*rhs)); };

	friend std::istream& operator>>(std::istream& s, point_t& rhs);
	friend std::istream& operator>>(std::istream& s, point_t *rhs)
	{ return(s >> (*rhs)); }

private:
	double x, y;
};

class PointAxisCompare
{
public:
	PointAxisCompare(int inaxis = 0)
		: axis(inaxis) { };

	bool operator()(const point_t &p1, const point_t &p2) const
		{ return (p1[axis] < p2[axis]); }
	
	bool operator()(point_t* const &p1, point_t* const &p2) const
		{ return ((*p1)[axis] < (*p2)[axis]); };

private:
	int axis;
};

#endif
