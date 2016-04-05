#!/bin/bash

#Test Validator.sh
#After running test runner, this script will check to see if the output matches
#the expected output files.
#Runs a diff on each output file and expected output file. Diff is stored in diffresult.txt
#in the folder that is being tested on.
#A copy with headers is stored in testresult.txt
#Takes one console argument, which is the transaction name.
#Argument must be capitialized, and camel case with no spaces
#in the case of ChangePlan and PayBill

cd tests/Tests/$1/

numfiles=(*)
numfiles=${#numfiles[@]}
numfiles=$(($numfiles / 3))

rm diffresult.txt
rm testresult.txt
for i in $(seq 1 $numfiles)
do
	echo "checking output of test $i"
	infile=""
	infile+="$1"
	infile+="$i"
	outfile=$infile
	infile+=".current.txt"
	outfile+=".out.txt"
	echo "diff $infile $outfile > diffresult.txt"
	# echo ":TEST $i:" >> diffresult.txt 
	echo ":TEST $i:" >> testresult.txt 
	diff $infile $outfile >> diffresult.txt
	diff $infile $outfile >> testresult.txt
	if [ -s diffresult.txt ]
		then
			echo "Test Fail"
			echo "Test Fail" >> testresult.txt
		else
			echo "Test Success"
			echo "Test Success" >> testresult.txt
	fi
done
