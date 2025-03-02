package modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.example.ticketbooking.R;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRcode {

    public static Bitmap displayQrCodeWithLogo(Context context, String idUser, String idTicket)
    {
        try
        {
            JwtTokenHelper jwtTokenHelper = new JwtTokenHelper(context);
            String token = jwtTokenHelper.createToken(idUser, idTicket);

            // Tạo mã QR
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap qrCodeBitmap = barcodeEncoder.encodeBitmap(token, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400);

            // Tải logo từ tài nguyên
            Bitmap logoBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.img_news); // Thay R.drawable.logo bằng logo của bạn
            Bitmap finalBitmap = addLogoToQrCode(qrCodeBitmap, logoBitmap);
            return finalBitmap;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private static Bitmap addLogoToQrCode(Bitmap qrCode, Bitmap logo) {
        // Tạo bitmap mới có kích thước giống mã QR
        Bitmap combinedBitmap = qrCode.copy(qrCode.getConfig(), true);

        // Lấy kích thước mã QR
        int qrCodeWidth = qrCode.getWidth();
        int qrCodeHeight = qrCode.getHeight();

        // Kích thước logo
        int logoWidth = qrCodeWidth / 5; // Tỉ lệ kích thước logo
        int logoHeight = qrCodeHeight / 5;

        // Tạo logo kích thước nhỏ hơn
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, logoWidth, logoHeight, false);

        // Vẽ logo lên giữa mã QR
        Canvas canvas = new Canvas(combinedBitmap);
        int centerX = (qrCodeWidth - logoWidth) / 2;
        int centerY = (qrCodeHeight - logoHeight) / 2;

        canvas.drawBitmap(scaledLogo, centerX, centerY, null);

        return combinedBitmap;
    }

}
