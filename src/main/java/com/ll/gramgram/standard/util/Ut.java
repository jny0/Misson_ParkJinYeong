package com.ll.gramgram.standard.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Base64;

public class Ut {
    public static class time {

        public static String diffFormat1Human(LocalDateTime time1, LocalDateTime time2) {
            String suffix = time1.isAfter(time2) ? "전" : "후";

            long diff = ChronoUnit.SECONDS.between(time1, time2);

            long diffSeconds = diff % 60;
            long diffMinutes = diff / (60) % 60;
            long diffHours = diff / (60 * 60) % 24;
            long diffDays = diff / (60 * 60 * 24);

            StringBuilder sb = new StringBuilder();

            if (diffDays > 0) sb.append(diffDays).append("일 ");
            if (diffHours > 0) sb.append(diffHours).append("시간 ");
            if (diffMinutes > 0) sb.append(diffMinutes).append("분 ");
            if (diffSeconds > 0) sb.append(diffSeconds).append("초 ");

            return sb.append(suffix).toString();
        }
    }

    public static class reflection {
        public static boolean setFieldValue(Object o, String fieldName, Object value) {
            Field field = null;

            try {
                field = o.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return false;
            }

            field.setAccessible(true);

            try {
                field.set(o, value);
            } catch (IllegalAccessException e) {
                return false;
            }

            return true;
        }

        public static <T> T getFieldValue(Object o, String fieldName, T defaultValue) {
            Field field = null;

            try {
                field = o.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                return defaultValue;
            }

            field.setAccessible(true);

            try {
                return (T) field.get(o);
            } catch (IllegalAccessException e) {
                return defaultValue;
            }
        }

        public static <T> T call(Object obj, String methodName, Object... args) {
            try {
                Class<?>[] argTypes = Arrays.stream(args)
                        .map(Object::getClass)
                        .toArray(Class<?>[]::new);

                Method method = obj.getClass().getDeclaredMethod(methodName, argTypes);
                method.setAccessible(true);
                return (T) method.invoke(obj, args);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        public static <T> T callArr(Object obj, String methodName, Object... args) {
            return call(obj, methodName, new Object[]{args});
        }
    }

    public static class hash {
        private static final MessageDigest md;

        static {
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        public static String sha256(String str) {
            // Convert the input string to bytes and update the MessageDigest
            byte[] inputBytes = str.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = md.digest(inputBytes);

            // Convert the hashed bytes to a Base64 encoded string
            return Base64.getEncoder().encodeToString(hashBytes);
        }
    }

    public static class url {
        public static String encode(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return str;
            }
        }

        public static String modifyQueryParam(String url, String paramName, String paramValue) {
            url = deleteQueryParam(url, paramName);
            url = addQueryParam(url, paramName, paramValue);

            return url;
        }

        public static String addQueryParam(String url, String paramName, String paramValue) {
            if (url.contains("?") == false) {
                url += "?";
            }

            if (url.endsWith("?") == false && url.endsWith("&") == false) {
                url += "&";
            }

            url += paramName + "=" + paramValue;

            return url;
        }

        private static String deleteQueryParam(String url, String paramName) {
            int startPoint = url.indexOf(paramName + "=");
            if (startPoint == -1) return url;

            int endPoint = url.substring(startPoint).indexOf("&");

            if (endPoint == -1) {
                return url.substring(0, startPoint - 1);
            }

            String urlAfter = url.substring(startPoint + endPoint + 1);

            return url.substring(0, startPoint) + urlAfter;
        }
    }
}
