# List of All Front-end Tests

## changeplan

 * allows_change_from_standard_to_student

    Check that administrators can change accounts from the standard plan to
    the student plan.

 * allows_change_from_student_to_standard

    Check that administrators can change student accounts from the student
    plan to the standard plan.

 * fails_gracefully_account_holder

    Check that the system continues prompting for the account holder's name
    when an invalid name is entered.

 * fails_gracefully_account_number

    Check that the system continues prompting for the account number when an
    invalid account number is entered.

 * fails_gracefully_mismatch

    Check that the system continues prompting for the account number when
    another user's account number is entered.


## create

 * allows_admins_to_create_accounts

    Check that administrators can create new accounts.

 * doesnt_allow_new_account_use

    Check that when a new account is created it cannot be used in the same day.

 * fails_gracefully_account_holder

    Check that the system continues prompting for the account holder's name
    when an invalid name is entered.

 * fails_gracefully_initial_balance

    Check that the system continues prompting for the initial balance when
    an invalid balance is entered.


## delete

 * allows_admin_to_delete_accounts

    Check that administrators are able to delete accounts.

 * doesnt_allow_use_of_deleted_accounts

    Check that deleted accounts cannot be used.

 * fails_gracefully_account_holder

    Check that the system continues prompting for the account holder's name
    when an invalid name is entered.

 * fails_gracefully_account_mismatch

    Check that the system continues prompting for the account number when
    another user's account number is entered.

 * fails_gracefully_account_number

    Check that the system continues prompting for the account number when an
    invalid account number is entered.


## deposit

 * allow_admins_to_deposit

    Check that administrators can deposit money into users' accounts.

 * allow_users_to_deposit

    Check that users can deposit money into their own accounts.

 * doesnt_allow_unauthorized_access

    Check that users cannot deposit money into other users' accounts.

 * doesnt_allow_users_to_use_funds

    Check that users cannot spend deposited funds in the same day.

 * fails_gracefully_account_number

    Check that the system continues prompting for the account number when an
    invalid account number is entered.

 * fails_gracefully_amount

    Check that the system continues prompting for the amount when an invalid
    amount is entered.


## disable

 * allows_admins_to_disable_accounts

    Check that administrators can disable accounts.

 * doesnt_allow_use_of_disabled_accounts

    Check that disabled accounts cannot be used to perform transactions.

 * fails_gracefully_account_holder

    Check that the system continues prompting for the account holder's name
    when an invalid name is entered.

 * fails_gracefully_account_number

    Check that the system continues prompting for the account number when an
    invalid account number is entered.

 * fails_gracefully_mismatch

    Check that the system continues prompting for the account number when
    another user's account number is entered.


## enable

 * allows_admins_to_enable_accounts

    Check that administrators can enable accounts.

 * allows_use_of_enabled_account

    Check that enabled accounts can be used to perform transactions.

 * fails_gracefully_account_holder

    Check that the system continues prompting for the account holder's name
    when an invalid name is entered.

 * fails_gracefully_account_number

    Check that the system continues prompting for the account number when an
    invalid account number is entered.

 * fails_gracefully_mismatch

    Check that the system continues prompting for the account number when
    another user's account number is entered.


## login

 * admins_can_use_all_commands

    Check that admins are able to use all commands.

 * allow_active_users

    Check that users with active accounts can log-in.

 * allow_disabled_users

    Check that users with disabled accounts can log-in.

 * allow_login_after_logout

    Check that users can log-in again after logging out.

 * require_login_first

    Check that the only command accepted at start-up is login.

 * require_logout_before_login

    Check that the user must log-out before being able to log-in again.

 * require_valid_name

    Check that the user must enter a valid user name in standard mode.

 * standard_cant_use_admin_commands

    Check that standard users can't access privileged commands.


## logout

 * allow_logged_in_users_to_logout

    Check that logged-in users can log out.

 * only_accepts_login_after_logout

    Check that the only command accepted after a log-out is login.


## paybill

 * allows_admins_to_pay_bills

    Check that administrators are able to pay bills for users.

 * allows_users_to_pay_bills

    Check that users are able to pay bills for themselves.

 * doesnt_allow_overdraft

    Check that users can't go into overdraft using the paybill command.

 * enforces_limit

    Check that the paybill command enforces the daily limit of
    $2000.00 per company per account.

 * fails_gracefully_account_holder

    Check that the system continues prompting for the account holder's name
    when an invalid name is entered.

 * fails_gracefully_account_number

    Check that the system continues prompting for the account number when an
    invalid account number is entered.

 * fails_gracefully_company_code

    Check that the system continues prompting for the company code when an
    invalid company code is entered.

 * fails_gracefully_payment_amount

    Check that the system continues prompting for the payment amount when an
    invalid payment amount is entered.


## transfer

 * allows_admins_to_transfer_funds

    Check that administrators are able to transfer funds between users' accounts.

 * allows_users_to_transfer_funds

    Check that users are able to transfer funds from one of their accounts to
    another account.

 * doesnt_allow_overdraft

    Check that users cannot go into overdraft.

 * doesnt_allow_unauthorized_access

    Check that users cannot transfer funds out of other users' accounts.

 * doesnt_enforce_limit_on_admin

    Check that the daily limit is not enforced for administrators.

 * enforces_daily_limit

    Check that the daily transfer limit of $1000.00 per account is enforced.

 * fails_gracefully_amount

    Check that the system continues prompting for the transfer amount when an
    invalid amount is entered.

 * fails_gracefully_destination_account

     Check that the system continues prompting for the account number when an
     invalid account number is entered.

 * fails_gracefully_source_account

     Check that the system continues prompting for the account number when an
     invalid account number is entered.


## withdrawal

 * allows_admins_to_withdraw

    Check that administrators are able to withdraw funds from users' accounts.

 * allows_users_to_withdraw

    Check that users are able to withdraw funds from their own accounts.

 * doesnt_allow_overdraft

    Check that users cannot go into overdraft by withdrawing funds.

 * doesnt_allow_unauthorized_access

    Check that users cannot withdraw funds from other users' accounts.

 * enforces_daily_withdrawal_limit

    Check that the daily withdrawal limit of $500.00 is enforced.

 * fails_gracefully_on_invalid_input

    Check that the system continues prompting for input when invalid input
    is entered.
