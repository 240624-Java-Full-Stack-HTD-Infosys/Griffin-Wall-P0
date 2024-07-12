package daos;


import models.Account;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDao {

    private Connection connection;

    public AccountDao(Connection connection) {
        this.connection = connection;
    }

    public void createAccount(Account account) throws SQLException {
        String sql = "INSERT INTO accounts (user_id, account_name, balance) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, account.getUserId());
            stmt.setString(2, account.getAccountName());
            stmt.setBigDecimal(3, account.getBalance());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    account.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    public List<Account> getAccountsByUserId ( int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (rs.next()) {
                accounts.add(mapRowToAccount(rs));
            }
            return accounts;
        }
    }

    public void updateAccountBalance ( int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setBigDecimal(1, newBalance);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
        }
    }

    public void deleteAccount ( int accountId) throws SQLException {
        String sql = "DELETE FROM accounts WHERE id = ? AND balance = 0.00";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, accountId);
            stmt.executeUpdate();
        }
    }

    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        Account account = new Account();
        account.setId(rs.getInt("id"));
        account.setUserId(rs.getInt("user_id"));
        account.setAccountName(rs.getString("account_name"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }



    }
