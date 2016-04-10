#include "Transactions.h"
#include "User.h"
#include "TransactionsFile.h"

#include <string>
#include <iostream>
#include <fstream>
#include <sstream>
#include <cstring>
#include <algorithm>
#include <iomanip>

using namespace std;

Transactions::Transactions(){
	transaction_id_ = "";
}

//Checks if a string could be casted to an integer or not.
bool Transactions::IsInteger(string check_string){
	char* p;
	strtol(check_string.c_str(), &p, 10);
	//If the function finds a character that could not be interpretted as a integer, it stores its index in p. If p is
	//not the default 0 value, then it can not be an integer and is false. Otherwise, it can be an integer and returns true.
	return (*p == 0);
}

bool Transactions::IsDouble(string check_string){
    char* p = 0;
    strtod(check_string.c_str(), &p);
    //If the function finds a character that could not be interpretted as a double, it stores its index in p. If p is
	//not the default 0 value, then it can not be a double and is false. Otherwise, it can be a double and returns true.
    return !(*p != 0 || p == check_string.c_str());
}

User Transactions::Login(string name){
	//If the user logins in as admin, log them in automatically. Else, prompt for account name
	if(name == "admin"){
		User admin(00000, "ADMIN", 0);
		cout << "Login Successful" << endl;
		TransactionsFile trans("10", "ADMIN", 0, 0);
		trans.WriteTransaction();
		return admin;
	} else {
		//Go through the accounts folder, and check if that name is attached to at least 1 account. If so, login. Else, error.
		ifstream accounts("accounts.txt");
		string line;
		while(getline(accounts, line)){
			size_t found = line.find(name);
			if(found != string::npos){
				cout << "Login Successful" << endl;
				string login_number;
				for(int i = 0; i < 5; i++){
					login_number = login_number + line[i];
				}
				istringstream account_buf(login_number);
				double number;
				account_buf >> number;
				User found_user = ReadAccount(name, number);
				TransactionsFile trans("10", name, number, 0);
				trans.WriteTransaction();
				return found_user;	
			} 
		}
		cout << "Error: Account not found under that name." << endl;
		User null(00000, "NULL", 0);
		return null;
	}
}

void Transactions::Withdrawal(User user){
	cout << "------------------" << endl;
	cout << "|   Withdrawal   |" << endl;
	cout << "------------------" << endl;
	string name_input;
	//If the user is an admin, ask for a name, otherwise, auto fill in name.
	if(user.GetName() == "ADMIN"){
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
	} else {
		name_input = user.GetName();
	}
	//Ask for the user to enter an account number
	int account_input;
	string account_entry;
	cout << "Please enter the account number: ";
	cin >> account_entry;
	cout << account_entry << endl;
	if(IsInteger(account_entry) == true){
		istringstream account_buf(account_entry);
		account_buf >> account_input;
		//Call ReadAccount to check if an account with that name and number exist
		User found_user = ReadAccount(name_input, account_input);
		//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with withdrawal
		if(found_user.GetName().compare("ERROR") == 0){
			cout << "Error: Account not found." << endl;
		} else if (found_user.GetStatus() == 'D'){
				cout << "Error: Account is currently disabled. Please contact admin." << endl;
		} else {
			//Ask for withdraw amount
			double withdraw_amount;
			string withdraw_entry;
			cout << "Please enter the amount to withdraw: ";
			cin >> withdraw_entry;
			cout << withdraw_entry << endl;
			if(IsDouble(withdraw_entry) == true){
				istringstream withdraw_buf(withdraw_entry);
				withdraw_buf >> withdraw_amount;
				//Before checking withdraw amount, check which fee we need to charge. 0.10 if not a student, 0.05 if a student
				double fee;
				if(found_user.GetPlan() == "NP"){
					fee = 0.10;
				} else {
					fee = 0.05;
				}
				//Check for error conditions withdrawal > balance, withdrawal > 500 when not admin, withdrawal not divisible by 5.
				if(fee + withdraw_amount > found_user.GetBalance()){
					cout << "Error: Withdrawal amount + fee is greater then balance" << endl;
				} else if (user.GetName() != "ADMIN" && withdraw_amount > 500){
					cout << "Error: Only admin may withdraw more then $500.00" << endl;
				} else if (withdraw_amount < 0 || ((int)withdraw_amount % 5) != 0){
					cout << "Error: Invalid withdrawal amount" << endl;
				} else {
					//Write transaction log
					TransactionsFile trans("01", found_user.GetName(), found_user.GetNumber(), withdraw_amount);
					trans.WriteTransaction();
					//Update the users account balance.
					found_user.UpdateNewBalance(found_user.GetNewBalance()-withdraw_amount-fee);
					found_user.UpdateBalance(0-withdraw_amount-fee);
					//Update daily account file
					UpdateDay(found_user);
					cout << "Transaction Successful" << endl;
				}
			} else {
				cout << "Error: Not a number entered." << endl;
			}
		}
	} else {
		cout << "Error: Not a number entered." << endl;
	}
}

