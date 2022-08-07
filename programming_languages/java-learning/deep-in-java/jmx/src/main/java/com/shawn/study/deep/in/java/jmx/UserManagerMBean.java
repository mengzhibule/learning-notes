package com.shawn.study.deep.in.java.jmx;

public interface UserManagerMBean {

  int getId();

  void setId(int id);

  String getUsername();

  void setUsername(String username);

  String getPassword();

  void setPassword(String password);

  String getAddress();

  void setAddress(String address);

  String toString();
}
