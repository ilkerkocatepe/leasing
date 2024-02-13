package dev.ilkerk.leasing.infrastracture.aws;

public record UploadResponse(String name, String uploadId, String path, String type, String eTag)
{}
