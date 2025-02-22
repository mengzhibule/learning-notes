package com.shawn.study.deep.in.java.configuration.converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import org.eclipse.microprofile.config.spi.Converter;

public class MutableConverters implements Converters {

  public static final int DEFAULT_PRIORITY = 100;

  private final Map<Class<?>, PriorityQueue<PrioritizedConverter<?>>> typedConverters =
      new HashMap<>();
  private final List<Converter<?>> converters = new ArrayList<>(16);

  public void addConverter(Converter<?> converter) {
    addConverter(converter, DEFAULT_PRIORITY);
  }

  public void addConverters(Converter<?>... converters) {
    Arrays.stream(converters).forEach(this::addConverter);
  }

  public void addConverter(Converter<?> converter, int priority) {
    Class<?> convertedType = resolveConvertedType(converter);
    addConverter(converter, priority, convertedType);
  }

  public void addConverter(Converter<?> converter, int priority, Class<?> convertedType) {
    PriorityQueue<PrioritizedConverter<?>> priorityQueue =
        typedConverters.computeIfAbsent(convertedType, t -> new PriorityQueue<>());
    PrioritizedConverter<?> prioritizedConverter = new PrioritizedConverter(converter, priority);
    priorityQueue.offer(prioritizedConverter);
    converters.add(converter);
  }

  protected Class<?> resolveConvertedType(Converter<?> converter) {
    assertConverter(converter);
    Class<?> convertedType = null;
    Class<?> converterClass = converter.getClass();
    while (converterClass != null) {
      convertedType = resolveConvertedType(converterClass);
      if (convertedType != null) {
        break;
      }

      Type superType = converterClass.getGenericSuperclass();
      if (superType instanceof ParameterizedType) {
        convertedType = resolveConvertedType(superType);
      }

      if (convertedType != null) {
        break;
      }
      // recursively
      converterClass = converterClass.getSuperclass();
    }

    return convertedType;
  }

  private void assertConverter(Converter<?> converter) {
    Class<?> converterClass = converter.getClass();
    if (converterClass.isInterface()) {
      throw new IllegalArgumentException(
          "The implementation class of Converter must not be an interface!");
    }
    if (Modifier.isAbstract(converterClass.getModifiers())) {
      throw new IllegalArgumentException(
          "The implementation class of Converter must not be abstract!");
    }
  }

  private Class<?> resolveConvertedType(Class<?> converterClass) {
    Class<?> convertedType = null;

    for (Type superInterface : converterClass.getGenericInterfaces()) {
      convertedType = resolveConvertedType(superInterface);
      if (convertedType != null) {
        break;
      }
    }

    return convertedType;
  }

  private Class<?> resolveConvertedType(Type type) {
    Class<?> convertedType = null;
    if (type instanceof ParameterizedType) {
      ParameterizedType pType = (ParameterizedType) type;
      if (pType.getRawType() instanceof Class) {
        Type rawType = pType.getRawType();
        if (Converter.class.isAssignableFrom((Class<?>) rawType)) {
          Type[] arguments = pType.getActualTypeArguments();
          if (arguments.length == 1 && arguments[0] instanceof Class) {
            convertedType = (Class<?>) arguments[0];
          }
        }
      }
    }
    return convertedType;
  }

  @Override
  public List<Converter<?>> getConverters(Class<?> convertedType) {
    PriorityQueue<PrioritizedConverter<?>> prioritizedConverters =
        typedConverters.get(convertedType);
    if (prioritizedConverters == null || prioritizedConverters.isEmpty()) {
      return Collections.emptyList();
    }
    List<Converter<?>> converters = new LinkedList<>();
    for (PrioritizedConverter<?> prioritizedConverter : prioritizedConverters) {
      converters.add(prioritizedConverter.getConverter());
    }
    return converters;
  }

  @Override
  public List<Converter<?>> getConverters() {
    return Collections.unmodifiableList(converters);
  }

  @Override
  public Converter<?> getConverter(Class<?> convertedType) {
    return getConverters(convertedType).iterator().next();
  }

  @Override
  public Iterator<Converter<?>> iterator() {
    List<Converter<?>> allConverters = new LinkedList<>();
    for (PriorityQueue<PrioritizedConverter<?>> converters : typedConverters.values()) {
      for (PrioritizedConverter<?> converter : converters) {
        allConverters.add(converter.getConverter());
      }
    }
    return allConverters.iterator();
  }
}
