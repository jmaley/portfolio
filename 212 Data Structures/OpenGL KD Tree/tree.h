/* 
 * FILE: tree.h
 * NAME: Joe Maley
 * DATE: Nov 14, 2011
 * Sec.: 2
 */

#ifndef TREE_H
#define TREE_H

using namespace std;

template <typename T, typename P, typename C> class kdtree_t;
template <typename T, typename P, typename C> ostream &operator<<(ostream &lhs, const kdtree_t<T, P, C> &rhs);

template <typename T, typename P, typename C>
class kdtree_t
{
public:
	kdtree_t();
	kdtree_t(const kdtree_t &rhs);
	~kdtree_t();

	void insert(std::vector<P> &pts, const T &min, const T&max);
	void nn(T &query, P &nearest, double &radius);
	void knn(T &query, std::vector<P> &knearest, double &radius, int k);
	void range(const T &min, const T &max, std::vector<P> &ranges);
	void render();

	void clear();

	bool empty() { return (root == NULL ? true : false); };

	friend ostream &operator<< <>(ostream &s, const kdtree_t &rhs);
	friend ostream &operator<<(ostream &s, kdtree_t *rhs)
	{ return(s << (*rhs)); };

	struct node_t
	{
		P data;
		T min, max;
		node_t *left, *right;
		int axis;
	
		node_t(const P &d = P(),
			const T &in = T(),
			const T &ix = T(),
			node_t *l = NULL,
			node_t *r = NULL,
			int a = 0) : \
			data(d), min(in), max(ix),
			left(l), right(r), axis(a) { };
	};

private:

	node_t		*root;

	node_t*		insert(node_t* &, std::vector<P> &, const T &, const T &, int);
	void		nn(node_t* &, T &, P &, double &);
	void		knn(node_t* &, T &, std::vector<P> &, double &,int);
	void		range(node_t* &, const T &, const T &, std::vector<P> &, int);
	void		render(node_t * &);

	void		clear(node_t* &);
	node_t*		clone(node_t* ) const;

	void		print(ostream &s, node_t *t) const;
};

/*
using namespace std;

template <typename T> class tree_t;
template <typename T> ostream &operator<<(ostream &lhs, const tree_t<T> &rhs);

template <typename T>
class tree_t
{
public:
    tree_t();
    tree_t(const tree_t &rhs);
   ~tree_t();

    const T &min() const;
    const T &max() const;
    bool contains(const T &x) const;
    bool isEmpty() const;

    void clear();
    void insert(const T &x);
    void erase(const T &x);

    const tree_t &operator=(const tree_t &rhs);

    friend ostream &operator<< <>(ostream &s, const tree_t &rhs);
    friend ostream &operator<<(ostream &s, tree_t *rhs)
    { return(s << (*rhs)); };
    
private:
    struct node_t
    {
        T element;
        node_t *left;
        node_t *right;
        int bal;

        node_t(const T &tmp_element, node_t *lt, node_t *rt, int b = 0)
               : element(tmp_element), left(lt), right(rt), bal(b) { }
    };

    node_t *root;

    bool    contains(const T&, node_t *) const;

    void    clear(node_t *&);
    int     insert(const T &x, node_t *&t);
    int     erase(const T &x, node_t *&t);

    node_t* min(node_t *t) const;
    node_t* max(node_t *t) const;
    node_t* clone(node_t *t) const;

    void    rotate_left(node_t *&t);
    void    rotate_right(node_t *&t);

    void    printTree(ostream &s, node_t *t) const;
    int     height(node_t *t);
};
*/

#endif
