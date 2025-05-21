package bg.sofia.uni.fmi.mjt.split.dto;

import bg.sofia.uni.fmi.mjt.split.exception.user.group.UserNotGroupMemberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GroupTest {

    private static Group group;
    private static final String firstMember = "user1";
    private static final String secondMember = "user2";
    private static final String thirdMember = "user3";
    private static Set<Split> splits;
    private static Set<String> members;

    @BeforeEach
    void setUp() {
        Split splitFirstSecond = new Split(firstMember, secondMember, 0);
        Split splitFirstThird = new Split(firstMember, thirdMember, 0);
        Split splitSecondThird = new Split(secondMember, thirdMember, 0);
        members = Set.of(firstMember, secondMember, thirdMember);
        splits = Set.of(splitFirstSecond, splitSecondThird, splitFirstThird);
        group = new Group("Group", members);
    }


    @Test
    void testIsMember() {
        for (String memberUsername : members) {
            assertTrue(group.isMember(memberUsername), memberUsername + " should be member of the group");
        }
        assertFalse(group.isMember("non-member username"));
    }

    @Test
    void testSplits() {
        assertTrue(group.splits().containsAll(splits));
    }

    @Test
    void testSplit() {
        double amountToSplit = 12;
        group.split(firstMember, amountToSplit);
        double expectedSplitAmount = 4;

        for (Split split : group.splits()) {
            if (split.isMember(firstMember)) {
                assertEquals(firstMember, split.lender());
                assertEquals(expectedSplitAmount, split.amount());
            }
        }
        for (Split split : group.splits()) {
            if (split.isMember(secondMember) && split.isMember(thirdMember)) {
                assertNull(split.lender());
                assertNull(split.borrower());
                assertTrue(split.amount() < 0.01);
            }
        }
    }

    @Test
    void testPayWithPayerOrPayeeNotInGroup() {
        assertThrows(UserNotGroupMemberException.class, () -> group.pay("non-member", firstMember, 5));
        assertThrows(UserNotGroupMemberException.class, () -> group.pay(firstMember,"non-member", 5));
    }

    @Test
    void testPay() {
        group.split(firstMember, 12);
        group.pay(firstMember, secondMember, 4);
        for (Split split : group.splits()) {
            if (split.isMember(firstMember) && split.isMember(secondMember)) {
                assertFalse(split.hasUnsettledDebt());
            }
        }
    }
}
