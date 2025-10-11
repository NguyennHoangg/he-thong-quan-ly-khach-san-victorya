# 🏨 Hệ Thống Quản Lý Khách Sạn Victorya

## 📖 Mô tả

Đây là một ứng dụng Java để quản lý khách sạn được phát triển bằng Java với **Maven build system**. Ứng dụng cung cấp các chức năng cơ bản để quản lý thông tin khách sạn, phòng và các hoạt động liên quan.

## 🛠️ Công nghệ sử dụng

- **Java**: 11+ (Oracle JDK hoặc OpenJDK)
- **Build Tool**: Maven 3.x
- **Architecture**: MVC (Model-View-Controller)
- **Logging**: SLF4J + Logback
- **Testing**: JUnit 4 & JUnit 5
- **IDE hỗ trợ**: IntelliJ IDEA, Eclipse, VS Code

## ✨ Tính năng chính

- 🏨 Quản lý thông tin khách sạn
- 🏠 Quản lý phòng (thêm, sửa, xóa, tìm kiếm)
- 👥 Quản lý khách hàng
- 📊 Báo cáo và thống kê
- 🔧 Cấu hình hệ thống linh hoạt
- 📝 Logging chi tiết các hoạt động

## ⚙️ Yêu cầu hệ thống

- **Java**: Version 11 hoặc cao hơn
- **Maven**: 3.x hoặc cao hơn
- **OS**: Windows, Linux hoặc macOS

## 🚀 Hướng dẫn Clone và Setup

### Bước 1: Clone dự án về máy

```bash
git clone https://github.com/lttboy25/he-thong-quan-ly-khach-san-victorya.git
cd he-thong-quan-ly-khach-san-victorya
```

### Bước 2: Kiểm tra Java version

```bash
java -version
```

Đảm bảo Java version >= 11

### Bước 3: Build dự án lần đầu

```bash
mvn clean install
```

### Bước 4: Chạy ứng dụng

**Cách 1 - Chạy trực tiếp bằng Maven:**

```bash
mvn exec:java -Dexec.mainClass="iuh.fit.se.group13.Main"
```

<!-- **Cách 2 - Chạy từ file JAR:**

```bash
mvn package
java -jar target/HeThongQuanLiKhachSan-1.0-SNAPSHOT.jar -->

````

## 🛠️ Các lệnh Maven hữu ích

### Build và Test

```bash
# Build toàn bộ project
mvn clean install

# Chỉ compile code
mvn compile

# Chạy tests
mvn test

# Clean build artifacts
mvn clean
````

### Chạy ứng dụng

```bash
# Chạy trực tiếp
mvn javafx:run

# Tạo distribution JAR
mvn package
```

## 📁 Folders được tạo sau khi build

Sau khi chạy `mvn clean install`, bạn sẽ thấy:

- `target/` - Chứa build artifacts
  - `target/classes/` - Compiled Java classes
  - `target/HeThongQuanLiKhachSan-1.0-SNAPSHOT.jar` - File JAR executable

> ⚠️ **Quan trọng**: Folder `target/` được tự động tạo và không được commit vào Git.

## 🤝 Làm việc nhóm

### Khi làm việc với team:

1. **Pull code mới nhất:**

   ```bash
   git pull origin main
   ```

2. **Build lại sau khi pull:**

   ```bash
   mvn clean install
   ```

3. **Commit code:**
   ```bash
   git add .
   git commit -m "Your commit message"
   git push origin your-branch
   ```

### Lưu ý khi merge:

- Chỉ commit source code (folder `src/`)
- KHÔNG commit folder `target/`
- File `.gitignore` đã cấu hình loại trừ `target/`

## 🐛 Troubleshooting

**1. Java version không đúng:**

```bash
java -version
javac -version

# Windows
set JAVA_HOME=C:\Program Files\Java\jdk-11.0.x
```

**2. Maven không nhận lệnh:**

- Kiểm tra đã cài Maven và thêm vào `PATH` chưa.
- Kiểm tra bằng `mvn -v`.

**3. OutOfMemoryError khi chạy ứng dụng:**

```bash
# Có thể thêm option khi chạy:
java -Xms512m -Xmx1024m -jar target/HeThongQuanLiKhachSan-1.0-SNAPSHOT.jar
```

## 🔧 Development Guide

### Cách thêm feature mới:

1. Tạo entity trong `src/main/java/com/hotel/entity/`
2. Tạo controller trong `src/main/java/com/hotel/controller/`
3. Tạo view trong `src/main/java/com/hotel/view/`
4. Cập nhật `Main.java` nếu cần

### Code style:

- camelCase cho biến và method
- PascalCase cho class
- Comment method phức tạp
- Tuân thủ Java conventions

### Testing:

```bash
mvn test
mvn test -q    # Chạy test với output chi tiết
```

## 📞 Liên hệ & Hỗ trợ

- **GitHub**: [Cozy](https://github.com/lttboy25)
- **Email**: [tungthanh8937@gmail.com](mailto:tungthanh8937@gmail.com)
- **Issues**: Tạo issue trên GitHub repository

---
