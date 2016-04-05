//
// Sunday, Feburary 21st, 2016
//
// State.cpp
//
// The state class controls the flow of the program and keeps track of login 
// variables.
//
//
//

#include "State.h"
#include <string.h>
#include <iostream>
#include <regex>


using namespace std;

//Constructor intializes some of the state variables
State::State(void){
  loggedin = 0;
  sessionwithdrawn = 0;
  sessiontransfer = 0;
  sessionECbill = 0;
  sessionCQbill = 0;
  sessionTVbill = 0;
}

//test function
void State::printtransactions(){
  cout << "Transactions:" << endl;
  for(std::vector<int>::size_type i = 0; i != this->transactions.size(); i++) {
    cout << this->transactions.at(i) << endl;
  }
}

//writes all the transactions in the transaction vector to the output file
void State::writetransactions(string filename){
  // cout << "writing" << endl;
  //create the ofstream object and open the file
  ofstream ofile;
  ofile.open (filename);
  if (ofile.is_open()){
    //write each of the objects in the transactions vector to the file
    for (int i = 0; i < this->transactions.size(); i++){
      ofile << this->transactions.at(i);
    }
    //close the file
    ofile.close();
  }else{
    cout << "unable to open output file" << endl;
  }
}

//adds a transaction to the transactions vector
void State::addtransaction(int code, string name, string account, float funds, string misc, string admin){
  string line = "";
  string spaces = "";

  //add transaction code followed by a space
  if (code < 10){
    line = line + "0" + to_string(code) + " ";
  }else{
    line = line + to_string(code) + " ";
  }

  //add account holder's name
  //reset spaces string
  spaces = "";
  //pad spaces
  for (int i = 0; i < 20 - name.length(); i++){
    spaces = spaces + " ";
  }
  line = line + name + spaces + " ";

  //add bank account number

  //reset spaces string
  spaces = "";

  //pad spaces
  for (int i = 0; i < 5 - account.length(); i++){
    spaces = spaces + "0";
  }
  line = line + spaces + account + " ";

  stringstream stream;
  stream << fixed << setprecision(2) << funds;

  //add funds involved
  
  //reset spaces string
  spaces = "";
  
  //pad spaces
  for (int i = 0; i < 8 - (stream.str()).length(); i++){
    spaces = spaces + "0";
  }
  line = line + spaces+ stream.str() + " ";

  //add misc info
  //pad spaces
  if (misc.length() == 2){
    misc = misc + " ";
  }

  line = line + misc;

  //add admin/non admin
  // if (admin.length() == 1){
  //   admin = admin + " ";
  // }
  line = line + admin;

  //add line break
  line = line + "\n";

  // cout << "line: " << line << endl;

  //add the transaction to the transactions vector
  this->transactions.push_back(line);
}

// Loads accounts from the Current Bank Accounts file.
void State::loadaccounts(string filename){
  //opens the file
  string line;
  ifstream myfile (filename.c_str());

  if (myfile.is_open()){
    Account tmp;
    while ( getline (myfile,line) ){
      // cout << line << '\n';
      //convert the line to c string
      char *cline = (char*)line.c_str();

      //Start reading tokens, seperated by spaces

      //First token is number
      char * tok = strtok(cline, " ");
      tmp.number = tok;

      //Second token is name of the holder of the account.
      //TODO: Currently we set the delimiter as a space, making spaces in names not supported.
      tok = strtok(NULL, " ");
      tmp.holder = tok;

      //Third token is A or D for enabled/disabled account.
      tok = strtok(NULL, " ");

      if (tok[0] == 'A'){
        tmp.active = 1;
      }else{
        tmp.active = 0;
      }

      //Fourth token is Balance for the account
      tok = strtok(NULL, " ");
      tmp.balance = atof(tok);

      //Fifth token is student/non student account
      tok = strtok(NULL, " ");

      if (tok[0] == 'S'){
        tmp.student = true;
      }else{
        tmp.student = false;
      }

      tmp.available = true;

      //adds the temporary account to the accountlist
      // cout << "pushing:" << endl;
      this->accountlist.push_back(tmp);
    }

    for (int i = 0; i < this->accountlist.size(); i++){
    // cout << "printing:" << endl;
    // cout << this->accountlist.at(i).number << endl;
    }
    myfile.close();
  }

  else {
    cout << "Unable to open file";
  }
}

