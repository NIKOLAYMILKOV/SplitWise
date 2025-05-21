package bg.sofia.uni.fmi.mjt.split.notification;

public class SplitRequestNotification {
    private static final String FORM = "You owe %s %.2f LV [%s]";

    public static String get(String splitter, double amount, String reason) {
        return String.format(FORM, splitter, amount, reason);
    }
}
