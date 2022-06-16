package com.shawn.study.deep.in.spring.core.resource;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;

public class DependencyInjectionResourceLoaderDemo implements ResourceLoaderAware {

  private ResourceLoader resourceLoader; // 方法一

  @Autowired private ResourceLoader autowiredResourceLoader; // 方法二

  @Autowired private AbstractApplicationContext applicationContext; // 方法三

  @PostConstruct
  public void init() {
    System.out.println(
        "resourceLoader == autowiredResourceLoader : "
            + (resourceLoader == autowiredResourceLoader));
    System.out.println(
        "resourceLoader == applicationContext : " + (resourceLoader == applicationContext));
  }

  @Override
  public void setResourceLoader(ResourceLoader resourceLoader) {
    this.resourceLoader = resourceLoader;
  }

  public static void main(String[] args) {

    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    // 注册当前类作为 Configuration Class
    context.register(DependencyInjectionResourceLoaderDemo.class);
    // 启动 Spring 应用上下文
    context.refresh();
    // 关闭 Spring 应用上下文
    context.close();
  }
}
