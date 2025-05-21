package bg.sofia.uni.fmi.mjt.split.dto;

import java.util.Objects;

public class Split {
    private static final double EPSILON = 0.01;
    private final String firstUser;
    private final String secondUser;
    private double amount;

    public Split(String firstUser, String secondUser, double amount) {
        validateUsername(firstUser);
        validateUsername(secondUser);
//        validateAmount(amount);

        this.firstUser = firstUser;
        this.secondUser = secondUser;
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Split split = (Split) o;
        return (Objects.equals(firstUser, split.firstUser) || Objects.equals(firstUser, split.secondUser)) &&
            (Objects.equals(secondUser, split.firstUser) || Objects.equals(secondUser, split.secondUser));
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstUser, secondUser);
    }

    public double amount() {
        return Math.abs(amount);
    }

    public String borrower() {
        if (amount < 0) {
            return firstUser;
        } else if (amount > 0) {
            return secondUser;
        }

        return null;
    }

    public String lender() {
        if (amount > 0) {
            return firstUser;
        } else if (amount < 0) {
            return secondUser;
        }

        return null;
    }

    public void pay(String payerUsername, double pay) {
        transact(firstUser, secondUser, payerUsername, pay);
    }

    public void split(String splitterUsername, double splitAmount) {
        transact(firstUser, secondUser, splitterUsername, splitAmount);
    }

    private synchronized void transact(String first, String second, String username, double sum) {
        validateUsername(username);
        validateAmount(sum);

        if (username.equals(first)) {
            amount += sum;
        } else if (username.equals(second)) {
            amount -= sum;
        } else {
            throw new IllegalArgumentException("Username must be one of the participants in the split");
        }
    }

    public boolean isMember(String username) {
        validateUsername(username);
        return username.equals(firstUser) || username.equals(secondUser);
    }

    private void validateUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
    }

    private void validateAmount(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount cannot be less than or equal to zero");
        }
    }

    public boolean hasUnsettledDebt() {
        return !(Math.abs(amount) < EPSILON);
    }
}
