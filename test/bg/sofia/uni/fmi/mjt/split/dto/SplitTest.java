package bg.sofia.uni.fmi.mjt.split.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SplitTest {

    private static final String firstUsername = "Niki";
    private static final String secondUsername = "Ivan";
    private static Split split;

    @BeforeEach
    void setUp() {
        split = new Split(firstUsername, secondUsername, 0);
    }

    @Test
    void testEquals() {
        Split firstSplit = new Split(firstUsername, secondUsername, 0);
        Split secondSplit = new Split(secondUsername, firstUsername, 0);

        assertEquals(firstSplit, secondSplit, "Two splits should be equal no mater of the order of the participants");
    }

    @Test
    void testIsMemberWithUsernameNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> split.isMember(null));
        assertThrows(IllegalArgumentException.class, () -> split.isMember("    "));
        assertThrows(IllegalArgumentException.class, () -> split.isMember(""));
    }

    @Test
    void testIsMemberWithCorrectUserName() {
        assertTrue(split.isMember(firstUsername));
        assertTrue(split.isMember(secondUsername));
    }

    @Test
    void testIsMemberWithIncorrectUser() {
        assertFalse(split.isMember("incorrect username"));
    }

    @Test
    void testBorrower() {
        Split splitWithBorrowerSecond = new Split(firstUsername, secondUsername, 10);
        assertEquals(secondUsername, splitWithBorrowerSecond.borrower(),
            "Second user should be borrower when amount is more than zero");

        Split splitWithBorrowerFirst = new Split(firstUsername, secondUsername, -10);
        assertEquals(firstUsername, splitWithBorrowerFirst.borrower(),
            "First user should be borrower when amount is less than zero");

        Split splitWithNoDebt = new Split(firstUsername, secondUsername, 0);
        assertNull(splitWithNoDebt.borrower(),
            "Borrower should be null when amount is zero");
    }

    @Test
    void testLender() {
        Split splitWithLenderFirst = new Split(firstUsername, secondUsername, 10);
        assertEquals(firstUsername, splitWithLenderFirst.lender(),
            "First user should be lender when amount is more than zero");

        Split splitWithLenderSecond = new Split(firstUsername, secondUsername, -10);
        assertEquals(secondUsername, splitWithLenderSecond.lender(),
            "Second user should be lender when amount is less than zero");

        Split splitWithNoDebt = new Split(firstUsername, secondUsername, 0);
        assertNull(splitWithNoDebt.lender(),
            "Lender should be null when amount is zero");
    }

    @Test
    void testSplitWithUsernameNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> split.split(null, 15));
        assertThrows(IllegalArgumentException.class, () -> split.split("  ", 15));
        assertThrows(IllegalArgumentException.class, () -> split.split("", 15));
    }

    @Test
    void testSplitWithAmountLessThanOrEqualToZero() {
        assertThrows(IllegalArgumentException.class, () -> split.split(firstUsername, 0));
        assertThrows(IllegalArgumentException.class, () -> split.split(firstUsername, -15));
    }

    @Test
    void testSplitWithUsernameNotInTheSplit() {
        assertThrows(IllegalArgumentException.class, () -> split.split("user", 10));
    }

    @Test
    void testSplit() {
        split.split(firstUsername, 10);
        assertEquals(secondUsername, split.borrower());
        assertEquals(10, split.amount());
    }

    @Test
    void testPayWithUsernameNullOrBlank() {
        assertThrows(IllegalArgumentException.class, () -> split.pay(null, 15));
        assertThrows(IllegalArgumentException.class, () -> split.pay("  ", 15));
        assertThrows(IllegalArgumentException.class, () -> split.pay("", 15));
    }

    @Test
    void testPayWithAmountLessThanOrEqualToZero() {
        assertThrows(IllegalArgumentException.class, () -> split.split(firstUsername, 0));
        assertThrows(IllegalArgumentException.class, () -> split.split(firstUsername, -15));
    }

    @Test
    void testPayWithUsernameNotInTheSplit() {
        assertThrows(IllegalArgumentException.class, () -> split.split("user", 10));
    }

    @Test
    void testPay() {
        Split splitWithBorrowerSecond = new Split(firstUsername, secondUsername, 10);
        splitWithBorrowerSecond.pay(secondUsername, 5);
        assertEquals(secondUsername, splitWithBorrowerSecond.borrower());
        assertEquals(5, splitWithBorrowerSecond.amount());
    }

    @Test
    void testHasUnsettledDebt() {
        Split splitWithUnsettledDebt = new Split(firstUsername, secondUsername, 10);
        Split splitWithoutUnsettledDebt = new Split(firstUsername, secondUsername, 0);
        assertTrue(splitWithUnsettledDebt.hasUnsettledDebt());
        assertFalse(splitWithoutUnsettledDebt.hasUnsettledDebt());
    }




}
