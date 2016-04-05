# Software Quality Banking System Project

## Dependencies

 - Java JDK (version 7 or higher) for `javac` and `java`.
 - G++ (version 4.9 or higher) or Clang (untested) for `c++`.
 - GNU Make (or equivalent) for `make`.
 - Unix environment for `cd`, `rm`, `pushd`, `popd`, etc. used in scripts.
 - Bash for shell scripts.

## Instructions

 1. Install dependencies.

 2. Run `make` in project root to build front end and back end

 3. Run `java -cp bin backend.BackEnd <old_master_accounts_file>
    <transactions_file> <new_master_accounts_file> <current_accounts_file>`
    to start back end, where:
     - `<old_master_accounts_file>` is the master accounts input file,
     - `<transactions_file>` is the transaction log input file,
     - `<new_master_accounts_file>` is the master accounts output file,
     - `<current_accounts_file>` is the current accounts output file.

 4. Run `make test` to run front end and back end test suites

## Backend Authors

 - Pat Smuk
 - Clayton Cheung
 - Dennis Pacewicz
