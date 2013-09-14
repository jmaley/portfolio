/* 
 * FILE: binheap.cpp
 * NAME: Joe Maley
 * DATE: Sep 27, 2011
 * Sec.: 2
 */

#include "binheap.h"

using namespace std;

// stream overload
template <typename T>
ostream &operator<<(ostream &s, const binheap_t<T> &rhs) {
    for (int i = 1; i <= rhs.currentSize; i++)
        s << i << ": " << rhs.array[i] << endl;

	return s;
}

// assignment constructor
template <typename T>
binheap_t<T>::binheap_t(int capacity)
: array(capacity), currentSize(0) { }

// copy constructor
template <typename T>
binheap_t<T>::binheap_t(const vector<T> &items)
: array(items.size() + 10), currentSize(items.size()) {
    for (int i = 0; i < (int)items.size(); i++) {
        array[i + 1] = items[i];
    }
    
    buildheap();
}

// size accessor
template <typename T>
int binheap_t<T>::size() const {
    return currentSize;    
}

// empty accessor
template <typename T>
bool binheap_t<T>::empty() {
    if (currentSize <= 0)
        return true;
    else
        return false;
}

// clear out the heap
template <typename T>
void binheap_t<T>::clear() {
    currentSize = 0;
}

// delete the minimum
template <typename T>
void binheap_t<T>::pop() {
    if (!empty())
    {
        array[1] = array[currentSize--];
        percolateDown(1);
    }
}

// find the minimum
template <typename T>
const T &binheap_t<T>::top() const {
    return array[1];
}

// insert
template <typename T>
void binheap_t<T>::insert(T &el) {
    if (currentSize == (int)array.size() - 1) {
        array.resize(array.size() * 2);
    }

    int hole = ++currentSize;
    for ( ; hole > 1 && el < array[hole / 2]; hole /= 2) {
        array[hole] = array[hole / 2];
    }
    
    array[hole] = el;
}

// decrement key at position p
template <typename T>
void binheap_t<T>::decKey(int p, T delta) {
    if (delta >= 0)
    {
        array[p] -= delta;
        T temp = array[p];
        for ( ; p > 1 && temp < array[p / 2]; p /= 2) {
            array[p] = array[p / 2];
        }
        
        array[p] = temp;
    }
    else {
        incKey(p, delta * -1);
    }
}

// increment key at position p
template <typename T>
void binheap_t<T>::incKey(int p, T delta) {
    if (delta >= 0)
    {
        array[p] += delta;
        percolateDown(p);
    }
    else {
        decKey(p, delta * -1);
    }
}

// percolate down
template <typename T>
void binheap_t<T>::percolateDown(int hole) {
    int child;
    T tmp = array[hole];
    
    for ( ; hole * 2 <= currentSize; hole = child)
    {
        child = hole * 2;

        if (child != currentSize && array[child + 1] < array[child]) {
            child++;
        }
        
        if (array[child] < tmp) {
            array[hole] = array[child];
        } else {
            break;
        }
    }

    array[hole] = tmp;
}

// build heap 
template <typename T>
void binheap_t<T>::buildheap() {
    for (int i = currentSize / 2; i > 0; i--)
        percolateDown(i);
}

/////////////////////////// specializations ////////////////////////////////////
template class binheap_t<float>;
template ostream & operator<<(ostream&, const binheap_t<float>&);
