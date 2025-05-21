package bg.sofia.uni.fmi.mjt.split.notification;

public class AddFriendNotification {
    private static final String FORM = "%s added you as a friend";

    public static String get(String adder) {
        return String.format(FORM, adder);
    }
}
