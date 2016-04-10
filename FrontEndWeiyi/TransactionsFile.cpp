#include "TransactionsFile.h"
#include "User.h"

#include <iostream>
#include <sstream>
#include <fstream>
#include <iomanip>

using namespace std;

TransactionsFile::TransactionsFile(string transaction_id, string account_name, int account_number, double new_balance){
	transaction_id_ = transaction_id;
	account_name_ = account_name;
	account_number_ = account_number;
	new_balance_ = new_balance;
}

void TransactionsFile::WriteTransaction(){
	//Open transactions.txt, write the new transaction, then close the file.
	ofstream transaction_file;
	transaction_file.open("transactions.txt", ios::app);
	string formatted_output = FormatTransactionID(transaction_id_) + " " + FormatName(account_name_) 
							  + " " + FormatNumber(account_number_) + " " + FormatBalance(new_balance_) 
							  + " " + FormatMiscellaneous();
	transaction_file << formatted_output+"\n";
	transaction_file.close();
}

string TransactionsFile::FormatTransactionID(string transaction_id){
	//This should be passed correctly, so shouldn't be a problem.
	return transaction_id;
}

string TransactionsFile::FormatName(string name){
	string formatted_name = name;
	//If the name is not of length 20, add spaces until it is of length 20
	if(name.length() < 20){
		for(int i = name.length(); i < 20; i++){
			formatted_name.insert(name.length()," ");
		}
	}
	return formatted_name;
}

string TransactionsFile::FormatNumber(int account_number){
	int account_length;
	int num_length = account_number;
	//For each time the number is succesfully divided by 10, increase the integer length.
	if(account_number > 0){
		for(account_length = 0; num_length > 0; account_length++){
			num_length = num_length / 10;
		}
	}
	//Make a string stream to type cast the integer to a string
	stringstream account_buf;
	account_buf << account_number;
	string str_number = account_buf.str();
	//If the length of account number < 5, pad 0's until length is 5.
	if(str_number.length() < 5){
		for(int i = str_number.length(); i < 5; i++){
			str_number.insert(0,"0");
		}
	}
	return str_number;
}

string TransactionsFile::FormatBalance(double balance){
	//Check the length of the balance. If balance length < 8, pad 0's until length is 8
	stringstream balance_buf;
	balance_buf << setprecision(2) << fixed << balance;
	string str_balance = balance_buf.str();
	if(str_balance.length() < 8){
		for(int i = str_balance.length(); i < 8; i++){
			str_balance.insert(0,"0");
		}
	}
	return str_balance;
}

string TransactionsFile::FormatMiscellaneous(){
	//Placeholder until Misc can be properly determined
	return "  ";
}