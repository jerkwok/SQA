#!/bin/sh

#day=${1:-1}

cd Frontend/

make

cd ..

rm $6

# Run the frontend 5 times
./Frontend/banksys $5 output_daily0.txt < $1
./Frontend/banksys $5 output_daily1.txt < $2
./Frontend/banksys $5 output_daily2.txt < $3
./Frontend/banksys $5 output_daily3.txt < $4
./Frontend/banksys $5 output_daily4.txt

#Merge the output files
for i in 0 1 2 3 4 
do
	# echo $i
	filename="output_daily"
	filename=$filename"$i.txt"
	cat $filename >> $6
done

# Remove the non merged output files
rm output_daily*

cd Backend/

make

java -cp bin backend.BackEnd ../dailyInput/masterBankAccounts ../$6 ../dailyInput/newmasterBankAccounts.txt ../dailyInput/CurrentBankAccounts.txt

cd ..