void State::login(){
  string input;    
  string admin;
  // cout << "login status: " << loggedin << endl;

  //don't allow login when logged in
  if (loggedin){
    cout << "Already logged in" << endl;
  }else{
    cout << "Enter session type" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );
    if(input.compare("admin") == 0){
      sessiontype = 1;
      admin = "A ";
    }else if(input.compare("standard") == 0){
      cout << "Enter account holder's name" << endl;
      getline(cin, input);
      input.erase( input.find_last_not_of( "\n\r" ) + 1 );
      name = input;
      sessiontype = 0;
      admin = "S ";
    }else{
      cout << "input not recognized:" << input << endl;
      return;
    }
    loggedin = 1;
    cout << "Successful Login" << endl;

    addtransaction(10, name, "00000", 00000.00, admin, "N");        
    // cout << "login status: " << loggedin << endl;
  }
}

//Withdrawal transaction
void State::withdrawal(){
  string input;
  string destaccount;
  float amount;
  //accountposition is where in the accountlist vector the inputted account is.
  int accountposition = -1;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( "\n\r" ) + 1 );
    name = input;
  }

  //ask for account number
  cout << "Enter account number" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  destaccount = input;

  //Try and find the account number in the list.
  for (int i = 0; i < accountlist.size(); i++){
    //If the number matches
    if (accountlist.at(i).number.compare(destaccount) == 0){
      //set account position
      accountposition = i;
      //If the account was just created
      if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
      }

      //If the account's name doesn't match ours
      if (sessiontype && accountlist.at(i).holder.compare(name) != 0){
        cout << "account holder must match account number" << endl;
        return;
      }
    }
  }

  //If we haven't found the account, account position will still be -1
  if (accountposition == -1){
    cout << "Account number not found" << endl;
    return;
  }

  //ask for amount
  cout << "Enter amount to withdraw" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  amount = stof(input);

  //check if it matches the number pattern
  if (input.substr(input.length()-3,1).compare(".") != 0){
    cout << "Malformed number input" << endl;
    return;
  }

  //multiples of 5
  if((int)amount % 5 != 0){
    cout << "Withdrawal amounts must be in multiples of $5" << endl;
    return;
  }

  //negative
  if(amount < 0){
    cout << "Withdrawal amounts must be positive" << endl;
    return;
  }

  //testing withdrawal limit
  if(!sessiontype){
    if (amount + sessionwithdrawn > 500){
      cout << "Session withdrawal limit reached" << endl;
      return;
    }
  }

  //check if amount would put into negative
  if (amount > accountlist.at(accountposition).balance){
    cout << "Amount is greater than balance in account" << endl;
    return;
  }


  //Check if the account is a student account
  string student;
  if(accountlist.at(accountposition).student){
    student = "S";
  }else{
    student = "N";
  }

  //one last check to see if the account holder matches name
  if (accountlist.at(accountposition).holder.compare(name) == 0){
    //subtract withdrawal from systemstate
    accountlist.at(accountposition).balance -= amount;
    sessionwithdrawn+=amount;
    //add the transaction
    addtransaction(1, name, destaccount, amount, "00", student);        
  }else{
    cout << "account holder must match account number" << endl;
  }
}

