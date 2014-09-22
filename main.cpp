// John Norwood
// Chris Harris
// EECS 588
// main.cpp

#include "Rainbow_table.h"
#include <unistd.h>
#include <openssl/sha.h>   // SHA1
#include <openssl/md5.h>   // MD5
#include <cctype>          // isalphanum
#include <iostream>
#include <fstream>
#include <thread>
#include <mutex>
#include <atomic>
#include <vector>

using std::vector; using std::string; using std::cerr;
using std::cout; using std::endl; using std::cin;
using std::istream; using std::ifstream; using std::ofstream;
using std::thread; using std::mutex; using std::atomic;


// Default Rainbow table and various other parameters
const int DEFAULT_NUM_ROWS         = 10;
const int DEFAULT_CHAIN_LENGTH     = 100;
const int NUM_SEARCH_THREADS       = 8;
const int MAX_FNAME                = 33;
const char * RAINBOW_TABLE_FILE    = "rfile.txt";
const string DEFAULT_CHARACTER_SET = "0123456789abcdefghijklmnopqrstuvwxyz";


// Global counters for the number of cracked hashes
atomic<long> g_num_cracked(0);
atomic<long> g_num_entries(0);
mutex        g_file_lock;



// SHA1 Rainbow Table type

// Parameters
const int SHA1_MAX_KEY_LENGTH = 7;
const int SHA1_OUTPUT_LENGTH  = 20;

// The cipher function we're trying to reverse
void SHA1_cipher_func(char digest[SHA1_OUTPUT_LENGTH], const char * key);

// The reduction function used for the SHA1 table
void SHA1_redux_func(char key[SHA1_MAX_KEY_LENGTH], const char * digest, int step);

// The SHA1 Rainbow table type
typedef Rainbow_table <SHA1_redux_func, SHA1_MAX_KEY_LENGTH, SHA1_cipher_func, SHA1_OUTPUT_LENGTH> 
  SHA1_Rainbow_table_t;



// Helper functions 

// Parses command line arguments and stores the supplied values
bool parseCommands(int argc, char ** argv, char hash_file[MAX_FNAME],
                   char rainbow_file[MAX_FNAME], int & num_rows, 
                   int & chain_length);


// Converts an input character to its hexadecimal integer value
int char2int(char input);


// Converts a hexadecimal string to a binary representation storing it in 
// the input buffer
void hexstr_to_binary(const string & digest, char * buffer);


// Runs a loop reading hashes from the input filestream and searching for the corresponding
// password in the input rainbow table
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void crack_hash_loop(Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN> & rtable, 
                     istream & hashstream);


// Dispatches threads to crack hashes read from the input filestream
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void crack_hashes(Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN> & rtable, 
                  istream & hashstream);


// Constructs a SHA1 rainbow table, then reads in hashes in ascii format
// from the input stream and tries to crack them one by one
void crack_SHA1(istream & hashstream, int num_rows, int chain_length, const char * rainbow_file);
  


// Where it all begins
int main(int argc, char ** argv)
{
  // Read the command line args
  char hash_file[MAX_FNAME]    = { '\0' }; 
  char rainbow_file[MAX_FNAME] = { '\0' }; 
  int num_rows                 = DEFAULT_NUM_ROWS; 
  int chain_length             = DEFAULT_CHAIN_LENGTH; 

 	if(parseCommands(argc, argv, hash_file, rainbow_file, num_rows, chain_length))
 		return 1;

  // If no filename provided, read from stdin
  if (*hash_file)
  {
    ifstream hashfstr(hash_file);
    crack_SHA1(hashfstr, num_rows, chain_length, rainbow_file);
  }

  else
    crack_SHA1(cin, num_rows, chain_length, rainbow_file);
}


// The cipher function we're trying to reverse
void SHA1_cipher_func(char digest[SHA1_OUTPUT_LENGTH], const char * key)
{
  SHA1((const unsigned char *) key, SHA1_MAX_KEY_LENGTH, (unsigned char *) digest);
}


// The reduction function used for the SHA1 table
void SHA1_redux_func(char key[SHA1_MAX_KEY_LENGTH], const char * digest, int step)
{
  for (int i = 0; i < SHA1_MAX_KEY_LENGTH; ++i)
  {
    int acc = (step + digest[i]) % (DEFAULT_CHARACTER_SET.size()); 
    key[i] = DEFAULT_CHARACTER_SET[acc];
  }
}

