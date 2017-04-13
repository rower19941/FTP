package ru.optimal_drive.ftp_uploader;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by hunte on 21.03.2017.
 */
public class FtpDownload {

    public static void main(String[] args) {
        if (args.length !=6){
            System.out.println("You must enter parameters in following order localFolder ftpFolder ftpHost " +
                    "ftpUserName ftpUserPassword ftpPort");
            System.out.println("local folder should be in drive root and have filename ODGFiles i.e C:/ODGFiles or D:/ODFFiles");
            System.out.println("ftpFolder is usually /ODFFiles/");
            System.out.println("ftpHost is ip address of the tablet");
            System.out.println("ftpUserName and ftpUserPassword is located in the tablet ftp server properties");
            return;
        }
        try {

            final String localFolder = args[0];//"C:/FtpFiles";
            final String ftpFolder = args[1];//"/Preview";
            final String ftpName = args[2];//"62.109.16.199";
            final String ftpUserName = args[3];//"ftp";
            final String ftpUserPassword = args[4]; //"";
            final String ftpPort = args[5];
            File localFolderFile = new File(localFolder);
            if (!localFolderFile.exists()){
                localFolderFile.mkdir();
            }
           // final int port = 21;
            Thread run = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            newFtpClient(localFolder, ftpFolder, ftpName, ftpUserName, ftpUserPassword,ftpPort);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
            run.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void newFtpClient(String localFolder,
                                     String ftpFolder,
                                     String ftpName,
                                     String ftpUserName,
                                     String ftpUserPassword, String ftpPort) throws IOException {
        int port = Integer.parseInt(ftpPort);
        FTPClient ftp = new FTPClient();

        //try to connect
        ftp.connect(ftpName, port);
        //login to server
        if (!ftp.login(ftpUserName, ftpUserPassword)) {
            ftp.logout();
        }
        int reply = ftp.getReplyCode();
        //FTPReply stores a set of constants for FTP reply codes.
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
        }

        //enter passive mode
        ftp.enterLocalPassiveMode();
        //get system name
        System.out.println("Remote system is " + ftp.getSystemType());
        //change current directory
        ftp.changeWorkingDirectory(ftpFolder);
        System.out.println("Current directory is " + ftp.printWorkingDirectory());

        //get list of filenames
        FTPFile[] ftpFiles = ftp.listFiles();

        File localFilesFolder = new File(localFolder);
        File[] listOFLocalFiles = localFilesFolder.listFiles();
        int counter = 0;
        long testSize = 0;
        if (ftpFiles != null && ftpFiles.length > 0) {
            for (FTPFile ftpFile : ftpFiles) {
                for (File localFile : listOFLocalFiles) {
                    testSize = localFile.length();
                    if(ftpFile.getName().equals(localFile.getName())){
                       if(ftpFile.getSize() == localFile.length())
                       {
                           System.out.println("local has file:" + localFile.getName()+
                                   " localSize:"+localFile.length()+
                                   " ftpSize" + ftpFile.getSize()
                           );

                           counter++;
                           break;
                       }


                    }

                }
                if(counter == 1 ){
                    counter = 0;
                }
                else {
                    System.out.println("Downloading  File is " + ftpFile.getName());
                    //get output stream
                    OutputStream output;
                    output = new FileOutputStream(localFolder + "/" + ftpFile.getName());
                    //get the ftpFile from the remote system
                    ftp.retrieveFile(ftpFile.getName(), output);
                    //close output stream
                    output.close();
                    continue;
                }
            }
        }
        ftp.logout();
        ftp.disconnect();
        System.out.println("----------------------------------------");
    }
}
