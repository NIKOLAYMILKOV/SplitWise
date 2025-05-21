package bg.sofia.uni.fmi.mjt.split.dto;

import java.util.Objects;

public class Friendship {
    private final Split split;

    public Friendship(String firstUser, String secondUser) {
        split = new Split(firstUser, secondUser, 0);
    }

    public boolean isMember(String username) {
        return split.isMember(username);
    }

    public void pay(String username, double amount) {
        split.pay(username, amount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friendship that = (Friendship) o;
        return Objects.equals(split, that.split);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(split);
    }

    public void split(String username, double amount) {
        split.split(username, amount / 2);
    }

    public boolean hasUnsettledDebt() {
        return split.hasUnsettledDebt();
    }

    public String borrower() {
        return split.borrower();
    }

    public String lender() {
        return split.lender();
    }

    public double amount() {
        return split.amount();
    }
}
