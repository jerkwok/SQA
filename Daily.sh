#!/bin/sh

#day=${1:-1}

cd Frontend/

make

cd ..

rm $6

# Run the frontend 5 times
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output0.txt < $1
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output1.txt < $2
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output2.txt < $3
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output3.txt < $4
./Frontend/banksys ./Frontend/CurrentBankAccounts.txt output4.txt < $5
#./Frontend/banksys ./dailyInput/CurrentBankAccounts.txt output0.txt < ./dailyInput/0$day/input01

#Merge the output files
for i in 0 1 2 3 4 
do
	# echo $i
	filename="output"
	filename=$filename"$i.txt"
	cat $filename >> $6
done

# Remove the non merged output files
# rm output*

cd Backend/

make

java -cp bin backend.BackEnd ../scripttesting/MasterBankAccounts.txt ../$6 ../scripttesting/NewMasterBankAccounts.txt ../scripttesting/CurrentBankAccounts.txt

cd ..