void Transactions::Deposit(User user){
	cout << "-------------------" << endl;
	cout << "|     Deposit     |" << endl;
	cout << "-------------------" << endl;
	string name_input;
	//If the user is an admin, ask for a name, otherwise, auto fill in name.
	if(user.GetName() == "ADMIN"){
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
	} else {
		name_input = user.GetName();
	}
	//Ask for the user to enter an account number
	int account_input;
	string account_entry;
	cout << "Please enter the account number: ";
	cin >> account_entry;
	cout << account_entry << endl;
	if(IsInteger(account_entry) == true){
		istringstream account_buf(account_entry);
		account_buf >> account_input;
		//Call ReadAccount to check if an account with that name and number exist
		User found_user = ReadAccount(name_input, account_input);
		//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with deposit
		if(found_user.GetName().compare("ERROR") == 0){
			cout << "Error: Account not found." << endl;
		} else if (found_user.GetStatus() == 'D'){
			cout << "Error: Account is currently disabled. Please contact admin." << endl;
		} else {
			//Ask for withdraw amount
			double deposit_amount;
			string deposit_entry;
			cout << "Please enter the amount to deposit: ";
			cin >> deposit_entry;
			cout << deposit_entry << endl;
			if(IsDouble(deposit_entry) == true){
				istringstream deposit_buf(deposit_entry);
				deposit_buf >> deposit_amount;
				//Before checking deposit amount, check which fee we need to charge. 0.10 if not a student, 0.05 if a student
				double fee;
				if(found_user.GetPlan() == "NP"){
					fee = 0.10;
				} else {
					fee = 0.05;
				}
				//Check for error conditions deposit > 99999.99, deposit amount not negative.
				if(deposit_amount + found_user.GetNewBalance() - fee > 99999.99){
					cout << "Error: Deposit amount cannot leave account balance at higher then 99999.99" << endl;
				} else if (deposit_amount < 0){
					cout << "Error: Invalid deposit amount" << endl;
				} else {
					//Write transaction log
					TransactionsFile trans("04", found_user.GetName(), found_user.GetNumber(), deposit_amount);
					trans.WriteTransaction();
					//Update the users account balance.
					found_user.UpdateNewBalance(found_user.GetNewBalance()+deposit_amount - fee);
					found_user.UpdateBalance(0-fee);
					//Update daily account file
					UpdateDay(found_user);
					cout << "Transaction Successful" << endl;
				}
			} else {
				cout << "Error: Not a number entered." << endl;
			}
		}
	} else {
		cout << "Error: Not a number entered." << endl;
	}
}

