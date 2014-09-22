John Norwood
2 / 19 / 2014
README


hashbash, so named because it bashes your hashes, and not at all as a reference to the 
popular nature festival held in Ann Arbor every April, will read a line seperated list 
of SHA1 hashes and search for them in the constructed rainbow table. It will read these 
hashes either from a file provided via the -f command line option or from stdin if -f 
is not  passed. A rainbow table can be constructed from a file with the -i option, 
otherwise it will be constructed from scratch.


hashbash [ -c <chain-lengh> -r <num-rows> -i <rainbow-file> -f <SHA1-digest-file> ]
