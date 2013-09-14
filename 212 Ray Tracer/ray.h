/*
 * FILE: ray.h
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */

#ifndef RAY_H
#define RAY_H

#define MAX_DIST 100
#define RAY_BOUNCES 5

// forward declaration
template <typename T, typename P, typename C>
class kdtree_t;
class photon_t;
class photon_c;

class ray_t {
public:
    ray_t() :
        dis(0.0),
        pos(0.0,0.0,0.0),
        dir(0.0,0.0,0.0) { };

    ray_t(const vec_t &o, const vec_t &d, double r=0.0) :
        dis(r),
        pos(o),
        dir(d) { };

    ray_t(const ray_t &rhs) :
        dis(rhs.dis),
        pos(rhs.pos),
        dir(rhs.dir) { };

    // operators (incl. assignment operator)
    const ray_t &operator=(const ray_t &rhs) {
        if (this != &rhs) {
            dis = rhs.dis;
            pos = rhs.pos;
            dir = rhs.dir;
        }
        return *this;
    }

    void trace(model_t&,rgb_t<double>&,int,kdtree_t<photon_t, photon_t*, photon_c>&);

    // destructors (default ok, no 'new' in constructor)
    ~ray_t() { };

protected:
    double   dis;	   // distance
    vec_t    pos;	   // position
    vec_t    dir;	   // direction
};

#endif