// Parses command line arguments and stores the supplied values
bool parseCommands(int argc, char ** argv, char hash_file[MAX_FNAME],
                   char rainbow_file[MAX_FNAME], int & num_rows, 
                   int & chain_length)
{
	char c; 
  int chain, rows; 

  while ((c = getopt (argc, argv, "f:r:c:i:")) != -1)
  {
    switch (c)
    {
      case 'f':
        if (strnlen(optarg, MAX_FNAME+1) > MAX_FNAME)
        {
          cerr << "Hash file name too long" << endl;
          return true;
        }

        else
          strncpy(hash_file, optarg, MAX_FNAME);

        break;

      case 'c':
        chain = atoi(optarg);
        if (chain > 0)
          chain_length = chain;

        break;

      case 'r':
        rows = atoi(optarg);
        if (rows > 0)
          num_rows = rows;

        break;

      case 'i':
        if (strnlen(optarg, MAX_FNAME+1) > MAX_FNAME)
        {
          cerr << "Rainbow file name too long" << endl;
          return true;
        }

        else
          strncpy(rainbow_file, optarg, MAX_FNAME);

        break;

      default:
     	  cerr << "Invalid arguments" << endl;
        return true;
    }
  }

  return false;
}


// Converts an input character to its hexadecimal integer value
int char2int(char input)
{
	if(input >= '0' && input <= '9')
		return input - '0';

	if(input >= 'A' && input <= 'F')
		return input - 'A' + 10;

	if(input >= 'a' && input <= 'f')
		return input - 'a' + 10;

  return -1;
}


// Converts a hexadecimal string to a binary representation storing it in 
// the input buffer
void hexstr_to_binary(const string & digest, char * buffer)
{
	memset(buffer, 0, SHA1_OUTPUT_LENGTH);
	for (int i = 0; i < (int) digest.size(); i += 2)
	{
		char msn = digest[i];
		char lsn = digest[i+1];

		buffer[i/2] = (char2int(msn) << 4) + char2int(lsn);
	}
}

// Runs a loop reading hashes from the input filestream and searching for the corresponding
// password in the input rainbow table
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void crack_hash_loop(Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN> & rtable, 
                     istream & hashstream)
{
  // Read and try to crack each hash from the istream
  string hashstr;

  g_file_lock.lock();
  while (hashstream >> hashstr)
  {
    g_file_lock.unlock();

    g_num_entries++;

    char hash[SHA1_OUTPUT_LENGTH];
    hexstr_to_binary(hashstr, hash);
    string password = rtable.search(hash);

    if (password.length())
    {
      cout << "Found matching password " << password << endl;
      g_num_cracked++;
    }

    g_file_lock.lock();
  }

  g_file_lock.unlock();
}


// Dispatches threads to crack hashes read from the input filestream
template 
<
  reduction_function_t RED_FN, int MAX_KEY_LEN, 
  cipher_function_t CIPHER_FN, int CIPHER_OUTPUT_LEN
>
void crack_hashes(Rainbow_table <RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN> & rtable, 
                  istream & hashstream)
{
  auto start_time = std::chrono::system_clock::now();

  vector<thread> threads;
  for(int i = 0; i < NUM_SEARCH_THREADS; ++i)
    threads.emplace_back(crack_hash_loop<RED_FN, MAX_KEY_LEN, CIPHER_FN, CIPHER_OUTPUT_LEN>, std::ref(rtable), std::ref(hashstream));

  for_each(threads.begin(), threads.end(), mem_fn(&thread::join));

  auto elapsed = std::chrono::system_clock::now() - start_time;
  cout << "Cracking Finished.  Total time = " << (std::chrono::duration_cast <std::chrono::milliseconds>(elapsed).count() / 1000.) << " seconds." << endl;
  cout << g_num_cracked << " cracked out of " << g_num_entries << " total entries." << endl;
  cout << (double) g_num_cracked / (double) g_num_entries * 100. << "% cracked." << endl;
}


// Constructs a SHA1 rainbow table, then reads in hashes in ascii format
// from the input stream and tries to crack them one by one
void crack_SHA1(istream & hashstream, int num_rows, int chain_length, const char * rainbow_file)
{
  SHA1_Rainbow_table_t * rtable;

  // Construct the rainbow table
  if (*rainbow_file)
  {
    ifstream rfile(rainbow_file);
    rtable = new SHA1_Rainbow_table_t(rfile, DEFAULT_CHARACTER_SET);
  }

  else
  {
    rtable = new SHA1_Rainbow_table_t(num_rows, chain_length, DEFAULT_CHARACTER_SET);
    ofstream rfile(RAINBOW_TABLE_FILE);
    rtable->save(rfile);
  }

  crack_hashes(*rtable, hashstream);
}
