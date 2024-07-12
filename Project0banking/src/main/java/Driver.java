import controllers.AccountController;
import controllers.TransactionController;
import controllers.UserController;
import daos.AccountDao;
import daos.TransactionDao;
import daos.UserDao;
import models.Account;
import models.Transaction;
import services.AccountService;
import services.TransactionService;
import services.UserService;
import io.javalin.Javalin;
import utils.ConnectionUtil;
import java.sql.Connection;
import java.sql.SQLException;

import java.io.IOException;
import java.sql.DriverManager;


public class Driver {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        Javalin app = Javalin.create().start(8080);
        Connection connection = ConnectionUtil.getConnection();

        UserDao userDao = new UserDao(connection);
        AccountDao accountDao = new AccountDao(connection);
        TransactionDao transactionDao = new TransactionDao(connection);

        UserService userService = new UserService(connection);
        AccountService accountService = new AccountService(connection);
        TransactionService transactionService = new TransactionService(connection);


        UserController userController = new UserController(userService);
        AccountController accountController = new AccountController(accountService, transactionService);
        TransactionController transactionController = new TransactionController(transactionService);

        app.post("/register", userController::register);
        app.post("/login", userController::login);
        app.put("/users", userController::updateUser);

        app.post("/users/{userId}/accounts", accountController::createAccount);
        app.get("/users/{userId}/accounts", accountController::getAccounts);
        app.delete("/accounts/{accountId}", accountController::deleteAccount);
        app.post("/accounts/{accountId}/deposit", accountController::deposit);
        app.post("/accounts/{accountId}/withdraw", accountController::withdraw);
        app.put("/accounts/transfer/{sourceAccountId}/{destinationAccountId}", accountController::transfer);

        app.get("/accounts/{accountId}/transactions", accountController::viewTransactionHistory);


    }
}
