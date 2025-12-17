# HƯỚNG DẪN VIẾT TEST CHO PROJECT VICTORYA HOTEL

## 📚 Tổng quan

Project đã được cấu hình sẵn các framework test:
- **JUnit 5** (Jupiter): Framework test chính
- **Mockito**: Mock objects để test độc lập
- **TestFX**: Test JavaFX GUI (nếu cần)

## 🎯 Cấu trúc thư mục Test

```
src/test/java/
├── dao/              # Test cho Data Access Objects
│   └── KhachHang_DAO_Test.java
├── model/            # Test cho Model classes
│   └── KhachHang_Test.java
├── controller/       # Test cho Controllers
│   └── KhachHang_Controller_Test.java
└── view/             # Test cho GUI (với TestFX)
```

## 📖 Các annotation quan trọng

### JUnit 5 Annotations

```java
@Test                    // Đánh dấu method là test case
@DisplayName("...")      // Tên hiển thị cho test
@BeforeEach             // Chạy trước MỖI test
@AfterEach              // Chạy sau MỖI test
@BeforeAll              // Chạy 1 lần trước TẤT CẢ test (static method)
@AfterAll               // Chạy 1 lần sau TẤT CẢ test (static method)
@Order(1)               // Thứ tự chạy test
@Disabled               // Tạm tắt test này
```

### Mockito Annotations

```java
@Mock                   // Tạo mock object
@InjectMocks           // Inject mock vào object test
@Spy                   // Tạo spy (mock một phần)
```

## 🔍 Assertions (Kiểm tra kết quả)

```java
// So sánh bằng
assertEquals(expected, actual, "message");
assertNotEquals(expected, actual);

// Kiểm tra null
assertNull(object);
assertNotNull(object);

// Kiểm tra boolean
assertTrue(condition);
assertFalse(condition);

// Kiểm tra exception
assertThrows(ExceptionClass.class, () -> {
    // code ném exception
});

// Kiểm tra tất cả (nhiều assertion cùng lúc)
assertAll(
    () -> assertEquals(1, result.getId()),
    () -> assertEquals("Name", result.getName())
);
```

## 📝 Mẫu Test cho DAO (Database)

```java
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MyDAO_Test {
    private MyDAO dao;
    private static String testId = "TEST001";
    
    @BeforeEach
    void setUp() {
        dao = new MyDAO();
    }
    
    @Test
    @Order(1)
    @DisplayName("Test thêm dữ liệu")
    void testCreate() {
        // Arrange (Chuẩn bị)
        MyModel obj = new MyModel();
        obj.setId(testId);
        
        // Act (Thực hiện)
        boolean result = dao.create(obj);
        
        // Assert (Kiểm tra)
        assertTrue(result, "Thêm phải thành công");
    }
    
    @Test
    @Order(2)
    @DisplayName("Test đọc dữ liệu")
    void testRead() {
        MyModel obj = dao.getById(testId);
        assertNotNull(obj);
        assertEquals(testId, obj.getId());
    }
    
    @Test
    @Order(5)
    @DisplayName("Test xóa dữ liệu")
    void testDelete() {
        boolean result = dao.delete(testId);
        assertTrue(result);
    }
}
```

## 🎭 Mẫu Test với Mockito

```java
class MyController_Test {
    @Mock
    private MyDAO mockDAO;
    
    @InjectMocks
    private MyController controller;
    
    private AutoCloseable closeable;
    
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }
    
    @Test
    void testWithMock() {
        // Giả lập hành vi
        when(mockDAO.getById("001")).thenReturn(new MyModel());
        
        // Test
        MyModel result = controller.findById("001");
        
        // Verify
        assertNotNull(result);
        verify(mockDAO, times(1)).getById("001");
    }
}
```

## 🚀 Cách chạy Test

### Trong IDE (IntelliJ IDEA / Eclipse)

1. **Chạy 1 test method**: 
   - Click chuột phải vào method test → `Run 'testMethodName()'`

2. **Chạy cả test class**:
   - Click chuột phải vào class → `Run 'ClassName'`

3. **Chạy tất cả test trong package**:
   - Click chuột phải vào package → `Run Tests in 'packageName'`

### Dòng lệnh Maven

```bash
# Chạy tất cả test
mvn test

# Chạy test của 1 class cụ thể
mvn test -Dtest=KhachHang_Test

# Chạy test với output chi tiết
mvn test -X

# Skip test khi build
mvn clean install -DskipTests
```

## 📊 Test Coverage (Kiểm tra độ phủ)

Thêm plugin JaCoCo vào pom.xml để xem test coverage:

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

Chạy: `mvn test jacoco:report`
Xem report tại: `target/site/jacoco/index.html`

## ✅ Best Practices

1. **Test phải độc lập**: Mỗi test không phụ thuộc vào test khác
2. **Đặt tên rõ ràng**: `testThemKhachHangThanhCong()` thay vì `test1()`
3. **AAA Pattern**: Arrange → Act → Assert
4. **1 test = 1 chức năng**: Không test quá nhiều thứ trong 1 test
5. **Cleanup sau test**: Xóa dữ liệu test khỏi DB sau khi test xong
6. **Mock external dependencies**: DB, API, file system
7. **Test cả happy path và error cases**

## 🎯 Checklist viết Test

- [ ] Test các CRUD operations (Create, Read, Update, Delete)
- [ ] Test validation (dữ liệu hợp lệ/không hợp lệ)
- [ ] Test edge cases (null, empty, boundary values)
- [ ] Test exception handling
- [ ] Test business logic
- [ ] Test integration giữa các components

## 📚 Tài liệu tham khảo

- JUnit 5: https://junit.org/junit5/docs/current/user-guide/
- Mockito: https://site.mockito.org/
- TestFX: https://github.com/TestFX/TestFX

## 🔧 Troubleshooting

**Lỗi: "Cannot resolve symbol JUnit"**
→ Chạy `mvn clean install` để download dependencies

**Test không chạy**
→ Kiểm tra test class phải có suffix `Test` hoặc `Tests`
→ Kiểm tra method test phải có annotation `@Test`

**Database connection fails trong test**
→ Tạo database test riêng hoặc dùng in-memory database (H2)
