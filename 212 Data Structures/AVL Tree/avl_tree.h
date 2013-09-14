/* 
 * FILE: avl_tree.h
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#ifndef AVL_TREE_H
#define AVL_TREE_H

using namespace std;

template <typename T> class tree_t;
template <typename T> ostream &operator<<(ostream &lhs, const tree_t<T> &rhs);

template <typename T>
class tree_t {
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
    friend ostream &operator<<(ostream &s, tree_t *rhs) { return(s << (*rhs)); };
    
private:
    struct node_t {
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

#endif
