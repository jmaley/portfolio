/* 
 * FILE: list.h
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#ifndef LIST_H
#define LIST_H

using namespace std;

template <typename T> class list_t;
template <typename T> ostream &operator<<(ostream &lhs, const list_t<T> &rhs);

template <typename T>
class list_t
{
private:
    struct node_t
    {
        T      data;
        node_t *prev;
        node_t *next;

        node_t(const T& d= T(), node_t *p = NULL, node_t *n = NULL) :
            data(d), prev(p), next(n) { }
    };

public:
    class const_iterator
    {
    public:
        const_iterator() : curp(NULL) { }
    
        const T &operator*() const
        {
            return retrieve();
        }

        // prefix ++:
        const_iterator &operator++()
        {
            curp = curp->next;
            return *this;
        }

        // prefix --;
        const_iterator &operator--()
        {
            curp = curp->prev;
            return *this;
        }

        // postfix ++:
        const_iterator operator++(int)
        {
            const_iterator old = *this;

            ++(*this);
            return old;
        }

        // postfix --;
        const_iterator operator--(int)
        {
            const_iterator old = *this;
            
            --(*this);
            return old;
        }

        bool operator==(const const_iterator &rhs) const
        {
            return curp == rhs.curp;
        }

        bool operator!=(const const_iterator &rhs) const
        {
            return !(curp == rhs.curp);
        }

    protected:
        node_t *curp;

        T &retrieve() const
        {
            return curp->data;
        }

        const_iterator (node_t *p) : curp(p) { }

        friend class list_t<T>;
    };

    class iterator : public const_iterator
    {
    public:
        iterator() { }

        T &operator*()
        {
            return iterator::retrieve();
        }

        const T &operator*() const
        {
            return const_iterator::operator*();
        }

        iterator& operator++()
        {
            iterator::curp = iterator::curp->next;
            return *this;
        }

        iterator &operator--()
        {
            iterator::curp = iterator::curp->prev;
            return *this;
        }


        iterator operator++(int)
        {
            iterator old = *this;

            ++(*this);
            return old;
        }

        iterator operator--(int)
        {
            iterator old = *this;
            --(*this);
            return old;
        }

    protected:
        iterator(node_t *p) : const_iterator(p) { }

        friend class list_t<T>;
    };

public:
    class const_reverse_iterator
    {
    public:
        const_reverse_iterator() : curp(NULL) { }
    
        const T &operator*() const
        {
            return retrieve();
        }

        // prefix ++:
        const_reverse_iterator &operator++()
        {
            curp = curp->prev;
            return *this;
        }

        // postfix ++:
        const_reverse_iterator operator++(int)
        {
            const_reverse_iterator old = *this;

            ++(*this);
            return old;
        }

        // prefix --;
        const_reverse_iterator &operator--()
        {
            curp = curp->next;
            return *this;
        }

        // postfix --;
        const_reverse_iterator operator--(int)
        {
            const_reverse_iterator old = *this;
            
            --(*this);
            return old;
        }

        bool operator==(const const_reverse_iterator &rhs) const
        {
            return curp == rhs.curp;
        }

        bool operator!=(const const_reverse_iterator &rhs) const
        {
            return !(curp == rhs.curp);
        }

    protected:
        node_t *curp;

        T &retrieve() const
        {
            return curp->data;
        }

        const_reverse_iterator (node_t *p) : curp(p) { }

        friend class list_t<T>;
    };

    class reverse_iterator : public const_reverse_iterator
    {
    public:
        reverse_iterator() { }

        T &operator*()
        {
            return reverse_iterator::retrieve();
        }

        const T &operator*() const
        {
            return const_reverse_iterator::operator*();
        }

        reverse_iterator& operator++()
        {
            reverse_iterator::curp = reverse_iterator::curp->prev;
            return *this;
        }

        reverse_iterator operator++(int)
        {
            reverse_iterator old = *this;

            ++(*this);
            return old;
        }

        // prefix --;
        reverse_iterator &operator--()
        {
            reverse_iterator::curp = reverse_iterator::curp->next;
            return *this;
        }

        // postfix --;
        reverse_iterator operator--(int)
        {
            reverse_iterator old = *this;
            
            --(*this);
            return old;
        }

    protected:
        reverse_iterator(node_t *p) : const_reverse_iterator(p) { }

        friend class list_t<T>;
    };

public:
    list_t();

    list_t(const list_t& rhs);

    ~list_t()
    {
        clear();
        delete head;
        delete tail;
    }

    friend std::ostream& operator<< <>(std::ostream& s, const list_t& rhs);
    friend std::ostream& operator<<(std::ostream& s, list_t *rhs)
    { return(s << (*rhs)); };

    const list_t &operator=(const list_t&);

    iterator begin()                { return iterator(head->next); }
    const_iterator begin() const    { return const_iterator(head->next); }
    iterator end()                  { return iterator(tail); }
    const_iterator end() const      { return const_iterator(tail); }

    reverse_iterator rbegin()                { return reverse_iterator(tail->prev); }
    const_reverse_iterator rbegin() const    { return const_reverse_iterator(tail->prev); }
    reverse_iterator rend()                  { return reverse_iterator(head); }
    const_reverse_iterator rend() const      { return const_reverse_iterator(head); }

    iterator insert(iterator, const T&);
    iterator erase(iterator);
    iterator erase(iterator, iterator);

    int         size() const            { return sz; }
    bool        empty() const           { return size() == 0; }
    void        clear()                 { while(!empty()) pop_front(); }

    T&          front()                 { return *begin(); }
    const T&    front() const           { return *begin(); }
    T&          back()                  { return *end(); }
    const T&    back() const            { return *end(); }
    iterator    push_front(const T& o)  { return insert(begin(), o); }
    iterator    push_back(const T& o)   { return insert(end(), o); }
    iterator    pop_front()             { return erase(begin()); }
    iterator    pop_back()              { return erase(--end()); }

private:
    int sz;
    node_t *head;
    node_t *tail;
};   

#endif
