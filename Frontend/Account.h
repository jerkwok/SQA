//
// Sunday, Feburary 21st, 2016
//
// Account.cpp
//
// header for the Account class.
// 
// Account class represents a single account in the system
// Keeps track of things like who owns it, the balance
// and various statuses of the account
//
//
//
#ifndef ACCOUNT_H
#define ACCOUNT_H

#include <string>

using namespace std;

//Contains all the data for a single account
class Account{
public:
  Account();
  //Account number, stored as a string.
  string number;
  //Name of the account holder
  string holder;
  //Whether the account is active or not. 0 means not active (disabled) and 1 means active (enabled)
  bool active;
  //The current balance of the account
  float balance;
  //Whether the account is a student account or not. True = a student account, false = non student account
  bool student;
  //Whether the account is available for transactions. Accounts created are not available until a new session.
  //False means not available for this session. True means available for this session
  bool available;
};

#endif