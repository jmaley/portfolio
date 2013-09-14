/*
 * FILE: sphere.h
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */

#ifndef SPHERE_H
#define SPHERE_H

class sphere_t : public object_t {
public:
    sphere_t(std::string token) :
        object_t(token),
        radius(0.0) { };

    sphere_t(const sphere_t &rhs) :
        object_t(rhs),
        center(rhs.center),
        radius(rhs.radius) { };

    // destructors (default not ok)
    ~sphere_t() { };

    // operators (incl. assignment operator)
    const sphere_t &operator=(const sphere_t &rhs) {
        if (this != &rhs) {
            cookie = rhs.cookie;
            type = rhs.type;
            name = rhs.name;
            material = rhs.material;
            center = rhs.center;
            radius = rhs.radius;
        }
        return *this;
    }

    std::ostream &put(std::ostream &s) const;
    std::istream &get(std::istream &s);

    double hits(const vec_t&, const vec_t&, vec_t&, vec_t&);
    vec_t getcenter() { return center;
    }

private:
    vec_t	 center;
    double radius;
};

#endif
