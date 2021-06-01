package com.shawn.study.algorithms.datastructure.queue;

public interface Queue<T> extends Iterable<T> {

  void offer(T elem);

  T poll();

  T peek();

  int size();

  boolean isEmpty();

}