//Transfer money from one account to another
void State::transfer(){
  string input;
  string sourceaccount;
  string destaccount;
  float fees;
  float amount;
  //accountposition is where in the accountlist vector the inputted account is.
  int accountposition = -1;
  int destaccountposition = -1;

  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( "\n\r" ) + 1 );
    name = input;
  }

  //ask for source account number
  cout << "Enter account number for source account" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  sourceaccount = input;

  //Try and find the account number in the list.
  for (int i = 0; i < accountlist.size(); i++){
    //If the number matches
    if (accountlist.at(i).number.compare(sourceaccount) == 0){
      //set account position
      accountposition = i;
      //If the account was just created
      if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
      }

      //If the account's name doesn't match ours
      if (accountlist.at(i).holder.compare(name) != 0){
        cout << "account holder must match account number" << endl;
        return;
      }
    }
  }

  //If we haven't found the account, account position will still be -1
  if (accountposition == -1){
    cout << "Account number not found" << endl;
    return;
  }

  //ask for destination account number
  cout << "Enter account number of destination account" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  destaccount = input;

  //repeat the above process for the destination account
  for (int i = 0; i < accountlist.size(); i++){
    if (accountlist.at(i).number.compare(destaccount) == 0){
      destaccountposition = i;
      if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
      }
    }
  }

  if (destaccountposition == -1){
    cout << "Destination account number not found" << endl;
    return;
  }

  //ask for amount
  cout << "Enter amount to transfer" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  amount = stof(input);

  //check if it matches the number pattern
  if (input.substr(input.length()-3,1).compare(".") != 0){
    cout << "Malformed number input" << endl;
    return;
  }

  //negative
  if(amount < 0){
    cout << "Transfer amounts must be positive" << endl;
    return;
  }

  //Check if the account is a student account
  string student;
  if(accountlist.at(accountposition).student){
    student = "S";
    fees = 0.05;
  }else{
    student = "N";
    fees = 0.10;
  }

  //check for session transfer limit
  if(!sessiontype){
    if (amount + sessiontransfer > 1000){
      cout << "Session transfer limit reached" << endl;
      return;
    }

    cout << accountlist.at(accountposition).balance - fees << endl;
    if (amount > (accountlist.at(accountposition).balance - fees)) {
      cout << "Source account has insufficient funds" << endl;
      return;      
    }
  }else{
    //check if the source account has enough money
    if(amount > accountlist.at(accountposition).balance){
      cout << "Source account has insufficient funds" << endl;
      return;
    }
  }

  //Check if the dest account is a student account
  string deststudent;
  if(accountlist.at(destaccountposition).student){
    student = "S";
  }else{
    student = "N";
  }

  //one last check to see if the account holder matches name
  if (accountlist.at(accountposition).holder.compare(name) == 0){
    //remove money from the systemstate source account
    accountlist.at(accountposition).balance -= amount;
    //add money from the systemstate destination account
    accountlist.at(destaccountposition).balance += amount;
    //add the transactions
    addtransaction(2, name, sourceaccount, amount, "00",student);        
    addtransaction(2, name, destaccount, amount, "00", deststudent); 
    sessiontransfer = amount + sessiontransfer;
  }else{
    cout << "account holder must match account number" << endl;
  }
}

//Pay a bill from an account
void State::paybill(){
  string input;
  string sourceaccount;
  float amount;
  string company;
  //accountposition is where in the accountlist vector the inputted account is.
  int accountposition = -1;;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( "\n\r" ) + 1 );
    name = input;
  }

  //ask for account number
  cout << "Enter account number" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  sourceaccount = input;

  //find the account
  for (int i = 0; i < accountlist.size(); i++){
    if (accountlist.at(i).number.compare(sourceaccount) == 0){
      accountposition = i;
      //check if the account is newly created
      if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
      }

      //check if the account holder matches the account number
      if (accountlist.at(i).holder.compare(name) != 0){
        cout << "account holder must match account number" << endl;
        return;
      }
    }
  }

  //if we didn't find the account then account position has not been changed
  if (accountposition == -1){
    cout << "Account number not found" << endl;
    return;
  }

  //ask for company to pay to
  cout << "Enter company to pay to" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  company = input;

  //check for incorrect input
  if ((company.compare("EC") != 0) && (company.compare("CQ") != 0) && (company.compare("TV") != 0)){
    cout << "Company not recognized" << endl;
    return;
  }

  //ask for amount
  cout << "Enter amount to pay" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( "\n\r" ) + 1 );
  amount = stof(input);

  //check if it matches the number pattern
  if (input.substr(input.length()-3,1).compare(".") != 0){
    cout << input.substr(input.length()-3,1) << endl;
    cout << "Malformed number input" << endl;
    return;
  }

  //check for bill payment limits, only if we aren't admin
  if(!sessiontype){
    if (company.compare("EC") == 0){
      if (amount + sessionECbill > 2000){
        cout << "Session bill payment for EC limit reached" << endl;
        return;
      }
    }else if (company.compare("CQ") == 0){
      if (amount + sessionCQbill > 2000){
        cout << "Session bill payment for CQ limit reached" << endl;
        return;
      }
    }else if (company.compare("TV") == 0){
      if (amount + sessionTVbill > 2000){
        cout << "Session bill payment for TV limit reached" << endl;
        return;
      }
    }

    //check for max payment
    if (amount > 2000){
      cout << "max payment is 2000" << endl;
      return;
    }
  }

  //Check if the account is a student account
  string student;
  if(accountlist.at(accountposition).student){
    student = "S";
  }else{
    student = "N";
  }

  if (accountlist.at(accountposition).holder.compare(name) == 0){
    //remove the money from the account
    accountlist.at(accountposition).balance -= amount;
    //add the transaction
    addtransaction(4, name, sourceaccount, amount, company, student);        
  }else{
    cout << "account holder must match account number" << endl;
  }
}

