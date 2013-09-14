/* 
 * FILE: pairs.cpp
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#include <iostream>
#include "pairs.h"

std::ostream &operator<<(std::ostream &s, const pairs_t &rhs)
{
    s << "('" << rhs.c << "'," << rhs.x << ")";

    return s;
}

pairs_t::pairs_t(const pairs_t &rhs)
{
    c = rhs.c;
    x = rhs.x;
}

const pairs_t &pairs_t::operator=(const pairs_t &rhs)
{
    if (this == &rhs)
        return *this;

    c = rhs.c;
    x = rhs.x;

    return *this;
}

bool pairs_t::operator<(const pairs_t &rhs)
{
    return c < rhs.c;
}

bool pairs_t::operator>(const pairs_t &rhs)
{
    return c > rhs.c;
}
