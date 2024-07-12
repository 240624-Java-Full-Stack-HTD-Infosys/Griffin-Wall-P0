package controllers;

import models.Transaction;
import services.TransactionService;


import io.javalin.http.Context;
import java.sql.SQLException;
import java.util.List;

public class TransactionController {
    private TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public void getTransactions(Context ctx) {
        int accountId = Integer.parseInt(ctx.pathParam("accountId"));
        List<Transaction> transactions = transactionService.getTransactionsByAccountId(accountId);
        ctx.status(200).json(transactions);
    }
}