//Deposit funds into an account
void State::deposit(){
  string input;
  string destaccount;
  float amount;
  //accountposition is where in the accountlist vector the inputted account is.
  int accountposition = -1;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );
    name = input;
  }

  //ask for account number
  cout << "Enter account number" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( " \n\r" ) + 1 );
  destaccount = input;

  for (int i = 0; i < accountlist.size(); i++){
    if (accountlist.at(i).number.compare(destaccount) == 0){
      accountposition = i;
      //check if the account is available
      if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
      }

      //check if the account number matches the given name
      if (accountlist.at(i).holder.compare(name) != 0){
        cout << "account holder must match account number" << endl;
        return;
      }
    }
  }

  //if we didn't find the account accountposition didn't change
  if (accountposition == -1){
    cout << "Account number not found" << endl;
    return;
  }

  //ask for amount
  cout << "Enter amount to deposit" << endl;
  getline(cin, input);
  input.erase( input.find_last_not_of( " \n\r" ) + 1 );
  cout << "input:" << input << endl; 
  //check if it matches the number pattern
  if (input.substr(input.length()-3,1).compare(".") != 0){
    cout << "Malformed number input" << endl;
    return;
  }

  cout << fixed << setprecision(2) << input << endl;
  amount = (float) stof(input);
  cout << fixed << setprecision(2) << amount << endl;

  cout << "amt:" << amount << endl;

  //deposits must be positive
  if (amount < 0){
    cout << "Amount deposited must be positive" << endl;
    return;
  }

  //deposits must not push a balance over maximum
  // cout << accountlist.at(accountposition).balance << " " << amount << " " << endl;
  // cout <<  accountlist.at(accountposition).balance + amount << endl;
  if(accountlist.at(accountposition).balance + amount >= 100000){
    cout << "Deposit would cause balance to exceed maximum" << endl;
    return;
  }

  //Check if the account is a student account
  string student;
  if(accountlist.at(accountposition).student){
    student = "S";
  }else{
    student = "N";
  }

  if (accountlist.at(accountposition).holder.compare(name) == 0){
    //add the amount to the accountlist
    accountlist.at(accountposition).balance += amount;
    //add the transaction
    addtransaction(4, name, destaccount, amount, "00", student);        
  }else{
    cout << "account holder must match account number" << endl;
  }
}

//Create a new account
void State::create(){
  string input;
  float newbalance;
  bool found;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    name = input;

    //truncate a name that's too long
    if (name.length() > 20){
      name = name.substr(0,20);
    }else{
      name.erase( input.find_last_not_of( " \n\r" ) + 1 );
    }
    cout << name << endl;

    //ask for account number
    cout << "Enter inital balance" << endl;
    getline(cin, input);
    newbalance = stof(input);

    //check if it matches the number pattern
    if (input.substr(input.length()-3,1).compare(".") != 0){
      cout << "Malformed number input" << endl;
      return;
    }

    //check if the balance is too large
    if (newbalance > 99999.99){
      cout << "Balance exceeds maximum" << endl;
      return;
    }

    //grab last account number
    int newaccountnumber; 
    int lastnumber = stoi(accountlist.at(accountlist.size()-1).number) + 1;
    //add the transaction
    addtransaction(5, name, to_string(lastnumber), newbalance, "00", "N");        

  }else{
    //only admins can do this
    cout << "Privileged action: admins only" << endl;
  }
}

//Delete an account from the accounts list
void State::deleteaction(){
  string input;
  string sourceaccount;
  bool found;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );
    name = input;

    //ask for account number
    cout << "Enter account number" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );
    sourceaccount = input;

    for (int i = 0; i < accountlist.size(); i++){
      if (accountlist.at(i).number.compare(sourceaccount) == 0){
        found = true;
        if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
        }
        if (accountlist.at(i).holder.compare(name) == 0){
          //add the transaction
          cout << "Account Deleted." << endl;
          addtransaction(6, name, sourceaccount, 0.00, "00", "N");
          accountlist.erase(accountlist.begin()+i);        
          return;
        }else{
          cout << "account holder must match account number" << endl;
        }
      }
    }

    // if (!found){
    //if we've gotten here then the account doesn't exist
      cout << "Account number not found" << endl;
    // }
  }else{
    //only admins can do this
    cout << "Privileged action: admins only" << endl;
  }
}

