package ru.malnev.gbcloud.common.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.zip.CRC32;

public class Util
{
    private static final String DEFAULT_HASH_ALGORITHM = "MD5";

    @NotNull
    @SneakyThrows
    public static String hash(final @NotNull String value)
    {
        final @NotNull MessageDigest digest = MessageDigest.getInstance(DEFAULT_HASH_ALGORITHM);
        final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        final byte[] hex = digest.digest(bytes);
        return DatatypeConverter.printHexBinary(hex);
    }

    public static long calculateCheckSum(final @NotNull String filePath) throws IOException
    {
        final InputStream inputStream = new FileInputStream(filePath);
        final CRC32 crc = new CRC32();
        int bytesRead;
        byte[] byteBuffer = new byte[4096];
        while ((bytesRead = inputStream.read(byteBuffer)) != -1)
        {
            crc.update(byteBuffer, 0, bytesRead);
        }

        return crc.getValue();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AnnotatedClass
    {
        private Annotation annotation;
        private Class annotatedClass;
    }

    @Nullable
    public static AnnotatedClass getAnnotation(final @NotNull Class annotationClass,
                                               final @Nullable Class targetClass)
    {
        if (targetClass == null) return null;
        final Annotation annotation = targetClass.getAnnotation(annotationClass);
        if (annotation == null)
        {
            return getAnnotation(annotationClass, targetClass.getSuperclass());
        }
        return new AnnotatedClass(annotation, targetClass);
    }

    public static String getErrorDescription(final @NotNull Exception e)
    {
        return e.getClass().getSimpleName() + "[" + e.getMessage() + "]";
    }
}
