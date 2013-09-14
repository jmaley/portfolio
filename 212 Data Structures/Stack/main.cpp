/* 
 * FILE: main.cpp
 * NAME: Joe Maley
 * DATE: Oct 11, 2011
 * Sec.: 2
 */

#include <iostream>
#include <sstream>
#include <string>
#include <list>
#include <ctype.h>

#include "list.h"

int main();
int postfix(const char *exp);

using namespace std;

int main()
{
    int     operand;
    char    c, op;

    list_t<char>    stack;
    ostringstream   os;

    while ((c = cin.get()))
    {
        if (isdigit(c))
        {
            cin.putback(c);
            cin >> operand;
            os << operand << " ";
        }
        else
        {
            switch(c)
            {
                case -1:
                    return 0;
                case '(':
                    stack.push_front(c);
                    break;
                case ')':
                    while (!stack.empty() && stack.front() != '(')
                    {
                        op = stack.front();
                        stack.pop_front();
                        os << op << " ";
                    }

                    stack.pop_front();
                    break;
                case '*':
                    while (!stack.empty() && (stack.front() == '*' || stack.front() == '/'))
                    {
                        if(stack.front() == '(') break;

                        op = stack.front();
                        stack.pop_front();
                        os << op << " ";
                    }
                    stack.push_front(c);
                    break; 
                case '/':
                    while (!stack.empty() && (stack.front() == '*' || stack.front() == '/'))
                    {
                        if(stack.front() == '(') break;

                        op = stack.front();
                        stack.pop_front();
                        os << op << " ";
                    }
                    stack.push_front(c);
                    break;
                case '+':
                    while (!stack.empty())
                    {
                        if(stack.front() == '(') break;

                        op = stack.front();
                        stack.pop_front();
                        os << op << " ";
                    }
                    stack.push_front(c);
                    break;
                case '-':
                    while (!stack.empty())
                    {
                        if(stack.front() == '(') break;

                        op = stack.front();
                        stack.pop_front();
                        os << op << " ";
                    }
                    stack.push_front(c);
                    break;
                case '\n':
                    while (!stack.empty())
                    {
                        op = stack.front();
                        stack.pop_front();
                        os << op << " ";
                    }
                    os << endl;

                    cerr << os.str().c_str();

                    postfix(os.str().c_str());
                    os.str("");
                    break;
                case 'q':
                    return 0;
                default:
                    break;
            }
        }
    }

    return 1;
}

int postfix(const char *exp)
{
    int             num, a, b;
    char            c;
    list_t<int>     stack;
    istringstream   is(exp);

    while((c = is.get()))
    {
        if (isdigit(c))
        {
            is.putback(c);
            is >> num;
            stack.push_front(num);
        }
        else
        {
            switch (c)
            {
                case -1:
                    return 0;
                case '*':
                    a = stack.front();
                    stack.pop_front();
                    b = stack.front();
                    stack.pop_front();
                    stack.push_front(b * a);
                    break;
                case '/':
                    a = stack.front();
                    stack.pop_front();
                    b = stack.front();
                    stack.pop_front();
                    stack.push_front(b / a);
                    break;
                case '+':
                    a = stack.front();
                    stack.pop_front();
                    b = stack.front();
                    stack.pop_front();
                    stack.push_front(b + a);
                    break;
                case '-':
                    a = stack.front();
                    stack.pop_front();
                    b = stack.front();
                    stack.pop_front();
                    stack.push_front(b - a);
                    break;
                case '\n':
                    num = stack.front();
                    cout << num << endl;
                    break;
                case 'q':
                    return 0;
                default:
                    break;
            }
        }
    }

    return 1;
}
