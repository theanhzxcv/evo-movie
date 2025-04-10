package com.evo.util;

import org.modelmapper.ExpressionMap;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EvoModelMapperUtils {

    private static ModelMapper getMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        mapper.getConfiguration().setFieldMatchingEnabled(true).setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        return mapper;
    }

    public static <S, T> T toObject(S s, Class<S> fromClass, Class<T> targetClass, ExpressionMap<S, T> expressionMap) {
        ModelMapper modelMapper = getMapper();
        if (expressionMap != null) {
            modelMapper.typeMap(fromClass, targetClass).addMappings(expressionMap);
        }

        return modelMapper.map(s, targetClass);
    }

    public static <S, T> T toObject(S s, Class<T> targetClass) {
        if (s == null)
            return null;
        return getMapper().map(s, targetClass);
    }

    public static <S, T> List<T> listObjectToListModel(Iterable<S> source, Class<T> targetClass) {
        int size;

        if (source instanceof Collection<?>) {
            size = ((Collection<?>) source).size();
            if (size > 0) {
                List<T> lstTargetClass = new ArrayList<T>();
                for (S s : source) {
                    if (s != null) {
                        lstTargetClass.add(toObject(s, targetClass));
                    }
                }
                return lstTargetClass;
            }
        }
        return new ArrayList<T>();
    }

    public static <S, T> T cloneObject(S source, Class<T> targetClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T target = targetClass.getConstructor().newInstance();
        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            field.set(target, field.get(source));
        }
        return target;
    }
}
