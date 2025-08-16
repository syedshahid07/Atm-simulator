import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ATMService atmService = new ATMService();

        System.out.println("💳 Welcome to the ATM!");
        System.out.print("Enter Card Number: ");
        String card = sc.nextLine();
        System.out.print("Enter PIN: ");
        String pin = sc.nextLine();

        Account account = atmService.login(card, pin);
        if (account != null) {
            System.out.println("✅ Login successful! Welcome, " + account.getCardNumber());
            Menu menu = new Menu(atmService, account);
            menu.show();
        } else {
            System.out.println("❌ Invalid card or PIN.");
        }
    }
}
