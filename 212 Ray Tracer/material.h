/*
 * FILE: material.h
 * NAME: Joe Maley
 * DATE: Dec 6, 2011
 * Sec.: 2
 */

#ifndef MATERIAL_H
#define MATERIAL_H

#define MAT_COOKIE 32456123

class material_t {
public:
    material_t() :
        cookie(MAT_COOKIE),
        ambient(0.0,0.0,0.0),
        diffuse(0.0,0.0,0.0),
        specular(0.0,0.0,0.0),
        alpha(0.0),
        ior(1.0) { };

    material_t(const material_t &rhs) :
        cookie(rhs.cookie),
        name(rhs.name),
        ambient(rhs.ambient),
        diffuse(rhs.diffuse),
        specular(rhs.specular),
        alpha(0.0),
        ior(0.0) { };

    // destructors (default ok, no 'new' in constructor)
    ~material_t() { };

    // operators (incl. assignment operator)
    const material_t &operator=(const material_t &rhs) {
        if (this != &rhs) {
            cookie = rhs.cookie;
            name = rhs.name;
            ambient = rhs.ambient;
            diffuse = rhs.diffuse;
            specular = rhs.specular;
            alpha = rhs.alpha;
            ior = rhs.ior;
        }
        return *this;
    }

    friend std::ostream &operator<<(std::ostream &s, const material_t &rhs);
    friend std::ostream &operator<<(std::ostream &s, material_t *rhs) { return(s << (*rhs)); }

    friend std::istream &operator>>(std::istream &s, material_t &rhs);
    friend std::istream &operator>>(std::istream &s, material_t *rhs) { return(s >> (*rhs)); }

    int getcookie() { return cookie; }
    std::string	getname() { return name; }
    rgb_t<double>	getamb() { return ambient; }
    rgb_t<double>	getdiff() { return diffuse; }
    rgb_t<double>	getspec() { return specular; }
    double getalpha() { return alpha; }
    double getior() { return ior; }

private:
    int cookie;		// magic number
    std::string	name;		// material name
    rgb_t<double>	ambient;	// ambient color
    rgb_t<double>	diffuse;	// ambient color
    rgb_t<double>	specular;	// ambient color
    double alpha;		// transparency
    double ior;		// index of refraction
};

#endif
