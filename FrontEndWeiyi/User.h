#ifndef USER_H
#define USER_H

#include <string>

using namespace std;

class User{
private:
	int account_number_;
	string account_name_;
	char account_status_;
	string plan_type_;
	double balance_;
	double new_balance_;

public:
	User(int account_number, string account_name, double balance);
	string GetName() { return account_name_; }
	int GetNumber() { return account_number_; }
	char GetStatus() { return account_status_; }
	string GetPlan() { return plan_type_; }
	double GetBalance() { return balance_; }
	double GetNewBalance() { return new_balance_; }
	void SetStatus();
	void SetPlan();
	void UpdateBalance(double change);
	void UpdateNewBalance(double change);
};

#endif