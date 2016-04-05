//
// Sunday, Feburary 21st, 2016
//
// Account.cpp
//
// Account class represents a single account in the system
// Keeps track of things like who owns it, the balance
// and various statuses of the account
//
//

#include "Account.h"
#include <string.h>
#include <iostream>


using namespace std;

//Constructor intializes some of the account variables
Account::Account(void){
  number = "";
  holder = "";
  active = true;
  balance = 0.0;
  student = true;
  available = true;
}