void Transactions::Transfer(User user){
	cout << "--------------------" << endl;
	cout << "|     Transfer     |" << endl;
	cout << "--------------------" << endl;
	string name_input;
	//If the user is an admin, ask for a name, otherwise, auto fill in name.
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
	} else {
		name_input = user.GetName();
	}
	//Ask for the user to enter an account number
	int account_input;
	string account_entry;
	int transfer_account;
	cout << "Please enter the account number to transfer from: ";
	cin >> account_entry;
	cout << account_entry << endl;
	if(IsInteger(account_entry) == true){
		istringstream account_buf(account_entry);
		account_buf >> account_input;
		//Call ReadAccount to check if an account with that name and number exist
		User found_user = ReadAccount(name_input, account_input);
		//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with paybill
		if (found_user.GetName().compare("ERROR") == 0) {
			cout << "Error: Account not found." << endl;
		} else if (found_user.GetStatus() == 'D'){
			cout << "Error: Account is currently disabled. Please contact admin." << endl;
		} else {
			cout << "Please enter the account number to transfer to: ";
			cin >> account_entry;
			cout << account_entry << endl;
			if(IsInteger(account_entry) == true){
				istringstream account_buf(account_entry);
				account_buf >> transfer_account;
				//Get the payee account information
				User payee = ReadAccount("", transfer_account);
				//Ask for withdraw amount
				if (found_user.GetName().compare("ERROR") == 0) {
					cout << "Error: Payee account not found." << endl;
				} else if (payee.GetStatus() == 'D'){
					cout << "Error: Account is currently disabled. Please contact admin." << endl;
				} else {
					double transfer_amount;
					string transfer_entry;
					cout << "Please enter the amount to transfer: ";
					cin >> transfer_entry;
					cout << transfer_entry << endl;
					if(IsDouble(transfer_entry) == true){
						istringstream transfer_buf(transfer_entry);
						transfer_buf >> transfer_amount;
						//Before checking transfer amount, check which fee we need to charge. 0.10 if not a student, 0.05 if a student
						double fee;
						if (found_user.GetPlan() == "NP") {
							fee = 0.10;
						} else {
							fee = 0.05;
						}
						//Check for error conditions transfer amount > balance, withdrawal > 500 when not admin, withdrawal not divisible by 5.
						if (fee + transfer_amount > found_user.GetBalance()) {
							cout << "Error: Transfer amount + fee is greater then balance" << endl;
						} else if (user.GetName() != "ADMIN" && transfer_amount > 1000) {
							cout << "Error: Only admin may transfer more then $1000.00" << endl;
						} else if (transfer_amount < 0 ) {
							cout << "Error: Invalid transfer amount" << endl;
						} else if (payee.GetNewBalance() + transfer_amount - fee > 99999.99) {
							cout << "Error: Payee account would be over balance cap of $99999.99" << endl;
						} else if (payee.GetStatus() == 'D' || found_user.GetStatus() == 'D' ) {
							cout << "Error: Accounts must be active" << endl;
						} else {
							//Write transaction log
							TransactionsFile trans1("02", found_user.GetName(), found_user.GetNumber(), transfer_amount);
							trans1.WriteTransaction();
							TransactionsFile trans2("02", payee.GetName(), payee.GetNumber(), transfer_amount);
							trans2.WriteTransaction();
							//Update the users account balance.
							found_user.UpdateNewBalance(found_user.GetNewBalance() - transfer_amount - fee);
							found_user.UpdateBalance(0 - transfer_amount - fee);
							payee.UpdateNewBalance(found_user.GetNewBalance()+transfer_amount);
							//Update daily account file
							UpdateDay(found_user);
							UpdateDay(payee);
							//Update the person receiving the money.
							cout << "Transaction Successful" << endl;
						}
					} else {
						cout << "Error: Not a number entered." << endl;
					}
				}
			} else {
				cout << "Error: Not a number entered." << endl;
			}
		}
	} else {
		cout << "Error: Not a number entered." << endl;
	}
}

