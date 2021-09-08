package ru.vsu.csf.Sashina;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Files {

    public static List<String> readFile (String name) {
        try {
            List<String> list = new ArrayList<>();
            BufferedReader br = new BufferedReader(new FileReader(name));
            String s = br.readLine();
            while (s != null) {
                list.add(s);
                s = br.readLine();
            }
            br.close();
            return list;
        } catch (Exception exp) {
            return null;
        }
    }

    public static void writeToFile (JTextArea text, String result, String name) {
        try {
            PrintWriter pov = new PrintWriter(new FileWriter(name, true));
            String s = text.getText();
            String [] arr = s.split("\n");
            for (String s0 : arr)
                pov.println(s0);
            pov.println(result);
            pov.close();
        } catch (Exception exp) {
            return;
        }
    }
}
