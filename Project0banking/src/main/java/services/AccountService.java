package services;

import daos.AccountDao;
import daos.TransactionDao;
import daos.UserDao;
import models.Account;
import models.Transaction;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AccountService {
    private final Connection connection;
    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private UserDao userDao;

    public AccountService(Connection connection) {
        this.accountDao = new AccountDao(connection);
        this.transactionDao = new TransactionDao(connection);
        this.userDao = new UserDao(connection);
        this.connection = connection;
    }

    public void createAccount(Account account) throws SQLException {
        accountDao.createAccount(account);
    }

    public List<Account> getAccountsByUserId(int userId) throws SQLException {
        return accountDao.getAccountsByUserId(userId);
    }


    public boolean deposit(int accountId, BigDecimal amount) {
        try {
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            updateStmt.setBigDecimal(1, amount);
            updateStmt.setInt(2, accountId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO transactions (account_id, amount, type, timestamp) VALUES (?, ?, 'deposit', NOW())");
                insertStmt.setInt(1, accountId);
                insertStmt.setBigDecimal(2, amount);
                insertStmt.executeUpdate();
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean withdraw(int accountId, BigDecimal amount) {
        try {
            PreparedStatement updateStmt = connection.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
            updateStmt.setBigDecimal(1, amount);
            updateStmt.setInt(2, accountId);
            int rowsAffected = updateStmt.executeUpdate();

            if (rowsAffected > 0) {
                PreparedStatement insertStmt = connection.prepareStatement("INSERT INTO transactions (account_id, amount, type, timestamp) VALUES (?, ?, 'withdrawal', NOW())");
                insertStmt.setInt(1, accountId);
                insertStmt.setBigDecimal(2, amount);
                insertStmt.executeUpdate();
                return true;
            }

            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean transfer(int sourceAccountId, int destinationAccountId, BigDecimal amount) {
        try {
            connection.setAutoCommit(false); // Begin transaction

            PreparedStatement deductStmt = connection.prepareStatement("UPDATE accounts SET balance = balance - ? WHERE id = ?");
            deductStmt.setBigDecimal(1, amount);
            deductStmt.setInt(2, sourceAccountId);
            int rowsAffected1 = deductStmt.executeUpdate();

            PreparedStatement addStmt = connection.prepareStatement("UPDATE accounts SET balance = balance + ? WHERE id = ?");
            addStmt.setBigDecimal(1, amount);
            addStmt.setInt(2, destinationAccountId);
            int rowsAffected2 = addStmt.executeUpdate();

            if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                PreparedStatement insertStmt1 = connection.prepareStatement("INSERT INTO transactions (account_id, amount, type, timestamp) VALUES (?, ?, 'transfer_out', NOW())");
                insertStmt1.setInt(1, sourceAccountId);
                insertStmt1.setBigDecimal(2, amount);
                insertStmt1.executeUpdate();

                PreparedStatement insertStmt2 = connection.prepareStatement("INSERT INTO transactions (account_id, amount, type, timestamp) VALUES (?, ?, 'transfer_in', NOW())");
                insertStmt2.setInt(1, destinationAccountId);
                insertStmt2.setBigDecimal(2, amount);
                insertStmt2.executeUpdate();

                connection.commit(); // Commit transaction
                return true;
            } else {
                connection.rollback(); // Rollback transaction in case of failure
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback(); // Ensure rollback on exception
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true); // Restore auto-commit mode
            } catch (SQLException autoCommitEx) {
                autoCommitEx.printStackTrace();
            }
        }
    }

    public void deleteAccount(int accountId) throws SQLException {
        accountDao.deleteAccount(accountId);
    }
}
