/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.sankalp.characterreader.publicendpoints;

import com.sankalp.characterreader.common.Constants;
import com.sankalp.characterreader.neuralnetwork.NeuralNetwork;
import com.sankalp.characterreader.neuralnetwork.TrainingData;
import com.sankalp.characterreader.util.ConfigProperties;
import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author sankalpkulshrestha
 */
public class Interpreter extends HttpServlet {

  private ConfigProperties configProperties;
  private NeuralNetwork neuralNetwork;

  private static final Logger LOGGER = LoggerFactory.getLogger(Interpreter.class);

  @Override
  public void init(ServletConfig config) throws ServletException {
    try {
      configProperties = new ConfigProperties(Constants.PROPERTY_FILE);
      //Training time
      boolean readNeuralNetworkFile = configProperties.getBoolean(Constants.READ_NN_FILE_KEY,
          Constants.DEFAULT_READ_NN_FILE);
      if (readNeuralNetworkFile) {
        LOGGER.debug("Reading NN file");
        System.out.println("Reading NN file");
        try (ObjectInputStream ois
            = new ObjectInputStream(new FileInputStream(configProperties.getString(
                        Constants.NEURAL_NETWORK_FILE_KEY)))) {

                      neuralNetwork = (NeuralNetwork) ois.readObject();

                    } catch (Exception ex) {
                      ex.printStackTrace();
                    }

      } else {
        LOGGER.debug("No NN file, hence trying to train");
        double learningRate = configProperties.getDouble(Constants.LEARNING_RATE_KEY,
            Constants.DEFAULT_LEARNING_RATE);
        List<Integer> hiddenLayerNeuronCountList = configProperties.getList(Integer.class,
            Constants.HIDDEN_LAYER_NEURONS_KEY);
        List<NeuralNetwork.Layer> hiddenLayerList = new ArrayList();
        for (int hiddenLayerNeuronCount : hiddenLayerNeuronCountList) {
          hiddenLayerList.add(new NeuralNetwork.Layer(hiddenLayerNeuronCount));
        }
        LOGGER.debug("Initiating NN");
        neuralNetwork = new NeuralNetwork(new NeuralNetwork.Layer(configProperties
            .getInt(Constants.UI_ALLOWED_WIDTH_KEY, Constants.DEFAULT_UI_ALLOWED_WIDTH_KEY)
            * configProperties.getInt(Constants.UI_ALLOWED_HEIGHT_KEY,
                Constants.DEFAULT_UI_ALLOWED_HEIGHT_KEY)),
            new NeuralNetwork.Layer(Constants.OUTPUT_LAYER_COUNT), hiddenLayerList, learningRate);
        LOGGER.debug("Initiated NN. Gathering training data");
        TrainingData trainingData = new TrainingData(new File(configProperties.getString(
            Constants.TRAINING_DATA_FOLDER_KEY)), configProperties.getInt(
                Constants.TRAINING_SAMPLE_COUNT_KEY, Constants.DEFAULT_TRAINING_SAMPLE_COUNT),
            new File(
                configProperties.getString(Constants.TRAINING_DATA_LABEL_FILE_KEY)));
        LOGGER.debug("Training data gathered");
        int totalEpochs = configProperties.getInt(Constants.TOTAL_EPOCHS_KEY,
            Constants.DEFAULT_TOTAL_EPOCHS);
        for (int index = 0; index < totalEpochs; index++) {
          System.out.println("---------- EPOCH " + index + " ----------");
          LOGGER.info("---------- EPOCH " + index + " ----------");
          neuralNetwork.train(trainingData);

          TrainingData testData = new TrainingData(new File(configProperties.getString(
              Constants.TESTING_DATA_FOLDER_KEY)), configProperties.getInt(
                  Constants.TESTING_SAMPLE_COUNT_KEY, Constants.DEFAULT_TESTING_SAMPLE_COUNT),
              new File(
                  configProperties.getString(Constants.TESTING_DATA_LABEL_FILE_KEY)));

          System.out.println("Training over");
          LOGGER.info("Training over");
          double accuracy = neuralNetwork.measureAccuracy(testData);
          System.out.println(accuracy);
          LOGGER.info(Double.toString(accuracy));
          if (index < totalEpochs - 1) {
            trainingData.shuffle();
            trainingData.reset();
          }
        }
        try (ObjectOutputStream oos
            = new ObjectOutputStream(new FileOutputStream(configProperties.getString(
                        Constants.NEURAL_NETWORK_FILE_KEY)))) {

                      oos.writeObject(neuralNetwork);
                      System.out.println("Done");

                    } catch (Exception ex) {
                      ex.printStackTrace();
                    }
      }
      System.out.println("Model trained. Please draw on the UI");
      LOGGER.info("Model trained. Please draw on the UI");
    } catch (IOException ex) {
      LOGGER.error(ex + "");
    }
  }

  /**
   * Handles the HTTP <code>POST</code> method.
   *
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException if a servlet-specific error occurs
   * @throws IOException if an I/O error occurs
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html;charset=UTF-8");
    PrintWriter printwriter = response.getWriter();

    StringBuffer jb = new StringBuffer();
    String line = null;
    BufferedReader reader = request.getReader();
    while ((line = reader.readLine()) != null) {
      jb.append(line);
    }
    String base64Image = URLDecoder.decode(jb.toString().substring(6), "UTF-8");
    base64Image = base64Image.split(",")[1];
    byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

    int width = image.getWidth();
    int height = image.getHeight();

    if (width == configProperties.getInt(Constants.UI_ALLOWED_WIDTH_KEY,
        Constants.DEFAULT_UI_ALLOWED_WIDTH_KEY) && height == configProperties.getInt(
            Constants.UI_ALLOWED_HEIGHT_KEY, Constants.DEFAULT_UI_ALLOWED_HEIGHT_KEY)) {
      int output = neuralNetwork.test(new TrainingData.Sample(image, 0));
      printwriter.write(output + "");
    } else {
      printwriter.write(Constants.UI_INVALID_IMAGE_SIZE_ERROR + ": width = " + width
          + " & height = " + height);
    }
    printwriter.flush();
    printwriter.close();
  }
}
