package com.company;

import java.io.*;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

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
            "6. Enter \"clear\" to clear currency data.\n" +
            "7. Enter \"quit/exit\" to exit.\n" +
            "8. Enter \"help\" for help information.\n" +
                                         "-------------------------------------------------------------\n";

    /**
     * help info for load data from file
     */
    private static String instructionsForCurrency="-------------------------instructions-------------------------\n" +
            "currency and amount format like this:\n\tUSD 1000\n\tHKD 100\n" +
                                              "--------------------------------------------------------------\n";

    private static String instructionsForExchange="-------------------------instructions-------------------------\n" +
            "currency exchange rate compared to USD format like this: \n" +
            "\thkd 0.128975\n" +
            "\tCNY 0.142868\n\n"+
            "--------------------------------------------------------------\n";

    private static ConcurrentHashMap<String, Double> currencyAmount = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, Double> exchangeRate = new ConcurrentHashMap<>();

    private static final long delay = 5000;
    private static final long intevalPeriod = 60 * 1000;

    private static final String regex="\\b[A-Za-z]{3}\\b";
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
                    if(line==null || line.length()<=0){
                        continue;
                    }
                    String[] arr = line.split(" ");
                    // add exchange data
                    if(Pattern.matches(regex, arr[0])){
                        try{
                            String currency = arr[0].toUpperCase();
                            if(temp.get(currency)==null){
                                temp.put(currency, Double.valueOf(arr[1]));
                            }else{
                                temp.replace(currency, Double.valueOf(arr[1]));
                            }
                        }catch (NumberFormatException e){
                            System.out.println("error: exchange format is invalid:"+line);
                            System.out.println(instructionsForExchange);
                            success=false;
                            break;
                        }
                    }else{
                        System.out.println("error: exchange format is invalid:"+line);
                        System.out.println(instructionsForExchange);
                        success=false;
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("error: file not found:" + f.getAbsoluteFile());
                success = false;
            } catch (IOException e) {
                System.out.println("error: file read error:"+f.getAbsoluteFile());
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
                    if(line==null || line.length()<=0){
                        continue;
                    }
                    String[] arr = line.split(" ");
                    // add currency data
                    if(Pattern.matches(regex, arr[0])){
                        try{
                            String currency = arr[0].toUpperCase();
                            if(temp.get(currency)==null){
                                temp.put(currency, Double.valueOf(arr[1]));
                            }else{
                                temp.replace(currency, temp.get(currency) + Double.valueOf(arr[1]));
                            }
                        }catch (NumberFormatException e){
                            System.out.println("error: currency format is invalid:"+line);
                            System.out.println(instructionsForCurrency);
                            success=false;
                            break;
                        }
                    }else{
                        System.out.println("error: currency format is invalid:"+line);
                        System.out.println(instructionsForCurrency);
                        success=false;
                        break;
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("error: file not found:"+f.getAbsoluteFile());
                success=false;
            } catch (IOException e) {
                System.out.println("error: file read error:"+f.getAbsoluteFile());
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


    private static boolean readDataIfFilesSpecified(String[] args){
        if(args!=null && args.length>0){
                boolean success=true;
                for(String arg: args){
                    if(arg.startsWith("-c") || arg.startsWith("-C")){
                        String currencyFile = arg.replaceFirst("-c", "").replaceFirst("-C","");
                        success = success && readCurrencyFile(currencyFile);
                        if(!success){
                            return success;
                        }
                    }
                    if(arg.startsWith("-e") || arg.startsWith("-E")){
                        String exFile = arg.replaceFirst("-e", "").replaceFirst("-E","");
                        success = success && readExchangeFile(exFile);
                        if(!success){
                            return success;
                        }
                    }
                }
            return success;
        }else{
            return false;
        }
    }
    public static void main(String[] args){

        if(args!=null && args.length>0){
            // read data from file if file specified throw start up parameters.
            boolean success = readDataIfFilesSpecified(args);
            if(!success){
                System.exit(-1);
            }
        }
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
                        System.out.println("bye.");
                        System.exit(0);
                    }else if("help".equalsIgnoreCase(input)){
                        System.out.println(instructions);
                    }else if("list".equalsIgnoreCase(input)){
                        list(currencyAmount, false);
                    }else if("clear".equalsIgnoreCase(input)){
                        currencyAmount.clear();
//                        exchangeRate.clear();
                        System.out.println("currency data is removed");
                    }else{
                        System.out.println("error: Invalid input, input \"help\" for help information.");
//                        System.out.println(instructions);
                    }
                }else if(arr.length==2){
//                    if(Pattern.matches(regex, arr[0])){
                    if("c".equalsIgnoreCase(arr[0])){
                        readCurrencyFile(arr[1]);
                    }else if("e".equalsIgnoreCase(arr[0])){
                        readExchangeFile(arr[1]);
                    }else if(Pattern.matches(regex, arr[0])){
                        // add currency data
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
//                            System.out.println("Action succeed.");
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
//                System.out.println("error: input failed, please try again or input \"help\" for help information");
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
                list(currencyAmount, true);
            }
        };
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(task, delay, intevalPeriod);
    }

    public static void list(Map<String, Double> currencyAmount, boolean fromTimer){
        if(currencyAmount.size()>0){
            System.out.println("-----------------------------------");
            for(Map.Entry<String, Double> entry: currencyAmount.entrySet()){
                if(entry.getValue().doubleValue()==0){
                    continue;
                }
                StringBuffer content = new StringBuffer(entry.getKey() +" "+formateDouble(entry.getValue()));
                if(exchangeRate.get(entry.getKey())!=null && !"USD".equalsIgnoreCase(entry.getKey())){
                    String currencyToUSD = formateDouble(entry.getValue() * exchangeRate.get(entry.getKey()));
                    content.append(" (USD "+currencyToUSD+")");
                }
                System.out.println(content.toString());
            }
            System.out.println("-----------------------------------");
        }else{
            if(!fromTimer){
                System.out.println("no data found.");
            }
        }
    }

    private static String formateDouble(double n) {
        return String.format("%.2f", n);
    }
}
