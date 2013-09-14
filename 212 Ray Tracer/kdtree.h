/*
 * FILE: kdtree.h
 * NAME: Joe Maley
 * DATE: Nov 14, 2011
 * Sec.: 2
 */

#ifndef KDTREE_H
#define KDTREE_H

using namespace std;

template <typename T, typename P, typename C> class kdtree_t;
template <typename T, typename P, typename C> ostream &operator<<(ostream &lhs, const kdtree_t<T, P, C> &rhs);

template <typename T, typename P, typename C>
class kdtree_t {
public:
    kdtree_t();
    kdtree_t(const kdtree_t &rhs);
    ~kdtree_t();

    void insert(std::vector<P> &pts, const T &min, const T&max);
    void nn(T &query, P &nearest, double &radius);
    void knn(T &query, std::vector<P> &knearest, double &radius, int k);
    void range(const T &min, const T &max, std::vector<P> &ranges);

    void clear();

    friend ostream &operator<< <>(ostream &s, const kdtree_t &rhs);
    friend ostream &operator<<(ostream &s, kdtree_t *rhs) { return(s << (*rhs));
    };

    struct node_t {
        P data;
        T min, max;
        node_t *left, *right;
        int axis;

        node_t(const P &d = P(),
               const T &in = T(),
               const T &ix = T(),
               node_t *l = NULL,
               node_t *r = NULL,
               int a = 0) :
            data(d), min(in), max(ix),
            left(l), right(r), axis(a) { };
    };

private:

    node_t *root;

    node_t *insert(node_t *&, std::vector<P> &, const T &, const T &, int);
    void nn(node_t *&, T &, P &, double &);
    void knn(node_t *&, T &, std::vector<P> &, double &,int);
    void range(node_t *&, const T &, const T &, std::vector<P> &, int d);

    void clear(node_t *&);
    node_t *clone(node_t *) const;

    void print(ostream &s, node_t *t) const;
};

#endif