//Enables one account
void State::enable(){
  string input;
  string sourceaccount;
  bool found;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );
    name = input;

    //ask for account number
    cout << "Enter account number" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );    
    sourceaccount = input;

    for (int i = 0; i < accountlist.size(); i++){
      if (accountlist.at(i).number.compare(sourceaccount) == 0){
        found = true;
        //check if the account is available
        if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
        }
        //check if the account number matches the given name
        if (accountlist.at(i).holder.compare(name) == 0){
          //account must be disabled
          if (!accountlist.at(i).active){
            //add the transaction
            addtransaction(9, name, sourceaccount, 0.00, "00", "N");        
            return;
          }else{
            cout << "account needs to be disabled" << endl;
          }
        }else{
          cout << "account holder must match account number" << endl;
        }
      }
    }

    // if (!found){
    //if we've gotten to this point the account doesn't exist
      cout << "Account number not found" << endl;
    // }

  }else{
    //admin action only
    cout << "Privileged action: admins only" << endl;
  }
}

//disable an enabled account
void State::disable(){
  string input;
  string sourceaccount;
  bool found;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );    
    name = input;

    //ask for account number
    cout << "Enter account number" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( " \n\r" ) + 1 );    
    sourceaccount = input;

    //loop over account list
    for (int i = 0; i < accountlist.size(); i++){
      if (accountlist.at(i).number.compare(sourceaccount) == 0){
        // found = true;
        //check if the account is available
        if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
        }
        //check if account number matches given name
        if (accountlist.at(i).holder.compare(name) == 0){
          //account must be enabled
          if (accountlist.at(i).active){
            //add the transaction
            addtransaction(07, name, sourceaccount, 0.00, "00", "N");        
            return;
          }else{
            cout << "account needs to be enabled" << endl;
          }
        }else{
          cout << "account holder must match account number" << endl;
        }
      }
    }

    // if (!found){
      //If we haven't found the account at this point then it doesn't exist
      cout << "Account number not found" << endl;
    // }

  }else{
    //must be admin to do this
    cout << "Privileged action: admins only" << endl;
  }
}

//Changes a student plan to a non student plan
void State::changeplan(){
  string input;
  string sourceaccount;
  bool found;
  bool namefound = false;
  //if admin, ask for account holder's name
  if (sessiontype){
    cout << "Enter account holder's name" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( "\n\r" ) + 1 );
    name = input;

    //check for real names
    for (int i = 0; i < accountlist.size(); i++){
      if (accountlist.at(i).holder.compare(name) == 0){
        namefound = true;
      }
    }

    if (namefound == false){
      cout << "name does not exist" << endl;
      return;
    }

    //ask for account number
    cout << "Enter account number" << endl;
    getline(cin, input);
    input.erase( input.find_last_not_of( "\n\r" ) + 1 );
    sourceaccount = input;

    for (int i = 0; i < accountlist.size(); i++){
      if (accountlist.at(i).number.compare(sourceaccount) == 0){
        // found = true;
        //check if the account is available
        if (!accountlist.at(i).available){
          cout << "Account not available" << endl;
          return;
        }
        //check if the account number matches name
        if (accountlist.at(i).holder.compare(name) == 0){
          //account must be student
          if (accountlist.at(i).student){
            //add the transaction
            addtransaction(8, name, sourceaccount, 0.00, "00", "S");        
            return;
          }else{
            cout << "account needs to be student plan" << endl;
          }
        }else{
          cout << "account holder must match account number" << endl;
        }
      }
    }

    //If we haven't found the account at this point then it doesn't exist
    // if (!found){
      cout << "Account number not found" << endl;
    // }

  }else{
    //must be admin to do this
    cout << "Privileged action: admins only" << endl;
  }
}

//logout and write transactions to the output file
void State::logout(string filename){
  loggedin = false;
  addtransaction(0,"","",0.0,"00","N");
  writetransactions(filename);
  cout << "Successful Logout" << endl;

  //enable all accounts
  for (int i = 0; i < accountlist.size(); i++){
    accountlist.at(i).available = true;
  }

  //reset session caps
  sessionwithdrawn = 0;
  sessiontransfer = 0;
  sessionECbill = 0;
  sessionCQbill = 0;
  sessionTVbill = 0; 
}