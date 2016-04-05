#!/bin/bash

#Test Runner.sh
#Runs all the tests for a given transaction, and puts the output files into the Tests folder
#Takes one console argument, which is the transaction name.
#Argument must be capitialized, and camel case with no spaces
#in the case of ChangePlan and PayBill

# make line just for testing purpose
make

cd tests/Tests/$1/

numfiles=(*)
numfiles=${#numfiles[@]}
numfiles=$(($numfiles / 2))
rm *.current.txt
# echo $numfiles
for i in $(seq 1 $numfiles)
do
	cd tests/Tests/$1/
	# echo "$i"
	# echo "$ioutput.txt"m
	string=""
	string+="$1"
	string+="$i"
	stringout=$string".current.txt"
	string+=".in.txt"
	# pwd
	cd ../../..
	# echo "$string"
	# rm tests/Tests/$1/"$stringout"
	echo "Testing $1 Command, Test Number $i, file $string"
	echo "running ./banksys teststate.txt tests/Tests/$1/$stringout < tests/Tests/$1/$string"
	 ./banksys teststate.txt tests/Tests/$1/"$stringout" < tests/Tests/$1/$string
done
