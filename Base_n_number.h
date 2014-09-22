// John Norwood
// Base_n_number.h

#ifndef BASE_N_NUMBER_H_
#define BASE_N_NUMBER_H_

#include <vector> 


// A not particularly good implementation of a base n number
class Base_n_number
{
public:

  // Construct the number with the input number of places
  Base_n_number(int base, int num_places)
  : m_base(base), m_rep(num_places, 0)
  {}

  
  // Sets the number to a value
  Base_n_number & operator =(int val)
  {
    set_value(val);
    return *this;
  }


  // Increments the number
  void increment()
  {
    bool increment = true;

    for (int i = 0; i < (int) m_rep.size() && increment; ++i)
    {
      if (increment)
      {
        increment = (m_rep[i] == m_base - 1);
        m_rep[i]  = increment ? 0 
                              : m_rep[i] + 1;
      }
    }
  }


  // Access the idxth place in the number
  int get_place(int idx) const
  {
    return m_rep[idx];
  }


private:


  // Sets the value of the number
  void set_value(int val)
  {
    int place_val     = 1;
    int highest_place = 0;

    for (; highest_place < (int) m_rep.size(); ++highest_place, place_val *= m_base)
      if (val < place_val * m_base)
        break;

    for (; highest_place > 0 && val > 0; --highest_place)
    {
      m_rep[highest_place] = val / place_val;
      val -= m_rep[highest_place] * place_val;
      place_val /= m_base;
    }

    m_rep[0] = val;
  }
    

  int               m_base; // The base of the number
  std::vector <int> m_rep;  // The representation of each "digit" of the number
};


#endif // BASE_N_NUMBER_H_
