/*
 * FILE: ray.cpp
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
#include <cmath>
#include <cstdlib>

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
#include "kdtree.h"

void ray_t::trace(model_t&		model,
                  rgb_t<double>&	color,
                  int 	bounce,
                  kdtree_t<photon_t, photon_t*, photon_c> &kdtree) {
    // (static) object properties
    object_t			*obj=NULL;
    rgb_t<double>			ambient, diffuse, specular;
    material_t			*mat;
    vec_t 		hit, N;

    // light and illumination model variables
    light_t				*lgt=NULL;
    vec_t 		L,V,R,H;
    rgb_t<double>			I_d, I_s;
    double 			r,ndotl=0.0,n=32.0;
    double 			alpha=0.0,ior;
    list_t<light_t *>::iterator	litr;

    if (bounce > RAY_BOUNCES) return;

    // get closest object, if any
    if (!(obj = model.find_closest(pos,dir,dis,hit,N)) || dis > MAX_DIST)
        return;

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

        // ambient color
        color += 1.0/dis * ambient;			// ambient scaled by ray dist

        // clamp resultant color
        color.clamp(0.0,1.0);

        // view direction (direction from hit point to camera)
        V = -dir;				// dir to camera

        // diffuse component from each light...
        if (!diffuse.iszero()) {
            for (litr = model.lgts.begin(); litr != model.lgts.end(); litr++) {
                // pointer to light
                lgt = (light_t *)*litr;

                // light direction and distance
                L = lgt->getlocation() - hit;
                r = L.len();				// distance to light
                L = L.norm();				// dir to light

                // angle with light
                ndotl = N.dot(L);

                // check visibility wrt light
                if (0.0 < ndotl && ndotl < 1.0) {
                    // light color scaled by N . L
                    I_d = 1.0/r * ndotl * lgt->getcolor();

                    // add in diffuse contribution from light scaled by material property
                    color += (I_d * diffuse);
                }
            }

            // clamp resultant color
            color.clamp(0.0,1.0);
        }

        // reflection
        if (!specular.iszero()) {
            rgb_t<double>	refcolor;
            vec_t r = dir.reflect(N);
            ray_t		*reflection = new ray_t(hit,r,dis);

            // trace the reflection
            //reflection->trace(model,refcolor,++bounce);
            reflection->trace(model,refcolor,bounce+1, kdtree);
            delete reflection;

            // composite surface color: blending baesd on surface properties
            // (note that diffuse + specular should add up to 1)
            color  = (diffuse * color) + (specular * refcolor);

            // clamp resultant color
            color.clamp(0.0,1.0);
        }

        // transmission, see: http://www.cs.utah.edu/~shirley/books/fcg2/rt.pdf
        if (alpha > 0) {
            rgb_t<double>	refcolor,trncolor;
            vec_t r = dir.reflect(N);
            vec_t t = dir.dot(N)<0 ? dir.refract(N,ior) :     // ray in
                            dir.refract(-N,1.0/ior); // ray out
            double 	c = dir.dot(N)<0 ? N.dot(-dir) : N.dot(t);
            ray_t		*reflection = new ray_t(hit,r,dis);
            ray_t		*transmission = new ray_t(hit,t,dis);
            double 	R,R_0 = ((ior-1.0)*(ior-1.0))/((ior+1.0)*(ior+1.0));

            // trace the reflection
            //reflection->trace(model,refcolor,++bounce);
            reflection->trace(model,refcolor,bounce+1, kdtree);
            delete reflection;

            // trace the transmission
            //transmission->trace(model,trncolor,++bounce);
            transmission->trace(model,trncolor,bounce+1, kdtree);
            delete transmission;

            // Schlick's approximation to the Fresnel equations
            R = R_0 + (1.0 - R_0)*pow(1.0 - c,5.0);

            // composite surface color: blending baesd on surface properties
            // (note that diffuse + specular should add up to 1)
            //color  = ((1.0 - alpha) * color) + (alpha * trncolor);
            color  = ((1.0 - alpha) * color) +
                     (alpha * ((1.0 - R) * trncolor + (R * refcolor)));

            // clamp resultant color
            color.clamp(0.0,1.0);
        }

        // specular highlights
        if (!specular.iszero()) {

            // add in specular highlight from each light...
            for (litr = model.lgts.begin(); litr != model.lgts.end(); litr++) {
                // pointer to light
                lgt = (light_t *)*litr;

                // light direction and distance
                L = lgt->getlocation() - hit;
                r = L.len();				// distance to light
                L = L.norm();				// dir to light

                // angle with light
                ndotl = N.dot(L);

                // check visibility wrt light
                if (0.0 < ndotl && ndotl < 1.0) {
                    // specular reflection direction (and bisector)
                    R = L.reflect(N);
                    H = 0.5 * (L + V);

                    // light color scaled by (R . V)^n
                    I_s = 1.0/r * pow(R.dot(V),n) * lgt->getcolor();

                    // add in specular contribution from light scaled by material property
                    color += (I_s * specular);
                }

                // clamp resultant color
                color.clamp(0.0,1.0);
            }
        }

        photon_t *query = new photon_t(hit, vec_t(0.0, 0.0, 0.0), vec_t(0.0, 0.0, 0.0));
        std::vector<photon_t*> knearest;
        double flux_radius(999999.0);

        kdtree.knn(*query, knearest, flux_radius, 10);


        std::vector<photon_t *>::iterator pitr;
        rgb_t<double> flux;
        for (pitr = knearest.begin(); pitr < knearest.end(); pitr++)
        {
            V = -dir;
            if (V.dot(N) > 0)
            {
                flux[0] += ((*pitr)->getpwr())[0];
                flux[1] += ((*pitr)->getpwr())[1];
                flux[2] += ((*pitr)->getpwr())[2];
            }
        }

        flux *= (1 / (3.14 * pow(flux_radius, 2)));

        color += flux;

        // clamp resultant color
        color.clamp(0.0,1.0);
    }
}
