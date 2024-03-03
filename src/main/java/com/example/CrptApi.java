// Объявляем пакет классов
package main.java.com.example;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Scanner;


public class CrptApi {

    static Scanner userInput = new Scanner(System.in);
    static String email, phone, password, inputDateLastScore;
    static boolean externalBool;
    static long daysNewScore;
    public static File userActions, dataUserTextFile, pushNumberFile, moveUserOfFunds,
    folderDebit, folderCredit, folderUser;
    static int accrualSensor, lastSearchIndex, percentDayCountDeb, percentDayCountCr;

    //private static final String BASE_URL = "https://ismp.crpt.ru/api/v3/lk/documents/create";
    
    public static void main(String[] argCommStr) throws IOException, ParseException {
        CrptApi crptApi = new CrptApi();
        crptApi.startMenu();
    }

    public void startMenu() throws IOException, ParseException {

        System.out.println("Добро пожаловать в приложение «WorkHonestSign»!");
        System.out.println("""
                -----------------------------------------------------------------
                1. Получить список товаров (recList).
                2. Получить информацию по товару (recInfo).
                3. Обновить информацию по товару (updateInfo).
                4. Выход из приложения (exit).""");
        while (!externalBool) {
            System.out.println("""
                    -----------------------------------------------------------------
                    Ввод необходимой команды:""");
            String command = userInput.next();
            switch (command) {
                case "recList" -> receiveList();
                case "recInfo" -> receiveInfo();
                case "updateInfo" -> updateInfo();
                case "exit" -> exitTheApplication();
            }
        }
        
    }

    private void receiveList() throws IOException, ParseException {
        
    }

    private void receiveInfo() throws IOException, ParseException {

    }

    private void updateInfo() throws IOException, ParseException {

    }

    private void exitTheApplication() throws IOException, ParseException {
        // Выход из среды выполнения кода
        Runtime.getRuntime().exit(0);
    }

}