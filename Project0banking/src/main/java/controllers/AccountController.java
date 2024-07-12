package controllers;

import models.Account;
import models.User;
import services.AccountService;
import services.TransactionService;

import io.javalin.http.Context;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class AccountController {
    private final TransactionService transactionService;
    private final AccountService accountService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }


    public void createAccount(Context ctx) {
        Account account = ctx.bodyAsClass(Account.class);
        try {
            accountService.createAccount(account);
            ctx.status(201).json(account);
        } catch (SQLException e) {
            ctx.status(500).json(e.getMessage());
        }
    }

    public void getAccounts(Context ctx) throws SQLException {
        int userId = Integer.parseInt(ctx.pathParam("userId"));
        ctx.json(accountService.getAccountsByUserId(userId));
        //try {
            //List<Account> accounts = accountService.getAccountsByUserId(userId);
           // ctx.status(200).json(accounts);
        //} catch (SQLException e) {
        //    ctx.status(500).json(e.getMessage());
        //}
    }

    public void deleteAccount(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        try {
            accountService.deleteAccount(accountId);
            ctx.status(204);
        } catch (SQLException e) {
            ctx.status(500).json(e.getMessage());
        }
    }

    public boolean deposit(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        String amountStr = ctx.body();

        System.out.println("Received deposit request for accountId: " + accountId);
        System.out.println("Request body: " + amountStr);

        if (amountStr == null || amountStr.isEmpty()) {
            System.out.println("Amount is null or empty");
            ctx.status(500).result("Amount must be provided");
            return false;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            System.out.println("Parsed amount: " + amount);
            if (accountService.deposit(accountId, amount)) {
                ctx.status(200).result("Deposit successful");
            } else {
                ctx.status(500).result("Deposit failed");
            }
        } catch (NumberFormatException e) {
            System.out.println("NumberFormatException: " + e.getMessage());
            ctx.status(500).result("Invalid amount format");
        }
        return false;
    }



    public void withdraw(Context ctx) {
        int accountId;
        try {
            accountId = Integer.parseInt(ctx.pathParam("accountId"));
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid account ID");
            return;
        }

        BigDecimal amount;
        try {
            amount = new BigDecimal(ctx.formParam("amount"));
        } catch (NumberFormatException | NullPointerException e) {
            ctx.status(400).result("Invalid amount");
            return;
        }

        boolean success = accountService.withdraw(accountId, amount);

        if (success) {
            ctx.status(200).result("Withdrawal successful");
        } else {
            ctx.status(400).result("Withdrawal failed");
        }
    }

    public void transfer(Context ctx) {
        int sourceAccountId = Integer.parseInt(ctx.pathParam("sourceAccountId"));
        int destinationAccountId = Integer.parseInt(ctx.pathParam("destinationAccountId"));
        BigDecimal amount = new BigDecimal(ctx.formParam("amount"));

        boolean success = accountService.transfer(sourceAccountId, destinationAccountId, amount);

        if (success) {
            ctx.status(200).result("Transfer successful");
        } else {
            ctx.status(400).result("Transfer failed");
        }
    }
    public void viewTransactionHistory(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        ctx.json(transactionService.getTransactionsByAccountId(accountId));
    }
}
