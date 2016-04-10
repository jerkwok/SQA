#include "User.h"
#include "Transactions.h"

#include <string>
#include <iostream>
#include <algorithm>

using namespace std;

int main(){
	//Set the inital user as not logged in.
	User active_user(00000, "NULL", 0);
	while(true){
		string input;
		Transactions trans;
		//Take in user input
		cout << "Please enter a command: ";
		cin >> input;
		cout << input << endl;
		transform(input.begin(), input.end(), input.begin(), ::tolower);
		//Check if user is logged in or not.
		if(active_user.GetName() == "NULL"){
			//If user is not logged in, only accept login as a command
			if(input == "login"){
				cout << "Please enter the session type: ";
				cin >> input;
				cout << input << endl;
				transform(input.begin(), input.end(), input.begin(), ::tolower);
				//If they enter admin, log them in as admin. Otherwise, ask for name and search for that user.
				if(input == "admin"){
					active_user = trans.Login("admin");
				} else if (input == "standard") {
					cout << "Please enter the account name: ";
					cin.ignore(1, '\n');
					getline(cin, input);
					cout << input << endl;
					transform(input.begin(), input.end(), input.begin(), ::toupper);
					active_user = trans.Login(input);
				} else {
					cout << "Error: Please enter 'standard' or 'admin' as your session type." << endl;
				}
			} else if (input == "exit"){
				return 0;
			} else {
				cout << "Please login first before issuing commands" << endl;
			}
		} else {
			//Parse user input and call correct command.
			if(input == "login"){
				cout << "Error: Already logged into account" << endl;
			} else if (input == "withdrawal"){
				trans.Withdrawal(active_user);
			} else if (input == "transfer"){
				trans.Transfer(active_user);
			} else if (input == "paybill"){
				trans.PayBill(active_user);
			} else if (input == "deposit"){
				trans.Deposit(active_user);
			} else if (input == "create"){
				if(active_user.GetName() == "ADMIN"){
					trans.Create(active_user);
				} else {
					cout << "Error: Only admin can access this function." << endl;
				}
			} else if (input == "delete"){
				if(active_user.GetName() == "ADMIN"){
					trans.Delete(active_user);
				} else {
					cout << "Error: Only admin can access this function." << endl;
				}
			} else if (input == "disable"){
				if(active_user.GetName() == "ADMIN"){
					trans.Disable(active_user);
				} else {
					cout << "Error: Only admin can access this function." << endl;
				}
			} else if (input == "enable"){
				if(active_user.GetName() == "ADMIN"){
					trans.Enable(active_user);
				} else {
					cout << "Error: Only admin can access this function." << endl;
				}
			} else if (input == "changeplan"){
				if(active_user.GetName() == "ADMIN"){
					trans.ChangePlan(active_user);
				} else {
					cout << "Error: Only admin can access this function." << endl;
				}
			} else if (input == "logout"){
				active_user = trans.Logout(active_user);
			} else if (input == "exit"){
				return 0;
			} else {
				cout << "Error: Not a valid command." << endl;
			}
		}
	}
}