/* 
 * FILE: pairs.h
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#ifndef PAIRS_H
#define PAIRS_H

class pairs_t
{
    public:
    pairs_t(float ix = 0.0, char ic = 'a') : x(ix), c(ic) { }
    pairs_t(const pairs_t &rhs);

    friend std::ostream &operator<<(std::ostream &s, const pairs_t &rhs);
    friend std::ostream &operator<<(std::ostream &s, pairs_t *rhs)
        { return (s << (*rhs)); }

    const pairs_t &operator=(const pairs_t &);

    bool operator<(const pairs_t &rhs);
    bool operator>(const pairs_t &rhs);

    char  getc() { return c; }
    float getx() { return x; }

    private:
    float x;
    char  c;
};

#endif
