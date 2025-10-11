package com.xuecheng.system.model.utils;

import com.xuecheng.system.model.enums.BaseEnum;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumUtil {
    // 枚举类 -> code -> 枚举对象 的缓存
    private static final Map<Class<?>, Map<String, ?>> ENUM_CACHE = new ConcurrentHashMap<>();

    // 根据 code 获取枚举对象
    @SuppressWarnings("unchecked")
    public static <E extends Enum<E> & BaseEnum> E fromCode(Class<E> enumClass, String code) {
        Map<String, E> codeMap = (Map<String, E>) ENUM_CACHE.computeIfAbsent(enumClass, cls ->
                Stream.of(enumClass.getEnumConstants())
                        .collect(Collectors.toMap(BaseEnum::getCode, e -> e))
        );
        return codeMap.get(code);
    }

    // 根据 code 获取 desc
    public static <E extends Enum<E> & BaseEnum> String getDescByCode(Class<E> enumClass, String code) {
        E e = fromCode(enumClass, code);
        return e != null ? e.getDesc() : null;
    }
}
