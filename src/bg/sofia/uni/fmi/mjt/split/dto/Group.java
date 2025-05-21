package bg.sofia.uni.fmi.mjt.split.dto;

import bg.sofia.uni.fmi.mjt.split.exception.user.group.GroupException;
import bg.sofia.uni.fmi.mjt.split.exception.user.group.UserNotGroupMemberException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class Group {
    private static final int MIN_MEMBER_COUNT = 3;

    private final String name;
    private final Set<String> members;
    private final Set<Split> splits;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(name, group.name) && Objects.equals(members, group.members);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, members);
    }

    public Group(String name, Set<String> members) {
        validateMemberCount(members);
        this.name = name;
        this.members = members;

        splits = new HashSet<>();
        List<String> usernameList = new ArrayList<>(members);
        for (int i = 0; i < usernameList.size() - 1; i++) {
            for (int j = i + 1; j < usernameList.size(); j++) {
                splits.add(new Split(usernameList.get(i), usernameList.get(j), 0));
            }
        }
    }

    public String name() {
        return name;
    }

    public Set<String> members() {
        return Collections.unmodifiableSet(members);
    }

    public boolean isMember(String username) {
        return members.contains(username);
    }

    public void split(String splitter, double amount) {
        double splitAmount = amount / members.size();
        splits.stream()
            .filter(s -> s.isMember(splitter))
            .forEach(s -> s.split(splitter, splitAmount));
    }

    public void pay(String lender, String borrower, double amount) {
        Optional<Split> optionalSplit = splits.stream()
            .filter(s -> s.isMember(borrower) && s.isMember(lender))
            .findFirst();
        if (optionalSplit.isEmpty()) {
            throw new UserNotGroupMemberException("Payer or payee is not in the group");
        }

        optionalSplit.get().pay(borrower, amount);
    }

    public List<Split> splits() {
        return splits.stream().toList();
    }

    private void validateMemberCount(Set<String> members) {
        if (members.size() < MIN_MEMBER_COUNT) {
            throw new GroupException("Member count must be at least " + MIN_MEMBER_COUNT);
        }
    }
}
