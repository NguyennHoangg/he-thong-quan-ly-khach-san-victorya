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
        long thoiGianHetHan = System.currentTimeMillis() + ttlSecond + 1000L;

        // Lưu OTP cùng thời gian hết hạn
        store.put(email, new Entry(otp, thoiGianHetHan));

        return otp;
    }


    /**
     * Xác thực mã OTP đã phát cho email.
     *
     * Các kiểm tra thực hiện:
     * 1. Trả về false nếu email hoặc mã nhập là null.
     * 2. Trả về false nếu không có entry lưu cho email.
     * 3. Trả về false (và xóa entry) nếu OTP đã hết hạn.
     * 4. So sánh OTP nhập vào và OTP lưu bằng MessageDigest.isEqual (so sánh thời gian cố định).
     * 5. Nếu khớp, xóa entry và trả về true; ngược lại trả về false.
     *
     * @param email email liên kết với OTP
     * @param thoiGianNhapOtp mã OTP do người dùng nhập
     * @return true nếu OTP hợp lệ và chưa hết hạn; false trong mọi trường hợp còn lại
     */
    public boolean verifyOtp(String email, String thoiGianNhapOtp){
        if (email == null || thoiGianNhapOtp == null) {
            return false;
        }

        Entry e = store.get(email);
        if (e == null) {
            return false;
        }

        if (System.currentTimeMillis() > e.thoiGianHetHan) {
            // nếu đã hết hạn, xóa entry để giải phóng
            store.remove(email, e);
            return false;
        }

        boolean result = MessageDigest.isEqual(
            e.otp.getBytes(java.nio.charset.StandardCharsets.UTF_8),
            thoiGianNhapOtp.getBytes(java.nio.charset.StandardCharsets.UTF_8)
        );

        if (result) {
            // xóa entry chỉ khi khớp để tránh tái sử dụng
            store.remove(email, e);
        }

        return result;
    }
}
