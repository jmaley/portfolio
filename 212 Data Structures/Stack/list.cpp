/* 
 * FILE: list.cpp
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#include <iostream>

#include "list.h"
#include "pairs.h"

using namespace std;

template <typename T>
ostream &operator<<(ostream &s, const list_t<T> &rhs)
{
    return s;
}

template <typename T>
list_t<T>::list_t()
{
    sz = 0;
    head = new node_t;
    tail = new node_t;
    head->next = tail;
    tail->prev = head;
}

template <typename T>
list_t<T>::list_t(const list_t<T> &rhs)
{
    sz = 0;
    head = new node_t;
    tail = new node_t;
    head->next = tail;
    head->prev = head;
    *this = rhs;
}

template <typename T>
const list_t<T> &list_t<T>::operator=(const list_t<T> &rhs)
{
    if(this == &rhs)
        return *this;

    clear();

    for (const_iterator itr = rhs.begin(); itr != rhs.end(); ++itr)
    {
        push_back(*itr);
    }

    return *this;
}

template <typename T>
typename
list_t<T>::iterator list_t<T>::insert(list_t<T>::iterator itr, const T& rhs)
{
    node_t *p = itr.curp;

    sz++;
    return iterator(p->prev = p->prev->next = new node_t(rhs, p->prev, p));
}

template <typename T>
typename
list_t<T>::iterator list_t<T>::erase(list_t<T>::iterator itr)
{
    typename list_t<T>::node_t      *p = itr.curp;
    typename list_t<T>::iterator    ret(p->next);
    p->prev->next = p->next;
    p->next->prev = p->prev;

    delete p;
    sz--;

    return ret;
}

template <typename T>
typename
list_t<T>::iterator list_t<T>::erase(list_t<T>::iterator start, list_t<T>::iterator end)
{
    for(typename list_t<T>::iterator itr = start; itr != end; ) itr = erase(itr);

    return end;
}

// spaceializations //
template class list_t<int>;
template ostream& operator<<(ostream&, const list_t<int>&);
template class list_t<float>;
template ostream& operator<<(ostream&, const list_t<float>&);
template class list_t<double>;
template ostream& operator<<(ostream&, const list_t<double>&);
template class list_t<char>;
template ostream& operator<<(ostream&, const list_t<char>&);
template class list_t<pairs_t>;
template ostream& operator<<(ostream&, const list_t<pairs_t>&);

