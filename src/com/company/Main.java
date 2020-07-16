package com.company;

import java.io.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    /**
     * help info
     */
    private static String instructions="-----------------------instructions---------------------------\n" +
            "1. Input your currency and amount like this(case insensitive):\n\tUSD 1000\n\thkd 100\n" +
            "2. Set currency exchange rate compared to USD like this(case insensitive): \n" +
            "\tex hkd 0.128975\n" +
            "\tex CNY 0.142868\n" +
            "3. Enter \"c currencyPath.txt\" to specify the currency data file path, you should replace \"currencyPath.txt\" with your own file path.\n" +
            "4. Enter \"e exchangeRatePath.txt\" to specify the exchange rate data file path, you should replace \"exchangeRatePath.txt\" with your own file path.\n" +
            "5. Enter \"list\" to show all data.\n" +
            "6. Enter \"clear\" to clear all data.\n" +
            "7. Enter \"quit/exit\" to exit.\n" +
            "8. Enter \"help\" for help information.\n" +
            "--------------------------------------------------\n";

    /**
     * help info for load data from file
     */
    private static String instructionsForFile="-------------------------instructions-------------------------\n" +
            "1. currency and amount format like this:\nUSD 1000\nHKD 100\n" +
            "2. currency exchange rate compared to USD format like this: \n" +
            "hkd 0.128975\n" +
            "CNY 0.142868\n\n"+
                                              "--------------------------------------------------------------\n";

    private static ConcurrentHashMap<String, Double> currencyAmount = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Double> exchangeRate = new ConcurrentHashMap<>();

    /**
     * load exchange rate file
     * @param filePath
     * @throws Exception
     */
    private static boolean readExchangeFile(String filePath) {
        boolean success=true;
        if(filePath!=null && filePath.length()>0){
            ConcurrentHashMap<String, Double> temp = new ConcurrentHashMap<>();
            File f = new File(filePath);
            BufferedReader bw = null;
            try {
                bw = new BufferedReader(new FileReader(f));
                String line = null;
                while((line = bw.readLine()) != null){
                    line = line.trim();
                    String[] arr = line.split(" ");
                    // add exchange data
                    if(arr[0].length()==3){
                        String currency = arr[0].toUpperCase();
                        if(temp.get(currency)==null){
                            temp.put(currency, Double.valueOf(arr[1]));
                        }else{
                            temp.replace(currency, Double.valueOf(arr[1]));
                        }
                    }else{
                        System.out.println("error: exchange format is invalid:"+line);
                        success=false;
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("error: file not found");
                success=false;
            } catch (IOException e) {
                System.out.println("error: file read error");
                success=false;
            }
            if(success){
                exchangeRate=temp;
                System.out.println("load exchange rate data succeed:"+f.getAbsoluteFile());
            }
        }else{
            System.out.println("error: exchange file path error");
            success=false;
        }
        return success;
    }

    /**
     * load currency data file
     * @param filePath
     * @return
     */
    private static boolean readCurrencyFile(String filePath) {
        boolean success=true;
        if(filePath!=null && filePath.length()>0){
            ConcurrentHashMap<String, Double> temp = new ConcurrentHashMap<>();
            File f = new File(filePath);
            BufferedReader bw = null;
            try {
                bw = new BufferedReader(new FileReader(f));
                String line = null;
                while((line = bw.readLine()) != null){
                    line = line.trim();
                    String[] arr = line.split(" ");
                    // add currency data
                    if(arr[0].length()==3){
                        String currency = arr[0].toUpperCase();
                        if(temp.get(currency)==null){
                            temp.put(currency, Double.valueOf(arr[1]));
                        }else{
                            temp.replace(currency, temp.get(currency) + Double.valueOf(arr[1]));
                        }
                    }else{
//                        throw new Exception("currency format is invalid");
                        System.out.println("error: currency format is invalid:"+line);
                        success=false;
                        break;
                    }
                }

            } catch (FileNotFoundException e) {
                System.out.println("error: file not found");
                success=false;
            } catch (IOException e) {
                System.out.println("error: file read error");
                success=false;
            }
            if(success){
                currencyAmount=temp;
                System.out.println("load currency data succeed:"+f.getAbsoluteFile());
            }
        }else{
            System.out.println("error: currency file path error");
            success=false;
        }
        return success;
    }


    private static void readDataIfFileSpecified(String[] args){
        if(args!=null && args.length>0){
            try{
                for(String arg: args){
                    if(arg.startsWith("-c") || arg.startsWith("-C")){
                        String currencyFile = arg.replaceFirst("-c", "").replaceFirst("-C","");
                        readCurrencyFile(currencyFile);
                    }
                    if(arg.startsWith("-e") || arg.startsWith("-E")){
                        String exFile = arg.replaceFirst("-e", "").replaceFirst("-E","");
                        readExchangeFile(exFile);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(instructionsForFile);
                throw e;
            }
        }
    }
    public static void main(String[] args){

        // read data from file if file specified throw start up parameters.
        readDataIfFileSpecified(args);

        System.out.println(instructions);
        listDataInterval();

        while(true){
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String input = null;
            try {
                input = br.readLine();
                if(input==null || input.trim().length()<=0){
                    continue;
                }
                input = input.trim();
                String[] arr = input.split(" ");
//                System.out.println("Your input is:"+input);
                if(arr.length==1){
                    if("quit".equalsIgnoreCase(input) || "exit".equalsIgnoreCase(input)){
                        System.out.println("bye");
                        System.exit(0);
                    }else if("help".equalsIgnoreCase(input)){
                        System.out.println(instructions);
                    }else if("list".equalsIgnoreCase(input)){
                        list(currencyAmount);
                    }else if("clear".equalsIgnoreCase(input)){
                        currencyAmount.clear();
                        exchangeRate.clear();
                        System.out.println("all data is removed");
                    }else{
                        System.out.println("error: Invalid input, input \"help\" for help information.");
//                        System.out.println(instructions);
                    }
                }else if(arr.length==2){
                    // add currency data
                    if(arr[0].length()==3){
                        try{
                            String currency = arr[0].toUpperCase();
                            if(currencyAmount.get(currency)==null){
                                currencyAmount.put(currency, Double.valueOf(arr[1]));
                            }else{
                                currencyAmount.replace(currency, currencyAmount.get(currency) + Double.valueOf(arr[1]));
                            }
                        }catch (NumberFormatException e){
                            System.out.println("error: number format is invalid");
                        }
                    }else if("c".equalsIgnoreCase(arr[0])){
                        readCurrencyFile(arr[1]);
                    }else if("e".equalsIgnoreCase(arr[0])){
                        readExchangeFile(arr[1]);
                    }else{
                        System.out.println("error: Invalid input, input \"help\" for help information.");
                    }
                }else if(arr.length==3){
                    // set currency exchange rate
                    String action = arr[0];
                    if("ex".equalsIgnoreCase(action)){
                        try{
                            String currency = arr[1].toUpperCase();
                            if(exchangeRate.get(currency)==null){
                                exchangeRate.put(currency, Double.valueOf(arr[2]));
                            }else{
                                exchangeRate.replace(currency, Double.valueOf(arr[2]));
                            }
                            System.out.println("Action succeed.");
                        }catch (NumberFormatException e){
                            System.out.println("error: number format is invalid.");
//                            e.printStackTrace();
                        }
                    }else{
                        System.out.println("error: Invalid input, input \"help\" for help information.");
//                        System.out.println(instructions);
                    }
                }else{
                    System.out.println("error: Invalid input, input \"help\" for help information");
//                    System.out.println(instructions);
                }
            } catch (IOException e) {
                System.out.println("error: input failed, please try again or input \"help\" for help information");
            }
        }
    }

    /**
     * list currency data once per minute
     */
    public static void listDataInterval(){
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                list(currencyAmount);
            }
        };
        Timer timer = new Timer();
        long delay = 5000;
        long intevalPeriod = 60 * 1000;
        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }

    public static void list(Map<String, Double> currencyAmount){
        if(currencyAmount.size()>0){
            System.out.println("-----------------------------------");
            for(Map.Entry<String, Double> entry: currencyAmount.entrySet()){
                if(entry.getValue().doubleValue()==0){
                    continue;
                }
                StringBuffer content = new StringBuffer(entry.getKey() +" "+entry.getValue());
                if(exchangeRate.get(entry.getKey())!=null && !"USD".equalsIgnoreCase(entry.getKey())){
                    String currencyToUSD = formateDouble(entry.getValue() * exchangeRate.get(entry.getKey()));
                    content.append("(USD "+currencyToUSD+")");
                }
                System.out.println(content.toString());
            }
            System.out.println("-----------------------------------");
        }
    }

    private static String formateDouble(double n) {
        return String.format("%.2f", n);
    }
}
