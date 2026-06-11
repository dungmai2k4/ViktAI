package com.viktai.client;

import com.viktai.config.HuggingFaceProperties;
import com.viktai.dto.AiDesignResult;
import com.viktai.exception.AiProviderException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

@Component
public class HuggingFaceClient {
    private static final String MOCK_IMAGE = "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?auto=format&fit=crop&w=1200&q=80";

    private final HuggingFaceProperties properties;
    private final RestClient restClient;

    public HuggingFaceClient(HuggingFaceProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.build();
    }

    public AiDesignResult generateDesign(String prompt, List<MultipartFile> images, String responseDescription) {
        // Chỉ trả ảnh demo khi bật HF_MOCK_ENABLED=true. Nếu thiếu token thì báo lỗi rõ ràng
        // để tránh người dùng tưởng API thật nhưng vẫn nhận kết quả giống sample.
        if (properties.mockEnabled()) {
            return new AiDesignResult(MOCK_IMAGE, buildMockDescription(responseDescription));
        }
        if (!StringUtils.hasText(properties.token())) {
            throw new AiProviderException(
                    "Thiếu HF_TOKEN nên backend không thể gọi Hugging Face. "
                            + "Hãy cấu hình HF_TOKEN hoặc đặt HF_MOCK_ENABLED=true nếu muốn dùng ảnh demo."
            );
        }

        try {
            // Hugging Face text-to-image endpoints trả về bytes ảnh; backend chuyển thành data URL cho frontend.
            byte[] imageBytes = restClient.post()
                    .uri(properties.modelUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("inputs", prompt))
                    .retrieve()
                    .body(byte[].class);

            if (imageBytes == null || imageBytes.length == 0) {
                throw new AiProviderException("Hugging Face không trả về ảnh thiết kế");
            }

            String base64 = Base64.getEncoder().encodeToString(imageBytes);
            return new AiDesignResult("data:image/png;base64," + base64, responseDescription);
        } catch (Exception ex) {
            throw new AiProviderException("Không thể gọi Hugging Face Inference API", ex);
        }
    }

    private String buildMockDescription(String responseDescription) {
        return "[Chế độ demo] " + responseDescription
                + " Backend đang dùng ảnh demo vì HF_MOCK_ENABLED=true. "
                + "Để nhận ảnh AI thật từ Hugging Face, hãy cấu hình HF_TOKEN và đặt HF_MOCK_ENABLED=false.";
    }
}
