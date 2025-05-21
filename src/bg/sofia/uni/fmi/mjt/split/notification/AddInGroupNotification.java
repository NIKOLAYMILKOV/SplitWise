package bg.sofia.uni.fmi.mjt.split.notification;

public class AddInGroupNotification {
    private static final String FORM = "%s added you as a group %s";

    public static String get(String adder, String groupName) {
        return String.format(FORM, adder, groupName);
    }
}
