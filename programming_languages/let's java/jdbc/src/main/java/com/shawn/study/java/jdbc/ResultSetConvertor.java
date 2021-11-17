package com.shawn.study.java.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetConvertor<T> {

  T convert(ResultSet rs) throws SQLException;
}