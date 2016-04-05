# General Purpose

 - Welcome to Generic Bank.\nEnter "login" to begin.
 - >
 - Enter account type (standard or admin):
 - Enter your name:
 - Enter the account holder's name:
 - Enter the account number:
 - Enter the amount ($0.01 - $99999.99): $
 - Error: Name entered is not valid.
 - Error: The command entered is invalid. Enter "help" for list of commands.
 - Error: Monetary amount entered is not valid.
 - Error: Account number entered is not valid.
 - Error: Account not found.
 - Error: Account holder not found.
 - Error: Account must be one of your own accounts.
 - Error: Balance is too high to make deposit.
 - Error: Insufficient funds.
 - The available commands are:
 - Error: Command requires admin access.
 - Error: Please login first.
 - Error: Account is disabled.


# login

 - Error: Name not found.
 - Error: You are already logged in (use "logout" to log out).
 - Enter login type ("standard" or "admin"):
 - Hello, <NAME>.


# logout

 - Goodbye, <NAME>.


# withdraw

 - Enter the amount ($5.00 - $99995.00): $
 - Error: Amount must end in 0 or 5. ($350, $420, $55, etc.)
 - Error: You can only withdraw up to $500 in a day. ($<AMOUNT> remaining)
 - Error: You have already withdrawn $500 today. Please try again another day.
 - $<AMOUNT> withdrawn successfully.


# transfer

 - Enter the account number to withdraw funds from:
 - Enter the account number to deposit funds to:
 - Error: You can only transfer up to $1000 in a day. ($<AMOUNT> remaining)
 - Error: You have already transferred $1000 today. Please try again another day.
 - $<AMOUNT> transferred successfully.


# paybill

 - Which company will you be paying? (EC, CQ, or TV):
 - Error: Company not found.
 - Payment of $<AMOUNT> to <COMPANY> made successfully.
 - Error: You can only pay up to $2000 per day per company. ($<AMOUNT> remaining)
 - Error: You have already paid <COMPANY> $2000 today. Please try again another day.


# deposit

 - $<AMOUNT> deposited successfully.\nFunds will be available tomorrow.


# create

 - Enter the initial balance of the account:
 - Account created successfully.


# delete

 - Account deleted successfully.


# disable

 - Account disabled successfully.
 - Error: Account is already disabled.


# enable

 - Account enabled successfully.
 - Error: Account is already enabled.


# changeplan

 - Plan changed successfully.\nAccount is now a <student|non-student> account.