void Transactions::PayBill(User user){
	cout << "--------------------" << endl;
	cout << "|     Pay Bill     |" << endl;
	cout << "--------------------" << endl;
	string name_input;
	//If the user is an admin, ask for a name, otherwise, auto fill in name.
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
	} else {
		name_input = user.GetName();
	}
	//Ask for the user to enter an account number
	int account_input;
	string account_entry;
	cout << "Please enter the account number: ";
	cin >> account_entry;
	cout << account_entry << endl;
	if(IsInteger(account_entry) == true){
		istringstream account_buf(account_entry);
		account_buf >> account_input;
		//Call ReadAccount to check if an account with that name and number exist
		User found_user = ReadAccount(name_input, account_input);
		//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with withdrawal
		if (found_user.GetName().compare("ERROR") == 0) {
			cout << "Error: Account not found." << endl;
		} else if (found_user.GetStatus() == 'D'){
			cout << "Error: Account is currently disabled. Please contact admin." << endl;
		} else {
			//Ask company to whom bill is being paid
			string company_name;
			cout << "Please enter which company you are paying to: " <<  endl;
			cout << "The Bright Light Electric Company (EC)" << endl;
			cout << "Credit Card Company Q (CQ)" << endl;
			cout << "Low Definition TV, Inc. (TV)" << endl;
			cout << "Enter their abbreviation (e.g. Credit Card Company enter CQ): ";
			cin >> company_name;
			cout << company_name << endl;
			transform(company_name.begin(), company_name.end(), company_name.begin(), ::toupper);
			if(company_name.compare("EC") == 0 || company_name.compare("CQ") == 0 || company_name.compare("TV") == 0){
				//Ask for bill amount
				double bill_amount;
				string bill_entry;
				cout << "Please enter the amount to pay: ";
				cin >> bill_entry;
				cout << bill_entry << endl;
				if(IsDouble(bill_entry) == true){
					istringstream bill_buf(bill_entry);
					bill_buf >> bill_amount;
					//Before checking withdraw amount, check which fee we need to charge. 0.10 if not a student, 0.05 if a student
					double fee;
					if (found_user.GetPlan() == "NP") {
						fee = 0.10;
					} else {
						fee = 0.05;
					}
					//Check for error conditions withdrawal > balance, withdrawal > 2000 when not admin, withdrawal not divisible by 5.
					if (fee + bill_amount > found_user.GetBalance()) {
						cout << "Error: Bill payment amount + fee is greater then balance" << endl;
					} else if (user.GetName() != "ADMIN" && bill_amount > 2000) {
						cout << "Error: Only admin may pay more then $2000.00" << endl;
					} else if (bill_amount < 0) {
						cout << "Error: Invalid bill amount" << endl;
					} else {
						//Write transaction log
						TransactionsFile trans("03", found_user.GetName(), found_user.GetNumber(), bill_amount);
						trans.WriteTransaction();
						//Update the users account balance.
						found_user.UpdateNewBalance(found_user.GetNewBalance() - bill_amount - fee);
						found_user.UpdateBalance(0 - bill_amount - fee);
						//Update daily account file
						UpdateDay(found_user);
						cout << "Transaction Successful" << endl;
					}
				} else {
					cout << "Error: Not a number entered." << endl;
				}
			} else {
				cout << "Error: No company with that abbreviation exists." << endl;
			}
		}
	} else {
		cout << "Error: Not a number entered." << endl;
	}
}

void Transactions::Create(User user){
	cout << "--------------------" << endl;
	cout << "|      Create      |" << endl;
	cout << "--------------------" << endl;
	//check admin privileges
	string name_input;
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
		if(name_input.length() > 20){
			name_input.resize(20);
		}
		//Ask for the user to enter an account number
		double balance_amount;
		string balance_entry;
		cout << "Please enter the initial account balance: ";
		cin >> balance_entry;
		cout << balance_entry << endl;
		if(IsDouble(balance_entry) == true){
			istringstream balance_buf(balance_entry);
			balance_buf >> balance_amount;
			if(balance_amount > 99999.99){
				cout << "Error: Initial balance cannot be greater then $99999.99" << endl;
			} else if (balance_amount < 0){
				cout << "Error: Initial balance must be greater then $0.00" << endl;
			} else {
				bool found_account = false;
				int counter = 1;
				while(found_account == false){
					User possible_user = ReadAccount("", counter);
					if(possible_user.GetName().compare("ERROR") == 0){
						found_account = true;
					} else {
						counter++;
					}
				}
				TransactionsFile trans("05", name_input, counter, balance_amount);
				trans.WriteTransaction();
				cout << "Account Created" << endl;
			}
		} else {
			cout << "Error: Not a number entered." << endl;
		}
	} else {
		cout << "Error: Only admin can access this function." << endl;
	}
}

