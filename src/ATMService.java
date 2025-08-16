import java.sql.*;

public class ATMService {

    public Account login(String cardNumber, String pin) {
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM accounts WHERE card_number=? AND pin=?");
            ps.setString(1, cardNumber);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("account_id"),
                        rs.getString("card_number"),
                        rs.getString("pin"),
                        rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deposit(Account acc, double amount) {
        try (Connection con = DatabaseManager.getConnection()) {
            double newBalance = acc.getBalance() + amount;
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance=? WHERE account_id=?");
            ps.setDouble(1, newBalance);
            ps.setInt(2, acc.getAccountId());
            ps.executeUpdate();

            recordTransaction(con, acc.getAccountId(), "deposit", amount);
            acc.setBalance(newBalance);
            System.out.println("✅ Deposit successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void withdraw(Account acc, double amount) {
        if (acc.getBalance() < amount) {
            System.out.println("❌ Insufficient balance!");
            return;
        }
        try (Connection con = DatabaseManager.getConnection()) {
            double newBalance = acc.getBalance() - amount;
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance=? WHERE account_id=?");
            ps.setDouble(1, newBalance);
            ps.setInt(2, acc.getAccountId());
            ps.executeUpdate();

            recordTransaction(con, acc.getAccountId(), "withdraw", amount);
            acc.setBalance(newBalance);
            System.out.println("✅ Withdrawal successful!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void checkBalance(Account acc) {
        System.out.println("💰 Current Balance: " + acc.getBalance());
        try (Connection con = DatabaseManager.getConnection()) {
            recordTransaction(con, acc.getAccountId(), "balance_inquiry", 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void miniStatement(Account acc) {
        try (Connection con = DatabaseManager.getConnection()) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM transactions WHERE account_id=? ORDER BY tx_time DESC LIMIT 5");
            ps.setInt(1, acc.getAccountId());
            ResultSet rs = ps.executeQuery();
            System.out.println("📜 Last 5 Transactions:");
            while (rs.next()) {
                System.out.println(
                        rs.getTimestamp("tx_time") + " | " +
                                rs.getString("tx_type") + " | " +
                                rs.getDouble("amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void recordTransaction(Connection con, int accountId, String type, double amount) throws SQLException {
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO transactions(account_id, tx_type, amount) VALUES(?,?,?)");
        ps.setInt(1, accountId);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.executeUpdate();
    }
}
