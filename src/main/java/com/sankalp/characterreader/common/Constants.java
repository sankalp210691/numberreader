/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sankalp.characterreader.common;

import java.util.Properties;

/**
 *
 * @author sankalpkulshrestha
 */
public class Constants {
  public static final String UI_ALLOWED_WIDTH_KEY = "UI_ALLOWED_WIDTH";
  public static final int DEFAULT_UI_ALLOWED_WIDTH_KEY = 28;
  public static final String UI_ALLOWED_HEIGHT_KEY = "UI_ALLOWED_HEIGHT";
  public static final int DEFAULT_UI_ALLOWED_HEIGHT_KEY = 28;
  public static final String PROPERTY_FILE = "/disk1/characterreader/properties/characterreader.properties";
  
  //Training
  public static final String LEARNING_RATE_KEY = "LEARNING_RATE";
  public static final double DEFAULT_LEARNING_RATE = 0.55;
  public static final String HIDDEN_LAYER_NEURONS_KEY = "HIDDEN_LAYER_NEURONS";
  public static final int OUTPUT_LAYER_COUNT = 10;
  public static final String TRAINING_DATA_FOLDER_KEY = "TRAINING_DATA_FOLDER";
  public static final String TRAINING_SAMPLE_COUNT_KEY = "TRAINING_SAMPLE_COUNT";
  public static final int DEFAULT_TRAINING_SAMPLE_COUNT = 60000;
  public static final String TRAINING_DATA_LABEL_FILE_KEY = "TRAINING_DATA_LABEL_FILE";
  public static final String TOTAL_EPOCHS_KEY = "TOTAL_EPOCHS";
  public static final int DEFAULT_TOTAL_EPOCHS = 30;
  public static final String TESTING_DATA_FOLDER_KEY = "TESTING_DATA_FOLDER";
  public static final String TESTING_SAMPLE_COUNT_KEY = "TESTING_SAMPLE_COUNT";
  public static final int DEFAULT_TESTING_SAMPLE_COUNT = 10000;
  public static final String TESTING_DATA_LABEL_FILE_KEY = "TESTING_DATA_LABEL_FILE";
  public static final String NEURAL_NETWORK_FILE_KEY = "NEURAL_NETWORK_FILE";
  public static final String READ_NN_FILE_KEY = "READ_NN_FILE";
  public static final boolean DEFAULT_READ_NN_FILE = false;
  
  //Errors
  public static final String UI_INVALID_IMAGE_SIZE_ERROR = "Invalid images uploaded";
  public static final String UI_INVALID_JSON_ERROR = "Invalid JSON";
  
}
