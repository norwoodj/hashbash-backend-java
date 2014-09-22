// John Norwood
// Chris Harris
// EECS 588
// Rainbow_table.h

#ifndef RAINBOW_TABLE_H_
#define RAINBOW_TABLE_H_

#include "Base_n_number.h"
#include <thread>
#include <vector>
#include <algorithm>
#include <functional>
#include <cstring>
#include <string>
#include <iostream>
#include <fstream>
#include <chrono>


// Reduction and Cipher function types used by the table
typedef void (*reduction_function_t)(char * key, const char * cipher, int step);
typedef void (*cipher_function_t)(char * cipher, const char * key);



// The rainbow table structure
template 
<
  reduction_function_t RED_FN, // The family of reduction functions to use for this table
  int MAX_KEY_LEN,          // The maximum number of bytes in a key

  cipher_function_t CIPHER_FN, // The cipher function used to generate the table
  int CIPHER_OUTPUT_LEN     // The size of the cipher output in bytes
>
class Rainbow_table
{
public:

  // Default constructor
  Rainbow_table()
  : Rainbow_table(RAINBOW_DEFAULT_ROWS, RAINBOW_DEFAULT_CHAIN, RAINBOW_DEFAULT_CHARSET)
  {}


  // Destructor
  ~Rainbow_table()
  {
    delete [] table;
  }


  // Constructor constructs the table from the input initialization
  // key list and the list of reduction functions
  Rainbow_table(int num_rows_in, int chain_length_in, const std::string & character_set_in); 


  // File constructor reads in the endpoints from a file and constructs the table from them
  Rainbow_table(std::istream & infile, const std::string & character_set_in); 


  // Searches the table for the input hash, returning the corresponding
  // password if found, null otherwise
  std::string search(const char * digest);


  // Saves the table to the file with the input name
  void save(std::ostream & outfile) const;


// Private static constants
private:

  // Default values for the table
  static const int RAINBOW_DEFAULT_ROWS    = 20;
  static const int RAINBOW_DEFAULT_CHAIN   = 10;
  static const int RAINBOW_DEFAULT_THREADS = 8;
  static const int RAINBOW_NUM_GENERATIONS = 1;

  // The default character set used
  static const std::string RAINBOW_DEFAULT_CHARSET;


// Structure definitions
private:

  // A chain in the table, consisting of a starting and ending point
  struct Rainbow_chain
  {
    char start[MAX_KEY_LEN];
    char end  [MAX_KEY_LEN];

    bool operator <(const Rainbow_chain & other) const
    {
      return memcmp(end, other.end, MAX_KEY_LEN) < 0;
    }

    bool operator ==(const Rainbow_chain & other) const
    {
      return memcmp(end, other.end, MAX_KEY_LEN) == 0;
    }
  };


  // A list of threads for generating the table
  typedef std::vector <std::thread> thread_list_t;


// Helper functions
private:

  // Generates the table 
  void generate_table();


  // Generates a range of the table, starting at the input key and index and 
  // generating rows_per_thread rows
  void generate_table_range(int start_idx, int rows_per_thread, 
                            Base_n_number keygen);


  // Dispatches threads to generate the table from the input row down
  void generate_table_from(Rainbow_chain * starting_row);


  // Generates an individual chain in the table starting from the input intial key, 
  // generating chainlength iterations, and writing the ending key in the input 
  // endpoint buffer
  void generate_chain_from_key(const char * initial_key, int chain_length, 
                               char endpoint[MAX_KEY_LEN]);


  // Generates a chain starting from the input index of the chain, with the 
  // input digest as the hash at this point, stores the ending key in the 
  // input endpoint variable
  void generate_chain_from_hash(const char * hash, int chain_link_index, 
                                int chain_length, char endpoint[MAX_KEY_LEN]);


  // Writes the next key to be generated into the slot at input table index
  void write_key(const Base_n_number & keygen, int idx);


