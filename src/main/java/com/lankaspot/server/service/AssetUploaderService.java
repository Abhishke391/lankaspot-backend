
package com.lankaspot.server.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lankaspot.server.dto.advertiser.uploads.UploadedAsset;
import com.lankaspot.server.enums.MediaType;
import com.lankaspot.server.properties.CloudinaryProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssetUploaderService {

    private static final Set<String> IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private static final Set<String> VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime"
    );

    private final Cloudinary cloudinary;
    private final CloudinaryProperties properties;

    /**
     * Upload multiple images or videos for an advertisement quote.
     *
     * The advertiser ID must come from trusted application logic.
     * quoteId must identify an existing AdQuote.
     *
     * All files in a single call must have the same MediaType.
     */
    public List<UploadedAsset> uploadAssets(
            UUID advertiserId,
            Long quoteId,
            MediaType mediaType,
            List<MultipartFile> files
    ) throws IOException {

        validateFiles(advertiserId, quoteId, mediaType, files);

        String folder = buildFolder(advertiserId, quoteId);

        List<UploadedAsset> uploadedAssets = new ArrayList<>();

        try {
            for (MultipartFile file : files) {

                Map<?, ?> result = cloudinary.uploader().upload(
                        file.getBytes(),
                        ObjectUtils.asMap(
                                "folder", folder,
                                "resource_type",
                                mediaType == MediaType.VIDEO
                                        ? "video"
                                        : "image",
                                "overwrite", false,
                                "unique_filename", true
                        )
                );

                UploadedAsset asset = new UploadedAsset(
                        (String) result.get("public_id"),
                        (String) result.get("secure_url"),
                        mediaType,
                        (String) result.get("format"),
                        (String) result.get("resource_type"),
                        ((Number) result.get("bytes")).longValue(),
                        numberAsInteger(result.get("width")),
                        numberAsInteger(result.get("height")),
                        numberAsDouble(result.get("duration"))
                );

                uploadedAssets.add(asset);

                log.info(
                        "Asset uploaded successfully. quoteId={}, mediaType={}",
                        quoteId,
                        mediaType
                );
            }

            return uploadedAssets;

        } catch (Exception exception) {

            // Attempt to remove files uploaded by this batch.
            for (UploadedAsset asset : uploadedAssets) {
                try {
                    deleteUploadedAsset(
                            advertiserId,
                            quoteId,
                            asset.publicId(),
                            asset.resourceType()
                    );
                } catch (Exception cleanupException) {
                    exception.addSuppressed(cleanupException);

                    log.error(
                            "Failed to clean up asset after upload failure. "
                                    + "quoteId={}, publicId={}",
                            quoteId,
                            asset.publicId(),
                            cleanupException
                    );
                }
            }

            if (exception instanceof IOException ioException) {
                throw ioException;
            }

            throw new IOException(
                    "Failed to upload assets to Cloudinary",
                    exception
            );
        }
    }

    /**
     * Delete a Cloudinary asset.
     *
     * Call this only after the business layer has verified ownership
     * and the deletion has been approved.
     */
    public void deleteUploadedAsset(
            UUID advertiserId,
            Long quoteId,
            String publicId,
            String resourceType
    ) throws IOException {

        validateLocation(advertiserId, quoteId);

        if (publicId == null || publicId.isBlank()) {
            throw new IllegalArgumentException(
                    "Cloudinary public ID is required"
            );
        }

        if (!"image".equals(resourceType)
                && !"video".equals(resourceType)) {
            throw new IllegalArgumentException(
                    "Unsupported Cloudinary resource type"
            );
        }

        Map<?, ?> result = cloudinary.uploader().destroy(
                publicId,
                ObjectUtils.asMap(
                        "resource_type", resourceType,
                        "invalidate", true
                )
        );

        Object deletionResult = result.get("result");

        if (!"ok".equals(deletionResult)
                && !"not found".equals(deletionResult)) {
            throw new IOException(
                    "Cloudinary failed to delete asset: " + publicId
            );
        }

        log.info(
                "Cloudinary asset deletion completed. quoteId={}, publicId={}",
                quoteId,
                publicId
        );
    }

    private void validateFiles(
            UUID advertiserId,
            Long quoteId,
            MediaType mediaType,
            List<MultipartFile> files
    ) {

        validateLocation(advertiserId, quoteId);

        if (mediaType == null) {
            throw new IllegalArgumentException(
                    "Media type is required"
            );
        }

        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one file is required"
            );
        }

        if (properties.getMaxImages() < 1
                || files.size() > properties.getMaxImages()) {
            throw new IllegalArgumentException(
                    "Maximum files per upload: "
                            + properties.getMaxImages()
            );
        }

        long maxFileSize =
                properties.getMaxFileSize().toBytes();

        long maxRequestSize =
                properties.getMaxRequestSize().toBytes();

        long totalSize = 0;

        Set<String> allowedTypes =
                mediaType == MediaType.IMAGE
                        ? IMAGE_TYPES
                        : VIDEO_TYPES;

        for (MultipartFile file : files) {

            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "Files must not be empty"
                );
            }

            if (file.getSize() > maxFileSize) {
                throw new IllegalArgumentException(
                        "A file exceeds the configured maximum size of "
                                + properties.getMaxFileSize()
                );
            }

            if (!allowedTypes.contains(file.getContentType())) {
                throw new IllegalArgumentException(
                        "Unsupported " + mediaType
                                + " content type: " + file.getContentType()
                );
            }

            try {
                totalSize = Math.addExact(totalSize, file.getSize());
            } catch (ArithmeticException exception) {
                throw new IllegalArgumentException(
                        "Total upload size is too large",
                        exception
                );
            }

            if (totalSize > maxRequestSize) {
                throw new IllegalArgumentException(
                        "Combined file size exceeds "
                                + properties.getMaxRequestSize()
                );
            }
        }
    }

    private String buildFolder(
            UUID advertiserId,
            Long quoteId
    ) {
        return properties.getBaseFolder()
                + "/" + advertiserId
                + "/" + quoteId;
    }

    private void validateLocation(
            UUID advertiserId,
            Long quoteId
    ) {
        if (advertiserId == null) {
            throw new IllegalArgumentException(
                    "Advertiser ID is required"
            );
        }

        if (quoteId == null || quoteId <= 0) {
            throw new IllegalArgumentException(
                    "A valid persisted quote ID is required"
            );
        }

        if (properties.getBaseFolder() == null
                || properties.getBaseFolder().isBlank()) {
            throw new IllegalStateException(
                    "Cloudinary base folder is not configured"
            );
        }
    }

    private Integer numberAsInteger(Object value) {
        return value instanceof Number number
                ? number.intValue()
                : null;
    }

    private Double numberAsDouble(Object value) {
        return value instanceof Number number
                ? number.doubleValue()
                : null;
    }
}
