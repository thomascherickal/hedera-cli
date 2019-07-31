package com.hedera.cli.hedera.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataDirectory {

  static public void writeFile(String fileName, String value) {
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    // write the data
    Path filePath = Paths.get(userHome, directoryName, fileName);
    File file = new File(filePath.toString());
    try {
      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(value);
      bw.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
  }

  static public String readFile(String fileName) {
    String defaultNetwork = "aspen";
    String currentNetwork = defaultNetwork;
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    // read the data from file
    Path filePath = Paths.get(userHome, directoryName, fileName);
    File file = new File(filePath.toString());
    boolean fileExists = Files.exists(filePath);
    if (!fileExists) {
      return defaultNetwork;
    }

    try {
      FileReader fr = new FileReader(file.getAbsoluteFile());
      BufferedReader br = new BufferedReader(fr);
      currentNetwork = br.readLine();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    
    return currentNetwork;
  }
  
}