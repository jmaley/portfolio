/*
 * FILE: photon.h
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */


#ifndef PHOTON_H
#define PHOTON_H

#define PHOTON_BOUNCES 10

class photon_t : public ray_t {
public:
    photon_t() :
        ray_t(),
        pwr(100.0,100.0,100.0) { };

    photon_t(const vec_t &o) :
        ray_t(o,genrand_hemisphere(vec_t(0.0, -1.0, 0.0)),0.0),
        pwr(100.0,100.0,100.0) { };

    photon_t(const vec_t &o, bool type) :
        ray_t(o,genrand_hemisphere(vec_t(0.0, -1.0, 0.0)),0.0),
        pwr((type ? vec_t(100.0,100.0,100.0) : vec_t(1.0, 1.0, 1.0))) { };

    photon_t(const vec_t &o, const vec_t &d, double r=0.0) :
        ray_t(o,d,r),
        pwr(100.0,100.0,100.0) { };

    photon_t(const vec_t &o, const vec_t &d, const vec_t &p, double r=0.0) :
        ray_t(o,d,r),
        pwr(p) { };

    photon_t(const photon_t &rhs) :
        ray_t(rhs),
        pwr(rhs.pwr) { };

    friend std::istream &operator>>(std::istream &s, photon_t &rhs);
    friend std::istream &operator>>(std::istream &s, photon_t *rhs) { return(s >> *rhs); }

    friend std::ostream &operator<<(std::ostream &s, const photon_t &rhs) { return(s << rhs[0] << " " << rhs[1] << " " << rhs[2]); }
    friend std::ostream &operator<<(std::ostream &s, photon_t *rhs) { return(s << *rhs); }

    friend class ray_t;

    // operators (incl. assignment operator)
    const photon_t &operator=(const photon_t &rhs) {
        if (this != &rhs) {
            dis = rhs.dis;
            pos = rhs.pos;
            dir = rhs.dir;
            pwr = rhs.pwr;
        }
        return *this;
    }

    const double& operator[](int k) const { return(pos[k]); }
    double& operator[](int k) { return(pos[k]); }

    bool operator<(const photon_t &rhs) {
        for (int i = 0; i < 3; i++) if ((*this)[i] > rhs[i]) return false;
        return true;
    }
    
    bool operator>(const photon_t &rhs) {
        for (int i = 0; i < 3; i++) if ((*this)[i] < rhs[i]) return false;
        return true;
    }

    int dim() const	{ return 3; }
    bool caustic(model_t&,int);
    bool global(model_t&,int);

    double distance(const vec_t &rhs) {
        vec_t	diff = pos - rhs;
        return(sqrt(diff.dot(diff)));
    }
    
    double distance(const photon_t &rhs) {
        vec_t	diff = pos - rhs.pos;
        return(sqrt(diff.dot(diff)));
    }
    
    double distance(photon_t *rhs) { return distance(*rhs); }

    vec_t getpwr()	{ return pwr; }
    
    void scale_pwr(double s) {
        pwr = s * pwr;
    }

    // destructors (default ok, no 'new' in constructor)
    ~photon_t() { };

private:
    vec_t    pwr;	   // power

    double   genrand(double lo,double hi) { return( (double)(((double)rand()/(double)RAND_MAX)*hi + lo) ); }

    vec_t    genrand_hemisphere(const vec_t &normal);
};

class photon_c {
public:
    photon_c(int inaxis=0) : axis(inaxis) {};
    bool operator()(const photon_t &p1, const photon_t &p2) const { return(p1[axis] < p2[axis]); }
    bool operator()(photon_t * const & p1, photon_t * const & p2) const { return((*p1)[axis] < (*p2)[axis]); }

private:
    int axis;
};

#endif