void Transactions::Delete(User user){
	cout << "--------------------" << endl;
	cout << "|      Delete      |" << endl;
	cout << "--------------------" << endl;
	//check admin privileges
	string name_input;
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
		//Ask for the user to enter an account number
		int account_input;
		string account_entry;
		cout << "Please enter the account number: ";
		cin >> account_entry;
		cout << account_entry << endl;
		if(IsInteger(account_entry) == true){
			istringstream account_buf(account_entry);
			account_buf >> account_input;
			//Call ReadAccount to check if an account with that name and number exist
			User found_user = ReadAccount(name_input, account_input);
			//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with withdrawal
			if (found_user.GetName().compare("ERROR") == 0) {
				cout << "Error: Account not found." << endl;
			} else {
				//delete account
				ifstream accounts("accounts.txt");
				ofstream editted_accounts;
				string entire_file = "";
				string found_line;
				string line;
				while(getline(accounts, line)){
					entire_file = entire_file + line + "\n";
					string account_name;
					string account_status;
					string account_balance;
					string account_to_check;
					//Go through each line and process the information in it.
					for(int i = 0; i < 36; i++){
						if(i >= 0 && i < 5){
							account_to_check = account_to_check + line[i];
						} else if (i > 5 && i < 25) {
							if(line[i] == ' ' && line[i+1] == ' '){
								//Don't do anything, its a filler space
							} else {
								account_name = account_name + line[i];
							}
						} else if (i > 25 && i < 27){
							account_status = account_status + line[i];
						} else if (i > 27){
							account_balance = account_balance + line[i];
						}
					}
					istringstream balance_buf(account_balance);
					double balance;
					balance_buf >> balance;
					istringstream number_buf(account_to_check);
					int account_num;
					number_buf >> account_num;
					//If we find a matching user, store the found user for later.
					if(found_user.GetNumber() == account_num && account_name.compare(found_user.GetName()) == 0){
						found_line = line;
					}
				}
				entire_file = entire_file.substr(0,entire_file.length()-1);
				size_t beginning_of_line = entire_file.find(found_line);
				string before = entire_file.substr(0, 0+beginning_of_line);
				string after = entire_file.substr(beginning_of_line+37);
				entire_file = before + after;
				editted_accounts.open("accounts.txt");
				editted_accounts << entire_file;
				editted_accounts.close();
				string found_number, found_balance, found_name;
				for(int i = 0; i < 36; i++){
					if(i >= 0 && i < 5){
						found_number = found_number + found_line[i];
					} else if (i > 5 && i < 25) {
						if(line[i] == ' ' && line[i+1] == ' '){
							//Don't do anything, its a filler space
						} else {
							found_name = found_name + found_line[i];
						}
					} else if (i > 27){
						found_balance = found_balance + found_line[i];
					}
				}
				istringstream balance_buf(found_balance);
				double balance;
				balance_buf >> balance;
				istringstream number_buf(found_number);
				int account_num;
				number_buf >> account_num;
				TransactionsFile trans("06", found_name, account_num, balance);
				trans.WriteTransaction();
				cout << "Account deleted!" << endl;
			}
		} else {
			cout << "Error: Not a number entered." << endl;
		}
	} else {
		cout << "Error: Only admin can access this function." << endl;
	}

}

void Transactions::Enable(User user){
	cout << "--------------------" << endl;
	cout << "|      Enable      |" << endl;
	cout << "--------------------" << endl;
	//check admin privileges
	string name_input;
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
		//Ask for the user to enter an account number
		int account_input;
		string account_entry;
		cout << "Please enter the account number: ";
		cin >> account_entry;
		cout << account_entry << endl;
		if(IsInteger(account_entry) == true){
			istringstream account_buf(account_entry);
			account_buf >> account_input;
			//Call ReadAccount to check if an account with that name and number exist
			User found_user = ReadAccount(name_input, account_input);
			//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with withdrawal
			if (found_user.GetName().compare("ERROR") == 0) {
				cout << "Error: Account not found." << endl;
			} else if (found_user.GetStatus() == 'A') {
				cout << "Error: Account already active." << endl;
			} else {
				found_user.SetStatus();
				UpdateDay(found_user);
				TransactionsFile trans("09", found_user.GetName(), found_user.GetNumber(), found_user.GetBalance());
				trans.WriteTransaction();
				cout << "Account enabled!" << endl;
			}
		} else {
			cout << "Error: Not a number entered." << endl;
		}
	} else {
		cout << "Error: Only admin can access this function." << endl;
	}
}

