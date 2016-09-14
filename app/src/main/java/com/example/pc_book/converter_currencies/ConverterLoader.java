package com.example.pc_book.converter_currencies;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ConverterLoader extends AsyncTaskLoader<ArrayList> {

    public static final String arg = "arg";
    private String mWord;

    public ConverterLoader(Context context, Bundle args) {
        super(context);
        if (args != null) {
            mWord = args.getString(arg);
        }
    }

    @Override
    public ArrayList loadInBackground() {

        String data = mWord;
        String date[] = data.split(" ");
        ArrayList result = new ArrayList();

        if (date[0].equals("convert")) {
            data = postHttp(date[1], "convert", "USDEUR");

            if (data != "") {
                String xml = xmlSplit(data);
                result = searchDataXml(xml, "convert");
            } else {
                result.add("not downloaded xml");
            }
        } else if (date[0].equals("table")) {

            //"месяц год"
            String year = date[1];

            for (int i = 0; i < 12; i++) {
                String eur = postHttp("" + (i + 1) + " " + year, "table", "EUR");
                String usd = postHttp("" + (i + 1) + " " + year, "table", "USD");
                ArrayList eurArr = new ArrayList();
                ArrayList usdArr = new ArrayList();

                eur = xmlSplit(eur);
                eurArr = searchDataXml(eur, "table");

                usd = xmlSplit(usd);
                usdArr = searchDataXml(usd, "table");


                double countValueEUR = 0, countValueUSD = 0;

                for (int j = 0; j < eurArr.size(); j++) {
                    if (!eurArr.get(j).toString().equals("")) {
                        countValueEUR += Double.parseDouble(inspectionDotDouble(eurArr.get(j).toString()));
                    }
                }
                for (int j = 0; j < usdArr.size(); j++) {
                    if (!usdArr.get(j).toString().equals("")) {
                        countValueUSD += Double.parseDouble(inspectionDotDouble(usdArr.get(j).toString()));
                    }
                }

                result.add("" + (countValueEUR / eurArr.size()));
                result.add("" + (countValueUSD / usdArr.size()));
            }

        } else {
            result = null;
        }

        return result;
    }

    @Override
    public void deliverResult(ArrayList result) {
        super.deliverResult(result);
    }


    public String inspectionDotDouble(String value) {

        StringBuilder v = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == ',') {
                v.append(".");
            } else {
                v.append("" + value.charAt(i));
            }
        }
        return v.toString();
    }

    private String xmlSplit(String xml) {

        StringBuilder resultXml = new StringBuilder();

        boolean flag = false;
        for (int i = 0; i < xml.length(); i++) {
            if (flag == true) {
                resultXml.append("" + xml.charAt(i));
            }
            if (xml.charAt(i) == '>' && flag == false) {
                flag = true;
            }
        }

        return resultXml.toString();
    }


    private ArrayList searchDataXml(String xmlRecords, String option) {
        ArrayList result = new ArrayList();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            ArrayList l = new ArrayList();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);

            if (option == "convert") {

                NodeList nodes = doc.getElementsByTagName("Valute");

                NodeList nodeError = doc.getElementsByTagName("ValCurs");
                Element elementError = (Element) nodeError.item(0);
                String error = getCharacterDataFromElement(elementError).toString();

                if (error.equals("Error in parameters")) {
                    result.add(error);
                }

                for (int i = 0; i < nodes.getLength(); i++) {
                    Element element = (Element) nodes.item(i);

                    NodeList name = element.getElementsByTagName("CharCode");
                    Element line = (Element) name.item(0);
                    if (getCharacterDataFromElement(line).toString().equals("EUR") ||
                            getCharacterDataFromElement(line).toString().equals("USD")) {

                        String charCode = getCharacterDataFromElement(line).toString();
                        NodeList title = element.getElementsByTagName("Value");
                        line = (Element) title.item(0);
                        String value = getCharacterDataFromElement(line).toString();

                        result.add(charCode);
                        result.add(value);
                    }
                }

            } else if (option == "table") {
                NodeList nodes = doc.getElementsByTagName("Record");

                for (int i = 0; i < nodes.getLength() - 1; i++) {
                    Element element = (Element) nodes.item(i);

                    NodeList name = element.getElementsByTagName("Value");
                    Element line = (Element) name.item(0);
                    String value = getCharacterDataFromElement(line).toString();

                    result.add(value);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    private String postHttp(final String urlToRead, String option, String v) {
        String resultHttp = "";
        StringBuilder result = new StringBuilder();
        URL url;
        HttpURLConnection conn;
        BufferedReader rd;
        String line;
        String urlDate = "";
        if (option.equals("convert") && v.equals("USDEUR")) {
            urlDate = "http://www.cbr.ru/scripts/XML_daily_eng.asp?date_req=" + urlToRead;
        } else if (option.equals("table") && v.equals("EUR") || option.equals("table") && v.equals("USD")) {
            String date[] = urlToRead.split(" ");
            int month = Integer.parseInt(date[0]);
            int year = Integer.parseInt(date[1]);
            String value1 = "";
            String value2 = "";
            String number = "";

            if (month == 12) {
                value1 += "" + month;
                value2 += "" + month;
                number = "31";
            } else if (month < 9) {
                value1 += "0" + month;
                value2 += "0" + (month + 1);
                number = "01";
            } else if (month == 9) {
                value1 += "0" + month;
                value2 += "" + (month + 1);
                number = "01";
            } else if (month >= 10) {
                value1 += "" + month;
                value2 += "" + (month + 1);
                number = "01";
            }

            if (v.equals("EUR")) {
                urlDate = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=01/" + value1 + "/" + year + "&date_req2=" + number + "/" + value2 + "/" + year + "&VAL_NM_RQ=R01239";
            } else if (v.equals("USD")) {
                urlDate = "http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=01/" + value1 + "/" + year + "&date_req2=" + number + "/" + value2 + "/" + year + "&VAL_NM_RQ=R01235";
            }
        }

        try {
            url = new URL(urlDate);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            resultHttp = result.toString();
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultHttp;
    }
}
