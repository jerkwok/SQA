//
// Sunday, Feburary 21st, 2016
//
// State.h
// This is the header file for the State class
// 
// The state class controls the flow of the program and keeps track of login 
// variables
//
//
#ifndef STATE_H
#define STATE_H

#include <string.h>
#include <vector>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <stdio.h>
#include <iostream>
#include <stdlib.h>
#include <string.h>
#include "Account.h"
// #include "AccountTransactions.h"

using namespace std;

//Contains all the data for the current state of the current session.
class State{
public:
  //Constructor
  State();
  //false means not logged in
  bool loggedin;
  //0 for standard, 1 for admin
  int sessiontype;
  //name of the current logged in user
  string name;
  //a vector containing all accounts read from the file + ones created
  vector<Account> accountlist;
  //a vector containing all the transaction codes from the session, to be written to the output file after logout
  vector<string> transactions;
  //adds a transaction to the transactions vector
  void addtransaction(int code, string name, string account, float funds, string misc, string student);
  //writes all the transactions in the transaction vector to the output file
  void writetransactions(string filename);
  //test function
  void printtransactions();
  void login();
  void logout(string filename);
  void withdrawal();
  void deposit();
  void transfer();
  void paybill();
  void changeplan();
  void enable();
  void disable();
  void create();
  void deleteaction();
  //Loads accounts from the Current Bank Accounts file.
  void loadaccounts(string filename);
  //The following keeps track of the money used in the session to enforce session limits
  int sessionwithdrawn;
  int sessiontransfer;
  int sessionECbill;
  int sessionCQbill;
  int sessionTVbill;
  // map<string, int> amount;


};

#endif