void Transactions::Disable(User user){
	cout << "--------------------" << endl;
	cout << "|      Disable     |" << endl;
	cout << "--------------------" << endl;
	//check admin privileges
	string name_input;
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
		//Ask for the user to enter an account number
		int account_input;
		string account_entry;
		cout << "Please enter the account number: ";
		cin >> account_entry;
		cout << account_entry << endl;
		if(IsInteger(account_entry) == true){
			istringstream account_buf(account_entry);
			account_buf >> account_input;
			//Call ReadAccount to check if an account with that name and number exist
			User found_user = ReadAccount(name_input, account_input);
			//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with withdrawal
			if (found_user.GetName().compare("ERROR") == 0) {
				cout << "Error: Account not found." << endl;
			} else if (found_user.GetStatus() == 'D') {
				cout << "Error: Account already disabled." << endl;
			} else {
				found_user.SetStatus();
				UpdateDay(found_user);
				TransactionsFile trans("07", found_user.GetName(), found_user.GetNumber(), found_user.GetBalance());
				trans.WriteTransaction();
				cout << "Account disabled!" << endl;
			}
		} else {
			cout << "Error: Not a number entered." << endl;
		}
	} else {
		cout << "Error: Only admin can access this function." << endl;
	}
}

void Transactions::ChangePlan(User user){
	cout << "--------------------" << endl;
	cout << "|    Change Plan   |" << endl;
	cout << "--------------------" << endl;
	//check admin privileges
	string name_input;
	if (user.GetName() == "ADMIN") {
		cout << "Please enter the account name: ";
		cin.ignore(1, '\n');
		getline(cin, name_input);
		cout << name_input << endl;
		transform(name_input.begin(), name_input.end(), name_input.begin(), ::toupper);
		//Ask for the user to enter an account number
		int account_input;
		string account_entry;
		cout << "Please enter the account number: ";
		cin >> account_entry;
		cout << account_entry << endl;
		if(IsInteger(account_entry) == true){
			istringstream account_buf(account_entry);
			account_buf >> account_input;
			//Call ReadAccount to check if an account with that name and number exist
			User found_user = ReadAccount(name_input, account_input);
			//If ReadAccount returns an account with name "ERROR", then the account doesn't exist. otherwise, continue with withdrawal
			if (found_user.GetName().compare("ERROR") == 0) {
				cout << "Error: Account not found." << endl;
			} else if (found_user.GetStatus() == 'D') {
				cout << "Error: Cannot change plan of disabled account." << endl;
			} else {
				found_user.SetPlan();
				if (found_user.GetPlan() == "NP") {
					found_user.SetPlan();
					cout << "Account is now a normal account." << endl;
				} else {
					found_user.SetPlan();
					cout << "Account is now a student account." << endl;
				}
				TransactionsFile trans("08", found_user.GetName(), found_user.GetNumber(), found_user.GetBalance());
				trans.WriteTransaction();
			}
		} else {
			cout << "Error: Not a number entered." << endl;
		}
	} else {
		cout << "Error: Only admin can access this function." << endl;
	}
}

