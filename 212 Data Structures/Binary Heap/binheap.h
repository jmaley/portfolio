/* 
 * FILE: binheap.h
 * NAME: Joe Maley
 * DATE: Sep 27, 2011
 * Sec.: 2
 */

#ifndef BINHEAP_H
#define BINHEAP_H

#include <iostream>
#include <vector>

template <typename T> class binheap_t;
template <typename T> std::ostream &operator<<(std::ostream&, const binheap_t<T>&);

template <typename T>
class binheap_t {
public:
    binheap_t(int capacity = 100);
    binheap_t(const std::vector<T> &items);

    int     size() const;               // return size of heap
    bool    empty();                    // return true if empty
    void    clear();                    // clear out the heap, make empty
    void    pop();                      // alias for deleteMin
    const T &top() const;               // alias for findMin
    void    insert(T& el);              // insert element
    void    decKey(int p, T delta);     // decrement key at position p
    void    incKey(int p, T delta);     // increment key at position p

	friend std::ostream& operator<< <>(std::ostream &s, const binheap_t &rhs);
	friend std::ostream& operator<<(std::ostream &s, binheap_t *rhs)
	{ return(s << (*rhs)); }

private:
    std::vector<T>      array;          // the heap array
    int                 currentSize;    // number of elements in heap

    void    percolateDown(int hole);    // used by pop()
    void    buildheap();                // only needed for constructor
};

#endif
