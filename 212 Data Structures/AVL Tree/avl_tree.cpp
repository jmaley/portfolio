/* 
 * FILE: avl_tree.cpp
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#include <cstdlib>
#include <iostream>

#include "avl_tree.h"

using namespace std;

// default constructor:
template <typename T>
tree_t<T>::tree_t() {
    root = NULL;
}

// copy constructor:
template <typename T>
tree_t<T>::tree_t(const tree_t &rhs) {
    if(this != &rhs) {
        clear();
        root = clone(rhs.root);
	}
}

// destructor:
template <typename T>
tree_t<T>::~tree_t() {
    clear();
}

// operator =:
template <typename T>
const tree_t<T> &tree_t<T>::operator=(const tree_t<T> &rhs) {
    if(this != &rhs) {
        clear();
        root = clone(rhs.root);
	}

	return *this;
}

// in-order printing:
template <typename T>
void tree_t<T>::printTree(ostream &s, node_t *t) const {
    if(t != NULL) {
        printTree(s, t->left);
        s << t->element << "(" << t->bal << ")" << endl;
        printTree(s, t->right);
    }
}

// operator <<:
template <typename T>
ostream &operator<<(ostream &s, const tree_t<T> &rhs) {
    typename tree_t<T>::node_t *t = rhs.root;

    rhs.printTree(s, t);       
    
    return s;
}

// find min:
template <typename T>
const T &tree_t<T>::min() const {
    if(root != NULL) {
        node_t *t;
        t = min(root);
    
        return t->element;
    }
}

// find max:
template <typename T>
const T &tree_t<T>::max() const {
    if(root != NULL) {
        node_t *t;
        t = max(root);
    
        return t->element;
    }
}

// contains:
template <typename T>
bool tree_t<T>::contains(const T &x) const {
    return contains(x, root);
}

// clear:
template <typename T>
void tree_t<T>::clear() {
    clear(root);
}

// insert:
template <typename T>
void tree_t<T>::insert(const T &x) {
    insert(x, root);
}

// erase:
template <typename T>
void tree_t<T>::erase(const T &x) {
    erase(x, root);
}

// (private) contains:
template <typename T>
bool tree_t<T>::contains(const T &x, node_t *t) const {
    if (t == NULL) {
        return false;
    } else if (x < t->element) {
        return contains(x, t->left);
    } else if (x > t->element) {
        return contains(x, t->right);
    } else {
        return true;
    }
}

// (private) clear:
template <typename T>
void tree_t<T>::clear(node_t * &t) {
    if(t != NULL) {
        clear(t->left);
        clear(t->right);
        delete t;
    }
    
    t = NULL;
}

// (private) insert:
template <typename T>
int tree_t<T>::insert(const T &x, node_t *&t) {
    int dH = 0;
    
    if (t == NULL) {
        t = new node_t(x, NULL, NULL);
        dH = 1;
    } else if (x < t->element) {
        if (insert(x, t->left)) {
            t->bal--;
            if (t->bal == -1) {
                dH = 1;
            } else if (t->bal == -2) {
                if (t->left->bal == 1) {
                    rotate_right(t->left);
                    cerr << "double_";
                }

                rotate_left(t);
                cerr << "rotate_left ";
            }            
        }
    } else if (x > t->element) {
        if (insert(x, t->right)) {
            t->bal++;
            if (t->bal == 1) {
                dH = 1;
            } else if (t->bal == 2) {
                if (t->right->bal == -1) {
                    rotate_left(t->right);
                    cerr << "double_";
                }
                
                rotate_right(t);
                cerr << "rotate_right ";
            }
        }
    }

    return dH;
}

// (private) erase:
template <typename T>
int tree_t<T>::erase(const T &x, node_t *&t) {
    int dH = 0;

    if (t == NULL) {
        return 0;
    }
    
    if (x < t->element) {
        if (erase(x, t->left)) {
            t->bal++;
            if (t->bal == 0) {
                dH = 1;
            } else if (t->bal == 2) {
                if (t->right->bal == -1) {
                    rotate_left(t->right);
                    cerr << "double_";
                }
                
                rotate_right(t);
                cerr << "rotate_right ";

                if (t->bal == 0) {
                    dH = 1;
                }
            }
        }
    } else if (x > t->element) {
        if (erase(x, t->right)) {
            t->bal--;
            if (t->bal == 0) {
                dH = 1;
            } else if (t->bal == -2) {
                if (t->left->bal == 1) {
                    rotate_right(t->left);
                    cerr << "double_";
                }
                
                rotate_left(t);
                cerr << "rotate_left ";

                if(t->bal == 0) {
                    dH = 1;
                }
            }
        }
    } else if (t->left != NULL && t->right != NULL) {
        t->bal--;
        if (t->bal == 0) {
            dH = 1;
        }
        
        if (t->bal == -2) {
            if (t->left->bal == 1) {
                rotate_right(t->left);
                cerr << "double_";
            }
            
            rotate_left(t);
            cerr << "rotate_left ";

            if(t->bal == 0) {
                dH = 1;
            }
        }
    } else if(t->left == NULL || t->right == NULL) {
        node_t *old = t;
        t = (t->left != NULL) ? t->left : t->right;
        delete old;
        dH = 1;
    }

    return dH;
}

// (private) min:
template <typename T>
typename
tree_t<T>::node_t*tree_t<T>::min(node_t* t) const {
    if (t == NULL) {
        return NULL;
    }
    
    if (t->left == NULL) {
        return t;
    }
    
    return min(t->left);
}

// (private) max:
template <typename T>
typename
tree_t<T>::node_t* tree_t<T>::max(node_t *t) const {
    if(t == NULL) {
        return NULL;
    }
    
    if (t->right == NULL) {
        return t;
    }
    
    return max(t->right);
}

// (private) clone:
template <typename T>
typename
tree_t<T>::node_t* tree_t<T>::clone(node_t *t) const {
    if (t == NULL) {
        return NULL;
    }
    
    return new node_t(t->element,
                      clone(t->left),
                      clone(t->right));
}

// (private) rotate_left:
template <typename T>
void tree_t<T>::rotate_left(node_t *&t) {
    node_t *k = t;
    t = t->left;
    k->left = t->right;
    t->right = k;

    k->bal++;
    if (t->bal < 0) {
        k->bal -= t->bal;
    }
    
    t->bal++;
    if (k->bal > 0) {
        t->bal += k->bal;
    }
}

// (private) rotate_right:
template <typename T>
void tree_t<T>::rotate_right(node_t *&t) {
    node_t *k = t;
    t = t->right;
    k->right = t->left;
    t->left = k;

    k->bal--;
    if (t->bal > 0) {
        k->bal -= t->bal;
    }
    
    t->bal--;
    if (k->bal < 0) {
        t->bal += k->bal;
    }
}

template class tree_t<int>;
template ostream& operator<<(ostream&, const tree_t<int>&);
