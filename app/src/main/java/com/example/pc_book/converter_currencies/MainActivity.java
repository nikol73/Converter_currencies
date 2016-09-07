package com.example.pc_book.converter_currencies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Конвертер валют");
        eventElement();
        eventButtons();
    }

    Spinner spinnerMonth, spinnerNumber, spinnerYear;
    Spinner spinnerCurrency1, spinnerCurrency2;
    TextView nameCurrency, nameDate, nameValue, valueV;
    Button buttonConverter, buttonTable;

    int fromYear = 2001, beforeYear = 2016;

    private Loader<String> mLoader;


    public void numberSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.optionMonth);
        int value = spinner.getSelectedItemPosition();

        if (value == 1 || value == 3 || value == 5 || value == 7 || value == 8 || value == 10 || value == 12) {
            value = 31;
        } else if (value == 4 || value == 6 || value == 9 || value == 11) {
            value = 30;
        } else if (value == 2) {
            value = 29;
        } else if (value == 0) {
            value = 31;
        }

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < value; i++) {
            if (i == 0) {
                list.add("Число");
            }
            list.add("" + (i + 1));
        }

        addlistSpiner(list, R.id.optionNumber);
    }

    public void monthSpinner() {
        String month[] = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < month.length; i++) {
            if (i == 0) {
                list.add("Месяц");
            }
            list.add((i + 1) + ". " + month[i]);
        }
        addlistSpiner(list, R.id.optionMonth);
    }

    public void yearSpinner() {
        int year = beforeYear - fromYear;


        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < year; i++) {
            if (i == 0) {
                list.add("Год");
            }
            list.add("" + (fromYear + (i + 1)));
        }
        addlistSpiner(list, R.id.optionYear);
    }

    public void addlistSpiner(ArrayList<String> list, int id) {
        final Spinner spinner = (Spinner) findViewById(id);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    public void eventElement() {
        monthSpinner();
        yearSpinner();

        nameCurrency = (TextView) findViewById(R.id.textViewIzV);
        nameDate = (TextView) findViewById(R.id.textViewDate);
        nameValue = (TextView) findViewById(R.id.textViewValue);
        valueV = (EditText) findViewById(R.id.editTextValue);

        spinnerMonth = (Spinner) findViewById(R.id.optionMonth);
        spinnerNumber = (Spinner) findViewById(R.id.optionNumber);
        spinnerYear = (Spinner) findViewById(R.id.optionYear);
        spinnerCurrency1 = (Spinner) findViewById(R.id.optionCurrency1);
        spinnerCurrency2 = (Spinner) findViewById(R.id.optionCurrency2);

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                numberSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }


    public String checkDataSpinner() {

        final int valueNumber = spinnerNumber.getSelectedItemPosition(),
                valueMonth = spinnerMonth.getSelectedItemPosition(),
                valueYear = spinnerYear.getSelectedItemPosition(),

                valueCurrency1 = spinnerCurrency1.getSelectedItemPosition(),
                valueCurrency2 = spinnerCurrency2.getSelectedItemPosition();

        EditText value = (EditText) findViewById(R.id.editTextValue);
        String valuestring = value.getText().toString();
        String result;

        if (valueNumber != 0 && valueMonth != 0 && valueYear != 0
                && valueCurrency1 != 0 && valueCurrency2 != 0 && !valuestring.equals("")) {

            String number = valueNumber < 10 ? "0" + valueNumber : "" + valueNumber,
                    month = valueMonth < 10 ? "0" + valueMonth : "" + valueMonth,
                    year = "" + (valueYear + fromYear),

                    currency1 = valueCurrency1 == 1 ? "Dollar" : "Euro",
                    currency2 = valueCurrency2 == 1 ? "Dollar" : "Euro";

            String date = number + "/" + month + "/" + year;
            result = date + " " + currency1 + " " + currency2;
        } else {
            result = null;
        }

        return result;
    }

    public void eventButtons() {
        buttonConverter = (Button) findViewById(R.id.buttonConverter);
        buttonTable = (Button) findViewById(R.id.buttonTable);

        buttonConverter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String data = checkDataSpinner();

                buttonConverter.setText("ЗАГРУЗКА...");

                if (data != null) {
                    resetLoader("convert " + data);
                    mLoader.onContentChanged();
                } else {
                    Message("Не все поля заполненны!");
                    buttonConverter.setText("КОНВЕРТИРОВАТЬ");
                }
            }
        });

        buttonTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableActivity();
            }
        });
    }

    private void tableActivity() {
        Intent in = new Intent(this, TableActivity.class);
        startActivity(in);
    }


    public void converter(String valute) {

        String data[] = checkDataSpinner().split(" ");
        String currency1 = data[1];
        String currency2 = data[2];
        String valuteData[] = valute.split(" ");

        //"Dollar" "Euro"

        double number = Double.parseDouble(inspectionDotDouble(valueV.getText().toString()));

        double dollar = 0, euro = 0;

        for (int i = 0; i < valuteData.length; i++) {
            if (valuteData[i].equals("USD")) {
                dollar = Double.parseDouble(inspectionDotDouble(valuteData[i + 1]));
            } else if (valuteData[i].equals("EUR")) {
                euro = Double.parseDouble(inspectionDotDouble(valuteData[i + 1]));
            }
        }

        if (currency1.equals("Dollar") && currency2.equals("Euro")) {
            nameCurrency.setText("Доллар -> Евро");
            nameDate.setText("На " + data[0]);

            number = (number * dollar) / euro;

            nameValue.setText("" + String.format("%.4f", number));

        } else if (currency1.equals("Euro") && currency2.equals("Dollar")) {
            nameCurrency.setText("Евро -> Доллар");
            nameDate.setText("На " + data[0]);

            number = (number * euro) / dollar;

            nameValue.setText("" + String.format("%.4f", number));

        } else if (currency1.equals("Dollar") && currency2.equals("Dollar") ||
                currency1.equals("Euro") && currency2.equals("Euro")) {
            Message("Указанные валюты одинаковые!");
        }
    }

    public String inspectionDotDouble(String value) {

        String v = "";

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == ',') {
                v += ".";
            } else {
                v += value.charAt(i);
            }
        }
        return v;
    }

    public void Message(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Loader<String> mLoader = null;
        mLoader = new ConverterLoader(this, args);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data.equals("not downloaded xml")) {
            Message("Не удалось скачать данные");
        } else if (data.equals("Error in parameters")) {
            Message("Данные за текущий день отсутствуют");
        } else if (!data.equals("")) {
            converter(data);
        }
        buttonConverter.setText("КОНВЕРТИРОВАТЬ");
    }

    public void resetLoader(String DATA) {
        Bundle bundle = new Bundle();
        bundle.putString(ConverterLoader.arg, DATA);
        mLoader = getSupportLoaderManager().restartLoader(1, bundle, this);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }
}
