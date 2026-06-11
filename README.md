# ViktAI

ViktAI là hệ thống AI hỗ trợ thiết kế nội thất phong cách Việt Nam với trải nghiệm giao diện tương tự ChatGPT/Gemini. Người dùng tạo một cuộc hội thoại mới, upload ảnh căn phòng hoặc sơ đồ mặt bằng, chọn phong cách thiết kế, nhận ảnh render AI và tiếp tục chỉnh sửa bằng tin nhắn tự nhiên.

## Tech stack

### Frontend

- React + Vite
- TailwindCSS
- Axios
- Lucide React

### Backend

- Spring Boot 3
- JDK 17
- Spring Data JPA
- MySQL 8
- Hugging Face Inference API với Bearer Token Authentication

## Cấu trúc thư mục

```text
ViktAI/
├── backend/                  # Spring Boot REST API
│   └── src/main/java/com/viktai
│       ├── client/           # HuggingFaceClient
│       ├── config/           # CORS + Hugging Face properties
│       ├── controller/       # REST controllers
│       ├── dto/              # Request/response DTOs
│       ├── entity/           # JPA entities
│       ├── exception/        # Global exception handling
│       ├── repository/       # Spring Data repositories
│       └── service/          # Service interfaces + implementations
├── frontend/                 # React app
│   └── src
│       ├── api/              # Axios API service
│       ├── components/       # Sidebar, form, chat, image viewer
│       ├── layouts/          # App layout
│       └── pages/            # Chat page orchestration
└── docs/
    ├── sample-api-response.json
    └── schema.sql
```

## Chức năng chính

- `New Chat` tạo form chọn loại đầu vào:
  - `4 mặt căn phòng`: upload ảnh mặt trước, trái, phải, sau.
  - `1 sơ đồ mặt bằng`: upload một ảnh sơ đồ.
- Chọn phong cách: `Cổ điển`, `Tân cổ điển`, `Cách tân`, `Đông Dương`.
- Gửi `multipart/form-data` tới backend gồm ảnh, `style`, `description`, `designType`.
- Backend tạo prompt tiếng Việt, gọi Hugging Face Inference API và lưu cuộc hội thoại vào MySQL.
- Giao diện chat hiển thị sidebar, danh sách hội thoại, ảnh kết quả, mô tả và ô nhập yêu cầu chỉnh sửa.
- Tin nhắn chỉnh sửa như “Đổi sofa thành màu nâu” sẽ tạo prompt mới giữ nguyên bố cục/phong cách và cập nhật ảnh hiện tại.

## API endpoints

| Method | Endpoint | Mô tả |
| --- | --- | --- |
| `POST` | `/api/conversations` | Tạo thiết kế mới bằng `multipart/form-data` |
| `POST` | `/api/conversations/{id}/messages` | Gửi yêu cầu chỉnh sửa thiết kế |
| `GET` | `/api/conversations` | Lấy danh sách hội thoại |
| `GET` | `/api/conversations/{id}` | Lấy chi tiết hội thoại |
| `DELETE` | `/api/conversations/{id}` | Xóa hội thoại |

## Cấu hình môi trường backend

Backend đọc cấu hình từ biến môi trường, có giá trị mặc định để dễ chạy local.

| Biến | Mặc định | Ý nghĩa |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/viktai?...` | JDBC URL MySQL |
| `DB_USERNAME` | `root` | User MySQL |
| `DB_PASSWORD` | `password` | Password MySQL |
| `HF_TOKEN` | rỗng | Hugging Face token |
| `HF_MODEL_URL` | Stable Diffusion XL endpoint | Model endpoint |
| `HF_MOCK_ENABLED` | `true` | `true` dùng ảnh demo, `false` gọi Hugging Face thật |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173` | Origin frontend |

> Khi chưa có token hoặc muốn demo nhanh, giữ `HF_MOCK_ENABLED=true`. Khi triển khai thật, đặt `HF_TOKEN` và `HF_MOCK_ENABLED=false`.

## Chạy backend

Yêu cầu JDK 17+ và MySQL 8.

```bash
cd backend
mvn spring-boot:run
```

Ví dụ chạy với Hugging Face thật:

```bash
cd backend
HF_TOKEN=hf_xxx HF_MOCK_ENABLED=false mvn spring-boot:run
```

## Chạy frontend

Yêu cầu Node.js 20+.

```bash
cd frontend
npm install
npm run dev
```

Frontend mặc định gọi backend tại `http://localhost:8080/api`. Có thể đổi bằng:

```bash
VITE_API_BASE_URL=http://localhost:8080/api npm run dev
```

## Sample request

```bash
curl -X POST http://localhost:8080/api/conversations \
  -F 'style=Đông Dương' \
  -F 'designType=FOUR_WALLS' \
  -F 'description=Phòng khách sang trọng, nhiều ánh sáng tự nhiên.' \
  -F 'files=@front.jpg' \
  -F 'files=@left.jpg' \
  -F 'files=@right.jpg' \
  -F 'files=@back.jpg'
```

Chỉnh sửa thiết kế:

```bash
curl -X POST http://localhost:8080/api/conversations/1/messages \
  -H 'Content-Type: application/json' \
  -d '{"message":"Đổi sofa thành màu nâu và thêm đèn chùm"}'
```

## Sample API response

Xem file [`docs/sample-api-response.json`](docs/sample-api-response.json).

## Database schema

Xem file [`docs/schema.sql`](docs/schema.sql). Hibernate cũng có thể tự tạo/cập nhật bảng khi chạy backend với `spring.jpa.hibernate.ddl-auto=update`.

## Build và kiểm thử

```bash
cd backend
mvn test
```

```bash
cd frontend
npm install
npm run build
```
