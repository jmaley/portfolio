/*
 * FILE: photon.cpp
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */


#include <iostream>
#include <iomanip>
#include <fstream>
#include <string>
#include <vector>
#include <cassert>
#include <cstdlib>
#include <cmath>

#include "vector.h"
#include "pixel.h"
#include "camera.h"
#include "light.h"
#include "material.h"
#include "object.h"
#include "list.h"
#include "plane.h"
#include "model.h"
#include "ray.h"
#include "photon.h"

std::istream &operator>>(std::istream &s, photon_t &rhs) {
    int	i = 0;
    char	c,n;
    double f;

    do {
        s >> f;
        rhs[i] = f;
        i++;
    } while (s.get(c) && (c != '\n') && ((n = s.peek()) != '\n') && (i < 3) );
}

bool photon_t::caustic(model_t&		model,
                       int 	bounce) {
    // (static) object properties
    object_t			*obj=NULL;
    material_t			*mat;
    vec_t 		hit, N;

    // transparency variables
    double 			alpha=0.0,ior;
    double 			fp(0.0);	// fade power
    double 			fd(1.0);	// fade distance
    double 			fa;		// fade attentuation

    if (bounce > PHOTON_BOUNCES) return false;

    // get closest object, if any
    if (!(obj = model.find_closest(pos,dir,dis,hit,N)) || dis > MAX_DIST)
        return false;

    // if hit distance valid, check for transmission
    if (dis > 0) {

        // get object material properties
        if ((mat = model.getmaterial(obj->getmaterial())) != NULL) {
            alpha = mat->getalpha();
            ior = mat->getior();
        }

        // transmission, see: http://www.cs.utah.edu/~shirley/books/fcg2/rt.pdf
        if (alpha > 0) {

            // move this photon along by setting its dir and dis
            pos = hit;
            dir = dir.dot(N) < 0 ? dir.refract(N,ior) :     // ray in
                  dir.refract(-N,1.0/ior); // ray out

            // power attentuation due to transmission
            if (fp < 1000.0)  fa = 1.0 / (1.0 + pow(dis / fd,fp));
            else             fa = exp(-dis/fd);

            pwr = fa * pwr;

            // trace the transmission
            return(caustic(model,++bounce));
        } else {
            // set photon to current location
            pos = hit;

            // stick photon
            //return true;
            return bounce ? true : false;
        }
    }
}

bool photon_t::global(model_t&		model,
                      int 	bounce) {
    // (static) object properties
    object_t			*obj=NULL;
    material_t			*mat;
    rgb_t<double>			ambient, diffuse, specular;
    double 			alpha=0.0,ior;
    double 			Kd,Ks;
    vec_t 		hit, N;

    double 			roulette = genrand(0.0,1.0);

    if (bounce > PHOTON_BOUNCES) return false;

    // get closest object, if any
    if (!(obj = model.find_closest(pos,dir,dis,hit,N)) || dis > MAX_DIST)
        return false;

    // if hit distance valid, compute color at surface
    if (dis > 0) {

        // get object material properties
        if ((mat = model.getmaterial(obj->getmaterial())) != NULL) {
            ambient = mat->getamb();
            diffuse = mat->getdiff();
            specular = mat->getspec();
            alpha = mat->getalpha();
            ior = mat->getior();
        }

        Kd = (diffuse[0] + diffuse[1] + diffuse[2])/3.0;
        Ks = (specular[0] + specular[1] + specular[2])/3.0;

        // diffuse reflection
        if ( (0.0 < roulette) && (roulette < Kd) ) {
            pos = hit;
            dir = genrand_hemisphere(N);

            return(global(model,++bounce));
        }

        // specular reflection
        else if ( (Kd < roulette) && (roulette < Kd+Ks) ) {
            pos = hit;
            dir = dir.reflect(N);

            return(global(model,++bounce));
        }

        // absorb
        else if ( (Kd+Ks < roulette) && (roulette < 1.0) ) {
            pos = hit;
            return true;
        }

        // shouldn't happen
        else
            return false;

    }
}

vec_t photon_t::genrand_hemisphere(const vec_t &normal) {
    double azimuth = genrand(0.0,2.0*M_PI);
    double elevation = genrand(0.0,2.0*M_PI);

    double sinA = sin(azimuth), sinE = sin(elevation);
    double cosA = cos(azimuth), cosE = cos(elevation);

    vec_t	dir, vup, out;

    dir[0] = -sinA*cosE;
    vup[0] =  sinA*sinE;
    dir[1] =  sinE;
    vup[1] =  cosE;
    dir[2] =  cosA*cosE;
    vup[2] = -cosA*sinE;

    out = (dir + vup).norm();

    //return(out);
    return (out.dot(normal) >= 0) ? out : -out;
}
