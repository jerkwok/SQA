#ifndef TRANSACTIONS_H
#define TRANSACTIONS_H
#include "User.h"

#include <string>

using namespace std;

class Transactions{
private:
	string transaction_id_;

public:
	Transactions();
	bool IsInteger(string check_string);
	bool IsDouble(string check_string);
	User Login(string name);
	User Logout(User user);
	void Withdrawal(User user);
	void Deposit(User user);
	void Transfer(User user);
	void PayBill(User user);
	void Create(User user);
	void Delete(User user);
	void Enable(User user);
	void Disable(User user);
	void ChangePlan(User user);
	User ReadAccount(string name, int account);
	void UpdateDay(User changed_user);
};

#endif