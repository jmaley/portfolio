/*
 * FILE: main.cpp
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */

#include <omp.h>
#include <iostream>
#include <iomanip>
#include <fstream>
#include <vector>
#include <string>
#include <cassert>
#include <cmath>
#include <cstdlib>
#include <cstring>
#include <algorithm>

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
#include "timer.h"
#include "kdtree.h"

// prototypes
int  main(int argc, char *argv[]);

int main(int argc, char *argv[]) {
    model_t		model;		// model (the world)
    std::ifstream	model_ifs;	// input file stream
    atd::timer_t	timer;

    if (argc != 2) {
        std::cerr << "Usage " << argv[0] << " <model_file>" << std::endl;
        return 1;
    }

    // seed random number generator
    srand((unsigned int)1337);

    // open file, read in model
    model_ifs.open(argv[1],std::ifstream::in);
    model_ifs >> model;
    model_ifs.close();

    // debugging
    std::cerr << model;

    // emit photons
    std::vector<photon_t *>			photons;
    std::vector<photon_t *>::iterator	pitr;
    std::ofstream				ofs;

    // shoot photons from lights
    model.shoot(photons);

    for (pitr = photons.begin(); pitr<photons.end(); pitr++) {
        (*pitr)->scale_pwr(1.0/photons.size());
    }

    kdtree_t<photon_t, photon_t*, photon_c> kdtree;
    kdtree.insert(photons, photon_t(vec_t(-9999.0, -9999.0, -9999.0)), photon_t(vec_t(9999.0, 9999.0, 9999.0)));

    int tid,ncores=omp_get_num_threads();
    int w=model.getpixel_w(), h=model.getpixel_h();
    int chunk;
    double 	wx,wy,wz=0.0;
    double 	ww=model.getworld_w(), wh=model.getworld_h();
    vec_t pos=model.getviewpoint();
    vec_t pix,dir;
    ray_t		*ray=NULL;
    rgb_t<double>	color;
    rgb_t<uchar>	*imgloc,*img=NULL;

    img = new rgb_t<uchar>[w*h];
    memset(img,0,3*w*h);

    // figure out how many cores we have available, then calculate chunk,
    // splitting up the work as evenly as possible
    #pragma omp parallel private(tid)
    {
        if ((tid = omp_get_thread_num())==0)
            ncores = omp_get_num_threads();
    }
    chunk = h/ncores;

    timer.start();
    
    #pragma omp parallel for \
        shared(model,w,h,ww,wh,wz,pos,img) \
        private(tid,wx,wy,pix,dir,ray,color,imgloc) \
        schedule(static,chunk)
    for (int y=h-1; y>=0; y--) {
        for (int x=0; x<w; x++) {
            wx = (double)x/(double)(w-1) * ww;
            wy = (double)y/(double)(h-1) * wh;

            // set pixel position vector (in world coordinates)
            pix = vec_t(wx,wy,wz);

            // compute the vector difference  v3 = v2 - v1
            dir = pix - pos;

            // our ray is now {pos, dir} (in world coordinates), normalize dir
            dir = dir.norm();

            // zero out color
            color.zero();

            // spawn new ray
            ray = new ray_t(pos,dir);

            // trace ray
            ray->trace(model,color,0, kdtree);

            // nuke this ray, we don't need it anymore, prevent memory leak
            delete ray;

            // where are we in the image (using old i*c + j)
            imgloc = img + y*w + x;

            // scale pixel by maxval, store at dereferenced image location
            for (int i = 0; i < 3; i++) (*imgloc)[i] = static_cast<uchar>(255.0 * color[i]);
        }
    }
    
    timer.end();

    std::cerr << "cores: " << ncores << ", ";
    std::cerr << "time: " << timer.elapsed_ms() << " ms" << std::endl;

    // output image
    std::cout << "P6 " << w << " " << h << " " << 255 << std::endl;
    for (int y=h-1; y>=0; y--) {
        for (int x=0; x<w; x++) {
            imgloc = img + y*w + x;
            std::cout.write((char *)imgloc,3);
        }
    }

    return 0;
}
