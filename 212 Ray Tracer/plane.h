/*
 * FILE: plane.h
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */


#ifndef PLANE_H
#define PLANE_H

class plane_t : public object_t {
public:
    plane_t(std::string token) :
        object_t(token),
        ndotq(0.0) { };

    plane_t(const plane_t &rhs) :
        object_t(rhs),
        normal(rhs.normal),
        point(rhs.point),
        ndotq(rhs.ndotq) { };

    // destructors (default not ok)
    ~plane_t() { };

    // operators (incl. assignment operator)
    const plane_t &operator=(const plane_t &rhs) {
        if (this != &rhs) {
            cookie = rhs.cookie;
            type = rhs.type;
            name = rhs.name;
            material = rhs.material;
            normal = rhs.normal;
            point = rhs.point;
            ndotq = rhs.ndotq;
        }
        return *this;
    }

    std::ostream &put(std::ostream &s) const;
    std::istream &get(std::istream &s);

    double hits(const vec_t&, const vec_t&, vec_t&, vec_t&);

private:
    vec_t normal;
    vec_t point;
    double ndotq;
};

#endif