User Transactions::ReadAccount(string name, int account){
	ifstream accounts("accounts.txt");
	ifstream touched_accounts("daily_changes.txt");
	string line;
	//Look through the daily account changes and see if the account is there. if it is, pull information from that.
	while(getline(touched_accounts, line)){
		string account_name;
		string account_status;
		string account_balance;
		string account_to_check;
		string account_new_balance;
		//Go through each line and process the information in it.
		for(int i = 0; i < line.length(); i++){
			if(i >= 0 && i < 5){
				account_to_check = account_to_check + line[i];
			} else if (i > 5 && i < 25) {
				if(line[i] == ' ' && line[i+1] == ' '){
					//Don't do anything, its a filler space
				} else {
					account_name = account_name + line[i];
				}
			} else if (i > 25 && i < 27){
				account_status = account_status + line[i];
			} else if (i > 27 && i < 36){
				account_balance = account_balance + line[i];
			} else if (i > 36){
				account_new_balance = account_new_balance + line[i];
			}
		}
		istringstream balance_buf(account_balance);
		double balance;
		balance_buf >> balance;
		istringstream number_buf(account_to_check);
		int account_num;
		number_buf >> account_num;
		istringstream new_balance_buf(account_new_balance);
		double new_balance;
		new_balance_buf >> new_balance;
		//If the account is the account we are looking for, put its information into a user and return it.
		if(name.compare("") == 0){
			if(account_num == account){
				User found_account(account_num, account_name, balance);
				found_account.UpdateNewBalance(new_balance);
				if(account_status.compare("D") == 0){
					found_account.SetStatus();
				}
				return found_account;
			}
		} else {
		  	if(account_num == account && account_name.compare(name) == 0){
				User found_account(account_num, account_name, balance);
				found_account.UpdateNewBalance(new_balance);
				if(account_status.compare("D") == 0){
					found_account.SetStatus();
				}
				return found_account;
			}
		}
	}
	//If not in the daily accounts, look through the accounts file and see if the account is there.
	while(getline(accounts, line)){
		string account_name;
		string account_status;
		string account_balance;
		string account_to_check;
		//Process the information pulled from the account line
		for(int i = 0; i < line.length(); i++){
			if(i >= 0 && i < 5){
				account_to_check = account_to_check + line[i];
			} else if (i > 5 && i < 25) {
				if(line[i] == ' ' && line[i+1] == ' '){
					//Don't do anything, its a filler space
				} else {
					account_name = account_name + line[i];
				}
			} else if (i > 25 && i < 27){
				account_status = account_status + line[i];
			} else if (i > 27){
				account_balance = account_balance + line[i];
			}
		}
		istringstream balance_buf(account_balance);
		double balance;
		balance_buf >> balance;
		istringstream number_buf(account_to_check);
		int account_num;
		number_buf >> account_num;
		//If account is found, return a user that contains its information.
	  	if(name.compare("") == 0){
			if(account_num == account){
				User found_account(account_num, account_name, balance);
				found_account.UpdateNewBalance(balance);
				if(account_status.compare("D") == 0){
					found_account.SetStatus();
				}
				return found_account;
			}
		} else {
		  	if(account_num == account && account_name.compare(name) == 0){
				User found_account(account_num, account_name, balance);
				found_account.UpdateNewBalance(balance);
				if(account_status.compare("D") == 0){
					found_account.SetStatus();
				}
				return found_account;
			}
		}
	}
	//If no account with that name/number is found, return an error user.
	User error(0,"ERROR",0);
	return error;
}