  // Dispatches a thread to generate the input number of rows from the
  // input index of the table, puts the thread in the threads list so
  // the main thread can join it.
  void dispatch_generator_thread(int start_idx, int rows_per_thread);


  // Finds the row of the table that matches the input one, returns nullptr if 
  // no such row found
  const Rainbow_chain * find_matching_endpoint(const Rainbow_chain * endpoint) const;


// Instance variables
private:

  Rainbow_chain *   table;         // The table data store
  Rainbow_chain *   last_row;      // The last generated row in the table
  const std::string character_set; // The set of characters we're exploring
  int               next_key;      // The next key to be used for an endpoint
  int               num_rows;      // Number of rows in the table
  int               chain_length;  // Length of the chains, number of generations of hash 
  thread_list_t     threads;       // Generator threads
};


// The default character set used by the table
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
const std::string Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::RAINBOW_DEFAULT_CHARSET =
  "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";


// Constructor constructs the table from the input initialization
// key list and the list of reduction functions
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::Rainbow_table(
  int                 num_rows_in,
  int                 chain_length_in,
  const std::string & character_set_in
) 
: character_set(character_set_in), next_key(0), num_rows(num_rows_in), 
  chain_length(chain_length_in) 
{
  generate_table();
}


// Saves the table to the file with the input name
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::save(std::ostream & outfile) const
{
  outfile << (last_row - table) << ' ' << chain_length << std::endl;

  for(int i = 0; i < last_row - table; ++i)
  {
    for(int j = 0; j < MAX_KEY_LEN; j++)
      outfile << table[i].start[j];
    for(int j = 0; j < MAX_KEY_LEN; j++)
      outfile << table[i].end[j];
  }
}


// File constructor reads in the endpoints from a file and constructs the table from them
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::Rainbow_table(
  std::istream &      infile, 
  const std::string & character_set_in
)
: character_set(character_set_in)
{
  // Should probably error check, but...
  int number_rows, chain_len;
  infile >> number_rows >> chain_len;

  num_rows     = number_rows;
  chain_length = chain_len;
  table        = new Rainbow_chain[num_rows];
  last_row     = table + num_rows;

  // Discard the newline
  infile.get();

  for (int i = 0; i < num_rows && infile; ++i)
  {
    infile.read(table[i].start, MAX_KEY_LEN);
    infile.read(table[i].end, MAX_KEY_LEN);
  }
}


// Generates the table 
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::generate_table()
{
  last_row = table = new Rainbow_chain[num_rows];

  // For each provided intial key, generate a chain
  for (int i = 0; i < RAINBOW_NUM_GENERATIONS && (last_row - table) < num_rows; ++i)
  {
    auto start_time = std::chrono::system_clock::now();

    generate_table_from(last_row);
    std::sort(table, table + num_rows);
    last_row = std::unique(table, table + num_rows);

    auto elapsed = std::chrono::system_clock::now() - start_time;
    std::cout << "Time: " << (std::chrono::duration_cast <std::chrono::milliseconds>(elapsed).count() / 1000.) << std::endl;
    std::cout << "There are now " << (last_row - table) << " unique endpoints" << std::endl;
  }
}


// Dispatches threads to generate the table from the input row down
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::generate_table_from(
  Rainbow_chain * starting_row
)
{
  int start_idx       = starting_row - table;
  int generation_rows = num_rows - start_idx;
  int rows_per_thread = generation_rows / RAINBOW_DEFAULT_THREADS;

  // Dispatch threads to generate the table
  for (int i = 0; i < RAINBOW_DEFAULT_THREADS-1; ++i, start_idx += rows_per_thread)
    dispatch_generator_thread(start_idx, rows_per_thread);

  dispatch_generator_thread(start_idx, num_rows - start_idx);

  // Join all of the threads
  std::for_each(threads.begin(), threads.end(), std::mem_fn(&std::thread::join));
  threads.clear();
}


