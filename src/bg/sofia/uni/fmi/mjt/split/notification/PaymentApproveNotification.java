package bg.sofia.uni.fmi.mjt.split.notification;

public class PaymentApproveNotification {
    private static final String FORM = "%s approved your payment %.2f LV";

    public static String get(String debtHolder, double amount) {
        return String.format(FORM, debtHolder, amount);
    }
}
