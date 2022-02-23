package com.carpour.loggerbungeecord.Utils;

import com.carpour.loggerbungeecord.API.LiteBansUtil;
import com.carpour.loggerbungeecord.Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.carpour.loggerbungeecord.Utils.Data.fileDeletion;
import static com.carpour.loggerbungeecord.Utils.Data.isStaffEnabled;

public class FileHandler {

    private final Main main = Main.getInstance();

    private static File staffLogFolder;
    private static File chatLogFolder;
    private static File commandLogFolder;
    private static File loginLogFolder;
    private static File leaveLogFolder;
    private static File reloadLogFolder;
    private static File serverStartLogFolder;
    private static File serverStopLogFolder;
    private static File RAMLogFolder;
    private static File liteBansLogFolder;

    private static File staffLogFile;
    private static File chatLogFile;
    private static File commandLogFile;
    private static File loginLogFile;
    private static File leaveLogFile;
    private static File reloadLogFile;
    private static File serverStartLogFile;
    private static File serverStopLogFile;
    private static File RAMLogFile;
    private static File liteBansBansLogFile;
    private static File liteBansMuteLogFile;
    private static File liteBansKickLogFile;

    public FileHandler(File dataFolder) {

        dataFolder.mkdir();

        final File logsFolder = new File(dataFolder, "Logs");
        logsFolder.mkdirs();

        final Date date = new Date();
        final SimpleDateFormat filenameDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        staffLogFolder = new File(logsFolder, "Staff");
        staffLogFile = new File(staffLogFolder, filenameDateFormat.format(date) + ".log");

        chatLogFolder = new File(logsFolder, "Player Chat");
        chatLogFile = new File(chatLogFolder, filenameDateFormat.format(date) + ".log");

        commandLogFolder = new File(logsFolder, "Player Commands");
        commandLogFile = new File(commandLogFolder, filenameDateFormat.format(date) + ".log");

        loginLogFolder = new File(logsFolder, "Player Login");
        loginLogFile = new File(loginLogFolder, filenameDateFormat.format(date) + ".log");

        leaveLogFolder = new File(logsFolder, "Player Leave");
        leaveLogFile = new File(leaveLogFolder, filenameDateFormat.format(date) + ".log");

        reloadLogFolder = new File(logsFolder, "Reload");
        reloadLogFile = new File(reloadLogFolder, filenameDateFormat.format(date) + ".log");

        serverStartLogFolder = new File(logsFolder, "Server Start");
        serverStartLogFile = new File(serverStartLogFolder, filenameDateFormat.format(date) + ".log");

        serverStopLogFolder = new File(logsFolder, "Server Stop");
        serverStopLogFile = new File(serverStopLogFolder, filenameDateFormat.format(date) + ".log");

        RAMLogFolder = new File(logsFolder, "RAM");
        RAMLogFile = new File(RAMLogFolder, filenameDateFormat.format(date) + ".log");

        liteBansLogFolder = new File(logsFolder, "LiteBans");

        final File liteBansBansLogFolder = new File(liteBansLogFolder, "Bans");
        liteBansBansLogFile = new File(liteBansBansLogFolder, filenameDateFormat.format(date) + ".log");

        final File liteBansMuteLogFolder = new File(liteBansLogFolder, "Mutes");
        liteBansMuteLogFile = new File(liteBansMuteLogFolder, filenameDateFormat.format(date) + ".log");

        final File liteBansKickLogFolder = new File(liteBansLogFolder, "Kick");
        liteBansKickLogFile = new File(liteBansKickLogFolder, filenameDateFormat.format(date) + ".log");

        try {

            if (isStaffEnabled) staffLogFolder.mkdir();
            chatLogFolder.mkdir();
            commandLogFolder.mkdir();
            loginLogFolder.mkdir();
            leaveLogFolder.mkdir();
            reloadLogFolder.mkdir();
            serverStartLogFolder.mkdir();
            serverStopLogFolder.mkdir();
            RAMLogFolder.mkdir();
            if (LiteBansUtil.getLiteBansAPI() != null){

                liteBansLogFolder.mkdir();
                liteBansBansLogFolder.mkdir();
                liteBansMuteLogFolder.mkdir();
                liteBansKickLogFolder.mkdir();
            }

            if (isStaffEnabled) staffLogFile.createNewFile();
            chatLogFile.createNewFile();
            commandLogFile.createNewFile();
            loginLogFile.createNewFile();
            leaveLogFile.createNewFile();
            reloadLogFile.createNewFile();
            serverStartLogFile.createNewFile();
            serverStopLogFile.createNewFile();
            RAMLogFile.createNewFile();
            if (LiteBansUtil.getLiteBansAPI() != null){

                liteBansBansLogFile.createNewFile();
                liteBansMuteLogFile.createNewFile();
                liteBansKickLogFile.createNewFile();
            }

        } catch (IOException e) {

            e.printStackTrace();

        }
    }

