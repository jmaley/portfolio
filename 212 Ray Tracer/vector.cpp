/*
 * FILE: vector.cpp
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */

#include <iostream>
#include <cmath>

#include "vector.h"

// compute the dot product of two vectors
double vec_t::dot(const vec_t &rhs) const {
    double s=0.0;

    for (int i = 0; i < 3; i++) s += vec[i] * rhs[i];

    return(s);
}

// compute the length of the vector v1
double vec_t::len() const {
    return(sqrt(dot(*this)));
}

// compute the dot product of two vectors
vec_t vec_t::norm() const {
    vec_t	result(*this);
    double length = len();

    for (int i = 0; i < 3; i++) result[i] /= length;

    return(result);
}

// compute the reflection vector: v = u - 2 (u dot n) n
vec_t vec_t::reflect(const vec_t &n) const {
    vec_t	u(*this);
    vec_t	result;

    // u - 2 (u dot n) n
    result = u - 2.0 * u.dot(n) * n;

    return result;
}

// compute the refraction vector:
//   v = (n_i/n_t)(u - (u . n)n) - sqrt(1-(n_i/n_t)^2(1 - (u . n)^2)n
vec_t vec_t::refract(const vec_t &n, double n_t) const {
    vec_t	u(*this);
    vec_t	result;
    double n_i = 1.000293;
    double nior = n_i/n_t;

    /* */
    // Shirley's solution
    double ndotu = u.dot(n);
    double d = 1.0 - ( nior*nior * (1.0 - ndotu*ndotu) );

    if (d < 0)  // TIR
        result = u.reflect(n);
    else
        result = (nior * (u - (ndotu * n))) - (sqrt(d) * n);

    return result;
    /* */

    /*
      // Glassner's solution
    	double ndotu = n.dot(-u);
    	double d = 1.0 + ( nior*nior * (ndotu*ndotu - 1.0) );

      if (d < 0)  // TIR
        result = u.reflect(n);
      else
        result = (nior * u) + (nior*ndotu - sqrt(d)) * n;

      return result;
    */

    /*
      // Wikipedia's solution
    	double ndotu = n.dot(-u);
    	double d = 1.0 - ( nior*nior * (1.0 - ndotu*ndotu) );

      if (d < 0)  // TIR
        result = u.reflect(n);
      else
        result = (nior * u) + (nior*ndotu - sqrt(d)) * n;

      return result;
    */
}
