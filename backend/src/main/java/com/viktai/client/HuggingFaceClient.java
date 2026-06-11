package com.viktai.client;

import com.viktai.config.HuggingFaceProperties;
import com.viktai.dto.AiDesignResult;
import com.viktai.exception.AiProviderException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Component
public class HuggingFaceClient {
    private final HuggingFaceProperties properties;
    private final RestClient restClient;

    public HuggingFaceClient(HuggingFaceProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.build();
    }

    public AiDesignResult generateDesign(String prompt, List<String> sourceImageUrls, String responseDescription) {
        List<String> usableSourceImages = sourceImageUrls == null ? List.of() : sourceImageUrls.stream()
                .filter(StringUtils::hasText)
                .toList();

        if (properties.mockEnabled()) {
            return new AiDesignResult(selectMockImage(usableSourceImages), buildMockDescription(responseDescription, usableSourceImages.size()));
        }
        if (!StringUtils.hasText(properties.token())) {
            throw new AiProviderException(
                    "Thiếu HF_TOKEN nên backend không thể gọi Hugging Face. "
                            + "Hãy cấu hình HF_TOKEN hoặc đặt HF_MOCK_ENABLED=true nếu muốn dùng ảnh demo."
            );
        }

        try {
            byte[] imageBytes = restClient.post()
                    .uri(properties.modelUrl())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(buildPayload(prompt, usableSourceImages))
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

    private Map<String, Object> buildPayload(String prompt, List<String> sourceImageUrls) {
        if (sourceImageUrls.isEmpty()) {
            return Map.of("inputs", prompt);
        }

        return Map.of(
                "inputs", buildReferenceImageBase64(sourceImageUrls),
                "parameters", Map.of("prompt", prompt)
        );
    }

    private String buildReferenceImageBase64(List<String> sourceImageUrls) {
        List<BufferedImage> images = sourceImageUrls.stream()
                .map(this::readDataUrlImage)
                .toList();

        if (images.size() == 1) {
            return toPngBase64(images.get(0));
        }

        return toPngBase64(buildReferenceSheet(images));
    }

    private BufferedImage readDataUrlImage(String imageUrl) {
        byte[] bytes = readImageBytes(imageUrl);
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            if (image == null) {
                throw new AiProviderException("Không đọc được ảnh tham chiếu");
            }
            return image;
        } catch (IOException ex) {
            throw new AiProviderException("Không đọc được ảnh tham chiếu", ex);
        }
    }

    private byte[] readImageBytes(String imageUrl) {
        if (imageUrl.startsWith("data:image/")) {
            int commaIndex = imageUrl.indexOf(',');
            if (commaIndex < 0) {
                throw new AiProviderException("Data URL ảnh không hợp lệ");
            }
            return Base64.getDecoder().decode(imageUrl.substring(commaIndex + 1));
        }

        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            byte[] bytes = restClient.get()
                    .uri(imageUrl)
                    .retrieve()
                    .body(byte[].class);
            if (bytes == null || bytes.length == 0) {
                throw new AiProviderException("Không tải được ảnh tham chiếu gần nhất");
            }
            return bytes;
        }

        throw new AiProviderException("Chỉ hỗ trợ gửi ảnh dạng data URL hoặc URL HTTP(S) tới Hugging Face.");
    }

    private BufferedImage buildReferenceSheet(List<BufferedImage> images) {
        int columns = (int) Math.ceil(Math.sqrt(images.size()));
        int rows = (int) Math.ceil((double) images.size() / columns);
        int cellSize = 512;
        BufferedImage sheet = new BufferedImage(columns * cellSize, rows * cellSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = sheet.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setColor(java.awt.Color.WHITE);
        graphics.fillRect(0, 0, sheet.getWidth(), sheet.getHeight());

        for (int index = 0; index < images.size(); index++) {
            BufferedImage source = images.get(index);
            Image scaled = source.getScaledInstance(cellSize, cellSize, Image.SCALE_SMOOTH);
            int x = (index % columns) * cellSize;
            int y = (index / columns) * cellSize;
            graphics.drawImage(scaled, x, y, cellSize, cellSize, null);
        }

        graphics.dispose();
        return sheet;
    }

    private String toPngBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException ex) {
            throw new AiProviderException("Không thể mã hóa ảnh tham chiếu", ex);
        }
    }

    private String selectMockImage(List<String> sourceImageUrls) {
        if (sourceImageUrls.isEmpty()) {
            throw new AiProviderException("Chế độ demo cần ít nhất một ảnh upload hoặc ảnh gần nhất trong đoạn chat.");
        }
        return sourceImageUrls.get(sourceImageUrls.size() - 1);
    }

    private String buildMockDescription(String responseDescription, int sourceImageCount) {
        return "[Chế độ demo] " + responseDescription
                + " Backend đang trả lại ảnh tham chiếu gần nhất trong " + sourceImageCount
                + " ảnh được gửi đi vì HF_MOCK_ENABLED=true. "
                + "Để nhận ảnh AI thật từ Hugging Face, hãy cấu hình HF_TOKEN và đặt HF_MOCK_ENABLED=false.";
    }
}
