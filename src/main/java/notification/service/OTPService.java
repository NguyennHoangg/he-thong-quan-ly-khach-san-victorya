package notification.service;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.concurrent.ConcurrentHashMap;

public class OTPService {
    private static final SecureRandom rnd = new SecureRandom();
    private static final ConcurrentHashMap <String, Entry> store = new ConcurrentHashMap<>();


    static class  Entry {
        private String otp;
        private long thoiGianHetHan;
        public Entry (String otp, long thoiGianHetHan){
            this.otp = otp;
            this.thoiGianHetHan = thoiGianHetHan;
        }
    }

    /**
     * Sinh mã OTP 4 chữ số cho địa chỉ email được chỉ định
     * @param email Địa chỉ email để liên kết với OTP
     * @param ttlSecond Thời gian sống (TTL) của OTP tính theo giây
     * @return Mã OTP 4 chữ số dưới dạng chuỗi
     */
    public static String generateOTP(String email, int ttlSecond){
        // Đặt độ dài OTP là 4 chữ số
        int lenght = 4;

        // Tính giá trị min và max cho độ dài đã cho
        int min = (int) Math.pow(10, lenght - 1);
        int max = (int) Math.pow(10, lenght) - 1;

        // Sinh số ngẫu nhiên trong khoảng
        int num = rnd.nextInt(max - min + 1) + min;
        
        // Định dạng số để đảm bảo có đủ độ dài, thêm số 0 ở đầu nếu cần
        String otp = String.format("%0" + lenght + "d", num);

    // Tính thời gian hết hạn (thời gian hiện tại + TTL tính theo milliseconds)
    long thoiGianHetHan = System.currentTimeMillis() + ttlSecond * 1000L;

        // Lưu OTP cùng thời gian hết hạn
        store.put(email, new Entry(otp, thoiGianHetHan));

        return otp;
    }


    /**
     * Xác thực mã OTP đã phát cho email, kiểm tra cả thời gian nhập vào.
     *
     * @param email email liên kết với OTP
     * @param otp mã OTP do người dùng nhập
     * @param currentTimeMillis thời gian hiện tại (milliseconds), thường lấy bằng System.currentTimeMillis()
     * @return true nếu OTP hợp lệ và chưa hết hạn; false trong mọi trường hợp còn lại
     */
    public static boolean verifyOtp(String email, String otp, long currentTimeMillis){
        if (email == null || otp == null) {
            return false;
        }

        Entry e = store.get(email);
        if (e == null) {
            return false;
        }

        if (currentTimeMillis > e.thoiGianHetHan) {
            store.remove(email, e);
            return false;
        }

        boolean result = MessageDigest.isEqual(
            e.otp.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            otp.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        if (result) {
            store.remove(email, e);
        }

        return result;
    }
}
