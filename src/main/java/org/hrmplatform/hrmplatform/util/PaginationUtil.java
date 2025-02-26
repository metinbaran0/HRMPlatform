package org.hrmplatform.hrmplatform.util;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PaginationUtil {
    public static <T> List<T> paginate(List<T> data, int page, int size) {
        if (data == null || data.isEmpty()) {
            return List.of(); // Boş liste döndür
        }

        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, data.size());

        if (fromIndex >= data.size()) {
            return List.of(); // Sayfa dışına çıkıldıysa boş liste dön
        }

        return data.subList(fromIndex, toIndex);
    }
}