// Dispatches a thread to generate the input number of rows from the
// input index of the table, puts the thread in the threads list so
// the main thread can join it.
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::dispatch_generator_thread(
  int start_idx,
  int rows_per_thread
)
{
  // Create a number where each digit represents the index of the character
  // set to use for that index of the key
  Base_n_number keygen(character_set.length(), MAX_KEY_LEN);
  keygen = next_key;
  next_key += rows_per_thread;

  // Dispatch the thread and put it in the thread list
  threads.emplace_back(&Rainbow_table::generate_table_range, this, start_idx, rows_per_thread, keygen);
}


// Generates a range of the table, starting at the input key and index and 
// generating rows_per_thread rows
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::generate_table_range(
  int           start_idx,
  int           rows_per_thread,
  Base_n_number keygen
)
{
  for (int i = start_idx; i < start_idx + rows_per_thread; ++i)
  {
    write_key(keygen, i);
    keygen.increment();
    generate_chain_from_key(table[i].start, chain_length, table[i].end);
  }
}



// Generates a chain starting from the input index of the chain, with the input digest as the
// hash at this point, stores the ending key in the input endpoint variable
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::generate_chain_from_hash(
  const char * hash, 
  int          chain_link_index, 
  int          chain_length, 
  char         endpoint[MAX_KEY_LEN]
)
{
  // Reduce the hash to a starting key
  RED_FN(endpoint, hash, chain_link_index);
  
  // From this link to the end of the chain
  for (int i = 1; i < chain_length; ++i)
  {
    // Hash the current key, then reduce it to the next key 
    char hashbuf[CIPHER_OUTPUT_LEN];
    CIPHER_FN(hashbuf, endpoint);
    RED_FN(endpoint, hashbuf, chain_link_index + i);
  }
}
  
  

// Generates an individual chain in the table starting from the input intial key, generating
// chainlength iterations, and writing the ending key in the input endpoint buffer
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::generate_chain_from_key(
  const char * initial_key, 
  int          chain_length, 
  char         endpoint[MAX_KEY_LEN]
)
{
  if (chain_length == 0)
  {
    memcpy(endpoint, initial_key, MAX_KEY_LEN);
    return;
  }

  // Encipher the key and generate from here to end of chain from
  // first digest
  char hashbuf[CIPHER_OUTPUT_LEN];
  CIPHER_FN(hashbuf, initial_key);
  generate_chain_from_hash(hashbuf, 0, chain_length, endpoint);
}


template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
typename Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::Rainbow_chain const *
Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::find_matching_endpoint(
  const Rainbow_chain * test
) const
{
  const Rainbow_chain * itr = std::lower_bound(table, last_row, *test);

  if (memcmp(itr->end, test->end, MAX_KEY_LEN) == 0)
    return itr;

  return nullptr;
}


// Searches the table for the input hash, returning the corresponding
// password if found, null otherwise
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
std::string Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::search(
  const char * digest
)
{
  // For each link in the chain try to reverse the hash as though it were there
  for (int i = chain_length; i > 0; --i)
  {
    Rainbow_chain test;
    generate_chain_from_hash(digest, i - 1, chain_length - i + 1, test.end);
    const Rainbow_chain * location = find_matching_endpoint(&test);

    if (location)
    {
      generate_chain_from_key(location->start, i-1, test.end);
      char real_digest[CIPHER_OUTPUT_LEN];
      CIPHER_FN(real_digest, test.end);

      if (memcmp(real_digest, digest, CIPHER_OUTPUT_LEN) == 0)
        return std::string(test.end,  MAX_KEY_LEN);
    }
  }

  return "";
}


// Writes the next key to be generated into the slot at input table index
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>::write_key(
  const Base_n_number & num,
  int                   index
)
{
  for (int i = 0; i < MAX_KEY_LEN; ++i)
    table[index].start[i] = character_set[num.get_place(i)];
}
  

#endif
