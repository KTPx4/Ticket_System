package modules;

import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormatter {
    public static String formatCurrency(long amount) {
        // Sử dụng Locale để định dạng theo kiểu Việt Nam (x.xxx.xxx)
        NumberFormat numberFormat = NumberFormat.getInstance(new Locale("vi", "VN"));
        return numberFormat.format(amount);
    }
}
