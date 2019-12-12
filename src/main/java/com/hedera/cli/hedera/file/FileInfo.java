package com.hedera.cli.hedera.file;

import com.google.protobuf.ByteString;
import com.hedera.cli.hedera.Hedera;
import com.hedera.cli.models.TransactionManager;
import com.hedera.cli.shell.ShellHelper;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.crypto.ed25519.Ed25519PrivateKey;
import com.hedera.hashgraph.sdk.file.FileContentsQuery;
import com.hedera.hashgraph.sdk.file.FileCreateTransaction;
import com.hedera.hashgraph.sdk.file.FileId;
import com.hedera.hashgraph.sdk.file.FileInfoQuery;
import com.hederahashgraph.api.proto.java.FileGetContentsResponse;
import org.hjson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Command;

import java.io.*;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

@Component
@Command(name = "info", description = "|@fg(225) Queries the info of a file|@")
public class FileInfo implements Runnable {

    @Autowired
    private Hedera hedera;

    @Autowired
    private ShellHelper shellHelper;

    @Autowired
    private TransactionManager txManager;

    @Parameters(index = "0", description = "@|fg(225) File Id in the format of shardNum.realmNum.fileNum"
            + "%n@|bold,underline Usage:|@%n"
            + "@|fg(yellow) file info 0.0.1003|@")
    private String fileNumInString;

    @Override
    public void run() {
        try (Client client = hedera.createHederaClient()) {
            FileId fileId = FileId.fromString(fileNumInString);

            Ed25519PrivateKey operatorKey = hedera.getOperatorKey();

            byte[] fileContents = ("Hedera is great!").getBytes();

            // Create the new file and set its properties
            TransactionId txid = new FileCreateTransaction(client.setMaxTransactionFee(1000000000))
                    .addKey(operatorKey.getPublicKey()) // The public key of the owner of the file
                    .setContents(fileContents) // Contents of the file
                    .setExpirationTime(Instant.now().plus(Duration.ofSeconds(2592000))) // Set file expiration time in seconds
                    .execute(); // Submits transaction to the network and returns receipt which contains file ID

            //Print the file ID to console
            System.out.println(txid);
            System.out.println(txid.getAccountId());

//            System.out.println("The new file ID is " + newFile.getFileId().toString());
//
//            com.hedera.hashgraph.sdk.file.FileInfo fileInfo = new FileInfoQuery(client.setMaxTransactionFee(1000000000))
//                    .setFileId(newFile.getFileId())
//                    .execute();
//
//            shellHelper.printInfo("File info : " + fileInfo);
//            shellHelper.printInfo("File expiry time : " + fileInfo.getExpirationTime());
//            shellHelper.printInfo("File public key : " + fileInfo.getKeys());
//            shellHelper.printInfo("File size : " + fileInfo.getSize());
//
//            FileGetContentsResponse fileGetContentsResponse = new FileContentsQuery(client.setMaxTransactionFee(1000000000))
//                    .setFileId(newFile.getFileId())
//                    .execute();

//            boolean fileVerified = verifyFileContentAValidUTF8ByteSequence(fileContents.getFileContents().getContents());
//            String decodedText = decodeText(fileContents.getFileContents().getContents().toStringUtf8(), "UTF-8");
//            System.out.println("decoded text: " + fileVerified);
//            System.out.println(decodedText);
//            shellHelper.printSuccess("File content : " + fileContents);
//            shellHelper.printSuccess("File has file contents : " + fileContents.hasFileContents());
//            shellHelper.printSuccess("File get file contents file contents to sring: " + fileGetContentsResponse.getFileContents().getContents());
//            shellHelper.printSuccess("File get file contents utf 8 : " + fileGetContentsResponse.getFileContents().getContents().toStringUtf8());
//            shellHelper.printSuccess("File content hashcode : " + fileContents.hashCode());
//
//            JsonObject objJsonObject = new JsonObject();
//            objJsonObject.get(fileContents.getFileContents().getContents().toString());
//            System.out.println(objJsonObject);
//            System.out.println("2");
//
//            JsonObject objJsonObject1 = new JsonObject();
//            objJsonObject1.get(fileContents.getFileContents().getContents().toStringUtf8());
//            System.out.println(objJsonObject1);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            // do nothing here
        } catch (Exception e) {
            shellHelper.printError(e.getMessage());
        }
    }

    public String decodeText(String input, String encoding) throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(input.getBytes())))
                .readLine();
    }

    public boolean verifyFileContentAValidUTF8ByteSequence(ByteString byteString) throws UnsupportedEncodingException {
        return Arrays.equals(byteString.toByteArray(),
                new String(byteString.toByteArray(), "UTF-8").getBytes("UTF-8"));
    }
}