    public static File getStaffLogFile() { return staffLogFile; }

    public static File getChatLogFile() { return chatLogFile; }

    public static File getCommandLogFile() { return commandLogFile; }

    public static File getLoginLogFile() { return loginLogFile; }

    public static File getLeaveLogFile() { return leaveLogFile; }

    public static File getReloadLogFile() { return reloadLogFile; }

    public static File getServerStartLogFile() { return serverStartLogFile; }

    public static File getServerStopLogFile() { return serverStopLogFile; }

    public static File getRAMLogFile() { return RAMLogFile; }

    public static File getLiteBansBansLogFile() { return liteBansBansLogFile; }

    public static File getLiteBansMuteLogFile() { return liteBansMuteLogFile; }

    public static File getLiteBansKickLogFile() { return liteBansKickLogFile; }

    public void deleteFile(File file) {

        if (fileDeletion <= 0 ){ return; }

        FileTime creationTime = null;

        try {

            creationTime = (FileTime) Files.getAttribute(file.toPath(), "creationTime");

        } catch (IOException e) {

            e.printStackTrace();
        }

        assert creationTime != null;
        final long offset = System.currentTimeMillis() - creationTime.toMillis();
        final long fileDeletionDays = main.getConfig().getInt("File-Deletion");
        final long maxAge = TimeUnit.DAYS.toMillis(fileDeletionDays);

        if(offset > maxAge) {

            file.delete();

        }
    }

    public void deleteFiles(){

        if (fileDeletion<= 0 ){ return; }

        if (isStaffEnabled){

            for (File staffLog : Objects.requireNonNull(staffLogFolder.listFiles())) {

                deleteFile(staffLog);

            }
        }

        for (File chatLog : Objects.requireNonNull(chatLogFolder.listFiles()))
        {

            deleteFile(chatLog);

        }

        for (File commandsLog : Objects.requireNonNull(commandLogFolder.listFiles()))
        {

            deleteFile(commandsLog);

        }

        for (File loginLog : Objects.requireNonNull(loginLogFolder.listFiles()))
        {

            deleteFile(loginLog);

        }

        for (File leaveLog : Objects.requireNonNull(leaveLogFolder.listFiles()))
        {

            deleteFile(leaveLog);

        }

        for (File reloadLog : Objects.requireNonNull(reloadLogFolder.listFiles()))
        {

            deleteFile(reloadLog);

        }

        for (File serverStartLog : Objects.requireNonNull(serverStartLogFolder.listFiles()))
        {

            deleteFile(serverStartLog);

        }

        for (File serverStopLog : Objects.requireNonNull(serverStopLogFolder.listFiles()))
        {

            deleteFile(serverStopLog);

        }

        for (File RAMLog : Objects.requireNonNull(RAMLogFolder.listFiles()))
        {

            deleteFile(RAMLog);

        }

        if (LiteBansUtil.getLiteBansAPI() != null){

            for (File liteBansLog : Objects.requireNonNull(liteBansLogFolder.listFiles()))
            {

                deleteFile(liteBansLog);

            }
        }
    }
}