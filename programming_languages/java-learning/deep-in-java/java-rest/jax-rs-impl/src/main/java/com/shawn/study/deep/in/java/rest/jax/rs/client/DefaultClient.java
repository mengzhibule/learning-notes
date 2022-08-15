package com.shawn.study.deep.in.java.rest.jax.rs.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

public class DefaultClient implements Client {

  @Override
  public void close() {}

  @Override
  public WebTarget target(String uri) {
    return target(URI.create(uri));
  }

  @Override
  public WebTarget target(URI uri) {
    return target(UriBuilder.fromUri(uri));
  }

  @Override
  public WebTarget target(UriBuilder uriBuilder) {
    return new ImmutableWebTarget(uriBuilder);
  }

  @Override
  public WebTarget target(Link link) {
    return null;
  }

  @Override
  public Builder invocation(Link link) {
    return null;
  }

  @Override
  public SSLContext getSslContext() {
    return null;
  }

  @Override
  public HostnameVerifier getHostnameVerifier() {
    return null;
  }

  @Override
  public Configuration getConfiguration() {
    return null;
  }

  @Override
  public Client property(String name, Object value) {
    return null;
  }

  @Override
  public Client register(Class<?> componentClass) {
    return null;
  }

  @Override
  public Client register(Class<?> componentClass, int priority) {
    return null;
  }

  @Override
  public Client register(Class<?> componentClass, Class<?>... contracts) {
    return null;
  }

  @Override
  public Client register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
    return null;
  }

  @Override
  public Client register(Object component) {
    return null;
  }

  @Override
  public Client register(Object component, int priority) {
    return null;
  }

  @Override
  public Client register(Object component, Class<?>... contracts) {
    return null;
  }

  @Override
  public Client register(Object component, Map<Class<?>, Integer> contracts) {
    return null;
  }
}
