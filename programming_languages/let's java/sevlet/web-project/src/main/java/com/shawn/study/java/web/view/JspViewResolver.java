package com.shawn.study.java.web.view;

public class JspViewResolver implements ViewResolver {

  private final String prefix;

  private final String suffix;

  public JspViewResolver(String prefix, String suffix) {
    this.prefix = prefix;
    this.suffix = suffix;
  }

  @Override
  public String resolve(String viewName) {
    String url = (prefix + viewName + suffix).replaceAll("//", "/");
    return url;
  }
}