void Transactions::UpdateDay(User changed_user){
	//Load the daily changes text file that stores all changed accounts for the day
	ifstream touched_accounts("daily_changes.txt");
	ofstream daily_changes;
	string line;
	string entire_file = "";
	string found_line = "NULL";
	//Parse through the file and search for a line matching the change user
	while(getline(touched_accounts, line)){
		//Store each line in an entire file line. Important for later
		entire_file = entire_file + line + "\n";
		string account_name;
		string account_status;
		string account_balance;
		string account_to_check;
		string account_new_balance;
		//Go through each line and process the information in it.
		for(int i = 0; i < line.length(); i++){
			if(i >= 0 && i < 5){
				account_to_check = account_to_check + line[i];
			} else if (i > 5 && i < 25) {
				if(line[i] == ' ' && line[i+1] == ' '){
					//Don't do anything, its a filler space
				} else {
					account_name = account_name + line[i];
				}
			} else if (i > 25 && i < 27){
				account_status = account_status + line[i];
			} else if (i > 27 && i < 36){
				account_balance = account_balance + line[i];
			} else if (i > 36){
				account_new_balance = account_new_balance + line[i];
			}
		}
		istringstream number_buf(account_to_check);
		int account_num;
		number_buf >> account_num;
		//If we find a matching user, store the found user for later.
		if(changed_user.GetNumber() == account_num && account_name.compare(changed_user.GetName()) == 0){
			found_line = line;
		}
	}
	//Remove the last newline from the file.
	entire_file = entire_file.substr(0,entire_file.length()-1);
	//If we didn't find the account, format a string correctly containing the users information and put it in their with the new balance.
	//If we did find the account, update the balance of the account in the file.
	if(found_line == "NULL"){
		daily_changes.open("daily_changes.txt", ios::app);
		string found_name = changed_user.GetName();
		if(changed_user.GetName().length() < 20){
			for(int i = changed_user.GetName().length(); i < 19; i++){
				found_name.insert(found_name.length()," ");
			}
		}
		stringstream status_buf;
		status_buf << changed_user.GetStatus();
		string found_status = status_buf.str();

		stringstream balance_buf;
		balance_buf << setprecision(2) << fixed << changed_user.GetBalance();
		string str_balance = balance_buf.str();
		if(str_balance.length() < 8){
			for(int i = 0; i < 9-str_balance.length(); i++){
				str_balance.insert(0,"0");
			}
		}
		int account_length;
		int num_length = changed_user.GetNumber();
		//For each time the number is succesfully divided by 10, increase the integer length.
		if(changed_user.GetNumber() > 0){
			for(account_length = 0; num_length > 0; account_length++){
				num_length = num_length / 10;
			}
		}
		//Make a string stream to type cast the integer to a string
		stringstream account_buf;
		account_buf << changed_user.GetNumber();
		string str_number = account_buf.str();
		//If the length of account number < 5, pad 0's until length is 5.
		if(account_length < 5){
			for(int i = 0; i < 5-account_length; i++){
				str_number.insert(0,"0");
			}
		}

		stringstream new_balance_buf;
		new_balance_buf << setprecision(2) << fixed << changed_user.GetNewBalance();
		string str_new_balance = balance_buf.str();
		if(str_new_balance.length() < 8){
			for(int i = 0; i < 9-str_new_balance.length(); i++){
				str_new_balance.insert(0,"0");
			}
		}

		string new_line = str_number + " " + found_name + " " + found_status + " " + str_balance + " " + str_new_balance + "\n";
		daily_changes << new_line;
		daily_changes.close();
	} else {
		//Format the new balance correctly
		stringstream balance_buf;
		balance_buf << setprecision(2) << fixed << changed_user.GetBalance();
		string str_balance = balance_buf.str();
		if(str_balance.length() < 8){
			for(int i = 0; i < 9-str_balance.length(); i++){
				str_balance.insert(0,"0");
			}
		}

		stringstream new_balance_buf;
		new_balance_buf << setprecision(2) << fixed << changed_user.GetNewBalance();
		string str_new_balance = new_balance_buf.str();
		if(str_new_balance.length() < 8){
			for(int i = 0; i < 9-str_new_balance.length(); i++){
				str_new_balance.insert(0,"0");
			}
		}
		//Change the balance of the account to the new account
		string original_line = found_line;
		found_line.replace(28,8,str_balance);
		found_line.replace(37,8,str_new_balance);
		//Go through the string containg the entire file and replace the line we had with the new editted line.
		entire_file.replace(entire_file.find(original_line), 46, found_line);
		//Replace the old daily changes file with the new, editted daily changes file using the editted entire file string.
		daily_changes.open("daily_changes.txt");
		daily_changes << entire_file << "\n";
		daily_changes.close();
	}
}

User Transactions::Logout(User user){
	User out(00000, "NULL", 0);
	TransactionsFile trans("00", user.GetName(), user.GetNumber(), user.GetBalance());
	cout << "Logout Successful" << endl;
	return out;
}