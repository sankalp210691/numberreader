/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sankalp.characterreader.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author sankalpkulshrestha
 */
public class ConfigProperties {

  private Properties props;

  public ConfigProperties(String propertiesFileName) throws IOException {
    props = new Properties();
    props.load(new FileInputStream(new File(propertiesFileName)));
  }

  public List<Integer> getList(Class<Integer> aClass, String key) {
    String[] valueArray = props.getProperty(key).split(",");
    List<Integer> integerList = new ArrayList();
    for (String value : valueArray) {
      integerList.add(Integer.parseInt(value));
    }
    return integerList;
  }

  public int getInt(String key, int defaultValue) {
    if (props.containsKey(key)) {
      return Integer.parseInt(props.getProperty(key));
    } else {
      return defaultValue;
    }
  }

  public String getString(String key) {
    return props.getProperty(key);
  }

  public double getDouble(String key, double defaultValue) {
    if (props.containsKey(key)) {
      return Double.parseDouble(props.getProperty(key));
    } else {
      return defaultValue;
    }
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    if (props.containsKey(key)) {
      return Boolean.parseBoolean(props.getProperty(key));
    } else {
      return defaultValue;
    }
  }
}
