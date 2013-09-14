/*
 * FILE: kdtree.cpp
 * NAME: Joe Maley
 * DATE: Nov 14, 2011
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
#include "photon.h"

using namespace std;

// default constructor:
template <typename T, typename P, typename C>
kdtree_t<T, P, C>::kdtree_t() {
    root = NULL;
}

// copy constructor:
template <typename T, typename P, typename C>
kdtree_t<T, P, C>::kdtree_t(const kdtree_t &rhs) {
    if (this != &rhs) {
        clear();
        root = clone(rhs.root);
    }
}

// destructor:
template <typename T, typename P, typename C>
kdtree_t<T, P, C>::~kdtree_t() {
    clear();
}

// (public) insert:
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::insert(std::vector<P> &pts, const T &min, const T&max) {
    root = insert(root, pts, min, max, 0);
}

// (public) nearest-neighbor query
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::nn(T &query, P &nearest, double &radius) {
    nn(root, query, nearest, radius);
}

// (public) k-nearest-neighbor query
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::knn(T &query, std::vector<P> &knearest, double &radius, int k) {
    knn(root, query, knearest, radius, k);
}

// (public) range
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::range(const T &min, const T &max, std::vector<P> &ranges) {
    range(root, min, max, ranges, 0);
}

// (public) clear
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::clear() {
    clear(root);
}

// in-order printing:
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::print(ostream &s, node_t *t) const {
    if (t != NULL) {
        print(s, t->left);
        s << t->data << endl;
        print(s, t->right);
    }
}

// operator <<:
template <typename T, typename P, typename C>
ostream &operator<<(ostream &s, const kdtree_t<T, P, C> &rhs) {
    typename kdtree_t<T, P, C>::node_t *t = rhs.root;
    rhs.print(s, t);
    return s;
}

// (private) insert:
template <typename T, typename P, typename C>
typename kdtree_t<T, P, C>::node_t *kdtree_t<T, P, C>::insert(node_t *&t,
        std::vector<P> &pts,
        const T &min,
        const T &max,
        int d) {
    int axis = pts.empty() ? 0 : d % pts[0]->dim();
    int m = 0;

    P median;
    T _min, _max;
    std::vector<P> left, right;
    typename std::vector<P>::iterator itr;

    if (pts.empty())
        return NULL;

    sort(pts.begin(), pts.end(), C(axis));

    m = pts.size() / 2;

    for (int i = 0; i < (int)pts.size(); i++) {
        if (i < m) left.push_back(pts[i]);
        else if (i > m) right.push_back(pts[i]);
        else median = pts[m];
    }

    node_t *node = new node_t(median, min, max, NULL, NULL, axis);

    _min = min;
    _max = max;
    _max[axis] = (*median)[axis];
    node->left = insert(node, left, _min, _max, d + 1);

    _min = min;
    _max = max;
    _min[axis] = (*median)[axis];
    node->right = insert(node, right, _min, _max, d + 1);

    return node;
}

// (private) nearest-neighbor query
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::nn(node_t *&t,
                           T &query,
                           P &nearest,
                           double &radius) {
    if (t == NULL)
        return;

    double dist = query.distance(t->data);

    if (dist < radius) {
        radius = dist;
        nearest = t->data;
    }

    int axis = t->axis;
    if (query[axis] <= (*t->data)[axis]) {
        nn(t->left, query, nearest, radius);

        if (query[axis] + radius > (*t->data)[axis])
            nn(t->right, query, nearest, radius);

    } else {
        nn(t->right, query, nearest, radius);

        if (query[axis] - radius <= (*t->data)[axis])
            nn(t->left, query, nearest, radius);
    }
}

// (private) k-nearest-neighbor query
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::knn(node_t *&t,
                            T &query,
                            std::vector<P> &knearest,
                            double &radius,
                            int k) {
    if (t == NULL)
        return;

    typename std::vector<P>::iterator pitr;

    double dist = query.distance(t->data);

    if ((int)knearest.size() < k) {
        if (knearest.empty() || dist > query.distance(knearest.back()))
        {
            knearest.push_back(t->data);
        }
        else
        {
            for (pitr = knearest.begin(); pitr != knearest.end(); pitr++)
            {
                if (dist < query.distance(*pitr))
                {
                    knearest.insert(pitr, 1, t->data);
                    break;
                }
            }
        }
    }
    else {
        for (pitr = knearest.begin(); pitr != knearest.end(); pitr++)
        {
            if (dist < query.distance(*pitr))
            {
                knearest.insert(pitr, 1, t->data);
                break;
            }
        }

        if ((int)knearest.size() > k)
            knearest.pop_back();

        radius = query.distance(knearest.back());
    }

    int axis = t->axis;
    if (query[axis] <= (*t->data)[axis]) {
        knn(t->left, query, knearest, radius, k);

        if (query[axis] + radius > (*t->data)[axis])
            knn(t->right, query, knearest, radius, k);
    }
    else {
        knn(t->right, query, knearest, radius, k);

        if (query[axis] - radius <= (*t->data)[axis])
            knn(t->left, query, knearest, radius, k);
    }
}

// (private) range
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::range(node_t *&t,
                              const T &min,
                              const T &max,
                              std::vector<P> &ranges, int d) {
    if (t == NULL)
        return;

    int axis = ranges.empty() ? 0 : d % ranges[0]->dim();

    // 2D HARDCODED
    if (min[0] <= (*t->data)[0] && max[0] >= (*t->data)[0] &&
            min[1] <= (*t->data)[1] && max[1] >= (*t->data)[1])
        ranges.push_back(t->data);

    if (min[axis] <= (*t->data)[axis])
        range(t->left, min, max, ranges, axis + 1);
    if (max[axis] >= (*t->data)[axis])
        range(t->right, min, max, ranges, axis + 1);
}

// (private) clear:
template <typename T, typename P, typename C>
void kdtree_t<T, P, C>::clear(node_t * &t) {
    if (t != NULL) {
        clear(t->left);
        clear(t->right);
        delete t;
    }
    
    t = NULL;
}

// (private) clone:
template <typename T, typename P, typename C>
typename
kdtree_t<T, P, C>::node_t *kdtree_t<T, P, C>::clone(node_t *t) const {
    if (t == NULL)
        return NULL;

    return new node_t(t->data,
                      t->min,
                      t->max,
                      t->left,
                      t->right,
                      t->axis);
}

template class kdtree_t<photon_t, photon_t*, photon_c>;
template ostream &operator<<(ostream &, const kdtree_t<photon_t, photon_t*, photon_c>&);
