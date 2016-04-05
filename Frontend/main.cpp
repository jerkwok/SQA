//
// Sunday, Feburary 21st, 2016
//
// main.cpp
//
// This program is the front end console for the SQA ATM project.
// It mimics an ATM in a console program
// input: loads CurrentBankAccounts.txt as a text file 
//        that contains the bank accounts
// output: outputs a file called output.txt
//         that contains a log of the transactions
//         performed during the login session
// To run: type make into the command window when you're
//         in the folder containing the program. 
//         then run main.out.
//          first argument needs to be the CurrentBankAccounts file, 
//         second argument is the output file the front end writes to.

#include <iostream>
#include <fstream>
#include <string.h>
#include <vector>
#include <stdio.h>
#include <stdlib.h>
#include <iomanip>
#include <sstream>
#include "Account.h"
// #include "Accountsystemstate.h"
#include "State.h"

using namespace std;

//State variable used
State systemstate;

//systemstate class used
// Accountsystemstate systemstate;

//Prints an account's details to the console. Mostly for testing purposes.
void printAccount(Account account){
  cout << "Account Number:" << account.number << endl;
  cout << "Account Holder:" << account.holder << ";" << endl;
  cout << "Account Status:";
  if (account.active == 1){
    cout << "Active";
  }else{
    cout << "Disabled";
  }
  cout << endl;
  cout << "Account Balance:" << account.balance << endl;
  cout << "Account Status:";
  if (account.student){
    cout << "Student";
  }else{
    cout << "Non Student";
  }
  cout << endl;
}

//Prints all the accounts in the accountlist vector to the console.
void printAllAccounts(){
  for (int i = 0; i < systemstate.accountlist.size(); i++){
    printAccount(systemstate.accountlist.at(i));
  }
}

std::string& trim(
  std::string&       s)
{
  return s.erase( s.find_last_not_of( " \n\r" ) + 1 );
}


int main (int argc, char* argv[])
{
  string cmd;
  //load current bank accounts file
  // systemstate.loadaccounts("CurrentBankAccounts.txt");

  // cout << "account:" << endl;
  // cout << systemstate.accountlist.size() << endl;
  // for (int i = 0; i < systemstate.accountlist.size(); i++){
  //   cout << "printing:" << endl;
  //   printAccount(systemstate.accountlist.at(i));
  // }

  //opens the file
  string line;

  //load current bank accounts file
  // string filename = "CurrentBankAccounts.txt";
  if (argc != 3){
    cout << "Program requires 2 arguments" << endl;
    return 0;
  }
  
  string filename = argv[1];
  ifstream myfile (filename.c_str());

  if (myfile.is_open()){
    Account tmp;
    while ( getline (myfile,line) ){
      // cout << line << '\n';
      //convert the line to c string
      char *cline = (char*)line.c_str();

      //Start reading tokens, seperated by spaces

      //First token is number
      // char * tok = strtok(cline, " ");
      // tmp.number = tok;

      tmp.number = line.substr(0,5);

      //Second token is name of the holder of the account.
      //TODO: Currently we set the delimiter as a space, making spaces in names not supported.
      // tok = strtok(NULL, "  ");
      // tmp.holder = tok;

      tmp.holder = line.substr(6,20);
      trim(tmp.holder);

      //Third token is A or D for enabled/disabled account.
      // tok = strtok(NULL, " ");

      // cout << line.substr(26,1) << endl;
      if (line.substr(26,1).compare("A") == 0){
        tmp.active = 1;
      }else{
        tmp.active = 0;
      }

      //Fourth token is Balance for the account
      // tok = strtok(NULL, " ");
      // tmp.balance = atof(tok);
      tmp.balance = stof(line.substr(28,8));

      //Fifth token is student/non student account
      // tok = strtok(NULL, " ");

      if (line.substr(37,1).compare("S") == 0){
        tmp.student = true;
      }else{
        tmp.student = false;
      }

      tmp.available = true;

      //adds the temporary account to the accountlist
      systemstate.accountlist.push_back(tmp);
    }
    myfile.close();



  // printAllAccounts();



  }
  else {
    cout << "Unable to open file" << endl;
  }

  do{
    cout << "Please enter a command: " << endl;
    getline(cin, cmd);
    trim(cmd);
    // cout << "Command: ;" << cmd << ";" << endl;
    // cout << systemstate.loggedin << endl;
    if (cmd.compare("exit") == 0){
      return 0;
    }

    if(cmd.compare("login") == 0){
      // cout << systemstate.loggedin << endl;
      // systemstate.loggedin = 1;
      systemstate.login();
      // cout << systemstate.loggedin << endl;
    }else{
      if (!systemstate.loggedin){
        cout << "Not logged in." << endl;
      }else{
        if(cmd.compare("withdrawal") == 0){
          systemstate.withdrawal();
        }else if(cmd.compare("transfer") == 0){
          systemstate.transfer();
        }else if(cmd.compare("paybill") == 0){
          systemstate.paybill();
        }else if(cmd.compare("deposit") == 0){
          systemstate.deposit();
        }else if(cmd.compare("create") == 0){
          systemstate.create();
        }else if(cmd.compare("delete") == 0){
          systemstate.deleteaction();
        }else if(cmd.compare("disable") == 0){
          systemstate.disable();
        }else if(cmd.compare("enable") == 0){
          systemstate.enable();
        }else if(cmd.compare("changeplan") == 0){
          systemstate.changeplan();
        }else if(cmd.compare("logout") == 0){
            // cout << "filename:" << argv[2] << endl;
          systemstate.logout(argv[2]);
        }else{
          cout << "command not recognized" << endl;
        } 
        // systemstate.printtransactions();
      }
   }
  }while(true);

  return 0;
}