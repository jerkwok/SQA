#!/bin/sh

#day=${1:-1}

# Run the frontend 5 times
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output0.txt < ./Frontend/tests/Tests/Login/Login1.in.txt
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output1.txt < ./Frontend/tests/Tests/Login/Login1.in.txt
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output2.txt < ./Frontend/tests/Tests/Login/Login1.in.txt
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output3.txt < ./Frontend/tests/Tests/Login/Login1.in.txt
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output4.txt
#./Frontend/banksys ./dailyInput/CurrentBankAccounts.txt output0.txt < ./dailyInput/0$day/input01

#Merge the output files
for i in 0 1 2 3 4 
do
	# echo $i
	filename="output"
	filename=$filename"$i.txt"
	cat $filename >> mergedoutput.txt
done

# Remove the non merged output files
rm output*

cd Backend/

make

java -cp bin backend.BackEnd ../scripttesting/MasterBankAccounts.txt ../mergedoutput.txt ../scripttesting/NewMasterBankAccounts.txt ../scripttesting/CurrentBankAccounts.txt

cd ..