package com.hedera.cli.hedera.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedera.cli.models.AddressBook;
import com.hedera.cli.models.Network;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataDirectory {

  private String userHome = System.getProperty("user.home");
  public String directoryName = ".hedera";
  private String defaultNetworkName = "aspen";

  // Example usage:
  // String currentNetwork = DataDirectory.readFile("network.txt", "aspen");
  // String pathToSubDir = currentNetwork + File.separator + "accounts"
  public void mkHederaSubDir(String pathToSubDir) {
    Path subdirpath = Paths.get(pathToSubDir);
    Path path = Paths.get(userHome, directoryName, subdirpath.toString());

    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdirs();
    }
  }

  public String readJsonToMap(InputStream addressBookInputStream) {
    String networkName = "";
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
      List<Network> networks = addressBook.getNetworks();
      DataDirectory dataDirectory = new DataDirectory();
      for (Network network: networks) {
        String currentNetwork = dataDirectory.readFile("network.txt", defaultNetworkName);
        if (currentNetwork == null) return defaultNetworkName;
        if (currentNetwork.equals(network.getName())) {
          System.out.println("* " + network.getName());
          networkName = network.getName();
          return networkName;
        } else {
          System.out.println("  " + network.getName());
          networkName = network.getName();
          return networkName;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return networkName;
  }


  public String networkGetName(InputStream addressBookInputStream) {
    ObjectMapper objectMapper = new ObjectMapper();
    String nodeName = "";
    try {
      AddressBook addressBook = objectMapper.readValue(addressBookInputStream, AddressBook.class);
      List<Network> networks = addressBook.getNetworks();
      String currentNetwork = this.readFile("network.txt", defaultNetworkName);
      if (currentNetwork == null) return defaultNetworkName;
      for (Network network: networks) {
        if (currentNetwork.equals(network.getName())) {
          nodeName = network.getName();
          return nodeName;
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return nodeName;
  }

  public void writeFile(String fileName, String value) {
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    System.out.println("dirname: " + directoryName);
    System.out.println("filename: " + fileName);
    // write the data
    Path filePath = Paths.get(userHome, directoryName, fileName);
    File file = new File(filePath.toString());
    System.out.println("file: " + file);
    System.out.println("value: " + value);
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

  public String readFile(String pathToFile) {
    String value = null;
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }
    Path filePath = Paths.get(userHome, directoryName, pathToFile);

    BufferedReader br = null;
    try {
      File file = new File(filePath.toString());
      FileReader fr = new FileReader(file.getAbsolutePath());
      br = new BufferedReader(fr);
      value = br.readLine();
    } catch (IOException e) {
      // e.printStackTrace();
      return value;
    } finally {
      try {
        br.close();
      } catch (IOException e) {
        // System.err.println("An IOException was caught!");
        // e.printStackTrace();
        return value;
      }
    }

    return value;
  }

  public String readFile(String pathToFile, String defaultValue) {
    Path path = Paths.get(userHome, directoryName);
    boolean directoryExists = Files.exists(path);
    if (!directoryExists) {
      File directory = new File(path.toString());
      directory.mkdir();
    }

    // read the data from file
    Path filePath = Paths.get(userHome, directoryName, pathToFile);
    File file = new File(filePath.toString());
    boolean fileExists = Files.exists(filePath);
    if (!fileExists) {
      writeFile(pathToFile, defaultValue);
      return defaultValue;
    }

    try {
      FileReader fr = new FileReader(file.getAbsoluteFile());
      BufferedReader br = new BufferedReader(fr);
      defaultValue = br.readLine();
      br.close();
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(-1);
    }
    return defaultValue;
  }

  public void listFiles(String pathToSubDir) {
    String userHome = System.getProperty("user.home");
    String directoryName = ".hedera";
    Path subdirpath = Paths.get(pathToSubDir);
    Path path = Paths.get(userHome, directoryName, subdirpath.toString());

    try {
      Stream<Path> walk = Files.walk(path);
      List<String> result = walk.map(x -> x.toString())
              .filter(f -> f.endsWith(".json")).collect(Collectors.toList());
      if (result.isEmpty()) {
        System.out.println("No Hedera accounts have created in the current network");
      }
      result.forEach(System.out::println);
      walk.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}