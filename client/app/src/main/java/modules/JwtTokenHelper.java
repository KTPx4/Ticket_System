package modules;

import android.content.Context;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.ticketbooking.R;

import java.util.Date;

public class JwtTokenHelper {

    // Khóa bí mật để ký token (nên giữ bí mật)
    private  final String SECRET_KEY ;

    // Thời gian hết hạn token (ví dụ: 1 giờ)
    private static final long EXPIRATION_TIME = 60 * 1000;

    public JwtTokenHelper(Context context)
    {
        SECRET_KEY = context.getString(R.string.JWT_SECRET_KEY);
    }

    public  String createToken(String idUser, String idTicket) {
        try {
            // Thuật toán ký (HMAC SHA256)
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

            // Ngày hết hạn
            Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

            // Tạo token
            return JWT.create()
                    .withIssuer("ticket_booking_app")  // Issuer
                    .withClaim("idUser", idUser)     // Thêm claim idUser
                    .withClaim("idTicket", idTicket) // Thêm claim idTicket
                    .withExpiresAt(expirationDate)  // Hạn sử dụng token
                    .sign(algorithm);               // Ký token
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public  boolean verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            JWT.require(algorithm)
                    .withIssuer("your_app_name")
                    .build()
                    .verify(token);
            return true; // Token hợp lệ
        } catch (Exception e) {
            e.printStackTrace();
            return false; // Token không hợp lệ
        }
    }


    public  String getClaimFromToken(String token, String claimKey) {
        try {
            return JWT.decode(token).getClaim(claimKey).asString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}