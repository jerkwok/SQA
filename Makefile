BACKEND_JAVA = \
	src/backend/Account.java \
	src/backend/BackEnd.java \
	src/backend/Constants.java \
	src/backend/Transaction.java \
	src/backend/TransactionContext.java \
	src/backend/TransferContext.java \
	src/backend/exceptions/InvalidTransactionException.java \
	src/backend/exceptions/ViolatedConstraintException.java \
	src/backend/transactions/ChangePlanTransaction.java \
	src/backend/transactions/CreateTransaction.java \
	src/backend/transactions/DeleteTransaction.java \
	src/backend/transactions/DepositTransaction.java \
	src/backend/transactions/DisableTransaction.java \
	src/backend/transactions/EnableTransaction.java \
	src/backend/transactions/PayBillTransaction.java \
	src/backend/transactions/TransferTransaction.java \
	src/backend/transactions/WithdrawalTransaction.java

BACKEND_TEST_JAVA = \
	tests/backend/Account/TestFromMasterAccountsLine.java \
	tests/backend/Account/TestToCurrentAccountsLine.java \
	tests/backend/Account/TestToMasterAccountsLine.java \
	tests/backend/ChangePlanTransaction/TestApply.java \
	tests/backend/ChangePlanTransaction/TestConstructor.java \
	tests/backend/CreateTransaction/TestApply.java \
	tests/backend/CreateTransaction/TestConstructor.java \
	tests/backend/DeleteTransaction/TestApply.java \
	tests/backend/DeleteTransaction/TestConstructor.java \
	tests/backend/DepositTransaction/TestApply.java \
	tests/backend/DepositTransaction/TestConstructor.java \
	tests/backend/DisableTransaction/TestApply.java \
	tests/backend/DisableTransaction/TestConstructor.java \
	tests/backend/EnableTransaction/TestApply.java \
	tests/backend/EnableTransaction/TestConstructor.java \
	tests/backend/PayBillTransaction/TestApply.java \
	tests/backend/PayBillTransaction/TestConstructor.java \
	tests/backend/Transaction/TestFromLine.java \
	tests/backend/TransferTransaction/TestApply.java \
	tests/backend/TransferTransaction/TestConstructor.java \
	tests/backend/WithdrawalTransaction/TestApply.java \
	tests/backend/WithdrawalTransaction/TestConstructor.java

BACKEND_TEST_CLASSES = \
	tests.backend.Account.TestFromMasterAccountsLine \
	tests.backend.Account.TestToCurrentAccountsLine \
	tests.backend.Account.TestToMasterAccountsLine \
	tests.backend.ChangePlanTransaction.TestApply \
	tests.backend.ChangePlanTransaction.TestConstructor \
	tests.backend.CreateTransaction.TestApply \
	tests.backend.CreateTransaction.TestConstructor \
	tests.backend.DeleteTransaction.TestApply \
	tests.backend.DeleteTransaction.TestConstructor \
	tests.backend.DepositTransaction.TestApply \
	tests.backend.DepositTransaction.TestConstructor \
	tests.backend.DisableTransaction.TestApply \
	tests.backend.DisableTransaction.TestConstructor \
	tests.backend.EnableTransaction.TestApply \
	tests.backend.EnableTransaction.TestConstructor \
	tests.backend.PayBillTransaction.TestApply \
	tests.backend.PayBillTransaction.TestConstructor \
	tests.backend.Transaction.TestFromLine \
	tests.backend.TransferTransaction.TestApply \
	tests.backend.TransferTransaction.TestConstructor \
	tests.backend.WithdrawalTransaction.TestApply \
	tests.backend.WithdrawalTransaction.TestConstructor

HAMCREST_JAR_URL = \
	http://search.maven.org/remotecontent?filepath=org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar

JUNIT_JAR_URL = \
	http://search.maven.org/remotecontent?filepath=junit/junit/4.12/junit-4.12.jar

.PHONY: all clean test test_backend

all: bin/backend/BackEnd.class

test: test_backend

bin/backend/BackEnd.class: $(BACKEND_JAVA)
	javac -d bin -classpath src src/backend/BackEnd.java

test_backend: hamcrest-core-1.3.jar junit-4.12.jar bin/backend/BackEnd.class
	@javac -classpath ".:bin:junit-4.12.jar:hamcrest-core-1.3.jar" $(BACKEND_TEST_JAVA)
	@java -classpath ".:bin:junit-4.12.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore $(BACKEND_TEST_CLASSES)

hamcrest-core-1.3.jar:
	wget -O $@ $(HAMCREST_JAR_URL)

junit-4.12.jar:
	wget -O $@ $(JUNIT_JAR_URL)

clean:
	rm bin/backend/*.class
