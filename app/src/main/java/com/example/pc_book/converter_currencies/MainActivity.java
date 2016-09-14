package com.example.pc_book.converter_currencies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.NameTitle_ActivityMain));
        eventElement();
        eventButtons();
    }

    private Spinner spinnerCurrency1, spinnerCurrency2;
    private TextView nameCurrency, nameDate, nameValue, valueV;
    private Button buttonConverter, buttonTable;
    private DatePicker dataPicker;
    private Loader<ArrayList> mLoader;


    public void eventElement() {

        dataPicker = (DatePicker) findViewById(R.id.datePicker);

        nameCurrency = (TextView) findViewById(R.id.textViewIzV);
        nameDate = (TextView) findViewById(R.id.textViewDate);
        nameValue = (TextView) findViewById(R.id.textViewValue);
        valueV = (EditText) findViewById(R.id.editTextValue);

        spinnerCurrency1 = (Spinner) findViewById(R.id.optionCurrency1);
        spinnerCurrency2 = (Spinner) findViewById(R.id.optionCurrency2);

        dataPicker.setCalendarViewShown(false);
        dataPicker.setSpinnersShown(true);
    }


    public String checkDataSpinner() {

        final int valueCurrency1 = spinnerCurrency1.getSelectedItemPosition(),
                valueCurrency2 = spinnerCurrency2.getSelectedItemPosition();

        EditText value = (EditText) findViewById(R.id.editTextValue);
        String valuestring = value.getText().toString();
        String result;

        if (valueCurrency1 != 0 && valueCurrency2 != 0 && !valuestring.equals("")) {
            String day = dataPicker.getDayOfMonth() < 10 ? "0" + dataPicker.getDayOfMonth() : "" + dataPicker.getDayOfMonth(),
                    month = dataPicker.getMonth() < 10 ? "0" + dataPicker.getMonth() : "" + dataPicker.getMonth(),
                    year = "" + dataPicker.getYear(),

                    currency1 = valueCurrency1 == 1 ? "Dollar" : "Euro",
                    currency2 = valueCurrency2 == 1 ? "Dollar" : "Euro";

            String date = day + "/" + month + "/" + year;
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
                buttonConverter.setText(getString(R.string.textButtonConvert_loading));

                if (data != null) {
                    resetLoader("convert " + data);
                    mLoader.onContentChanged();
                } else {
                    Message(getString(R.string.Message_fields));
                    buttonConverter.setText(getString(R.string.textButtonConvert_noLoading));
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


    public void converter(ArrayList valute) {

        String data[] = checkDataSpinner().split(" ");
        String currency1 = data[1];
        String currency2 = data[2];
        ArrayList valuteData = valute;

        //"Dollar" "Euro"

        double number = Double.parseDouble(inspectionDotDouble(valueV.getText().toString()));

        double dollar = 0, euro = 0;

        for (int i = 0; i < valuteData.size(); i++) {
            if (valuteData.get(i).equals("USD")) {
                dollar = Double.parseDouble(inspectionDotDouble(valuteData.get(i + 1).toString()));
            } else if (valuteData.get(i).equals("EUR")) {
                euro = Double.parseDouble(inspectionDotDouble(valuteData.get(i + 1).toString()));
            }
        }

        if (currency1.equals("Dollar") && currency2.equals("Euro")) {
            nameCurrency.setText(getString(R.string.textTextView_nameCurrency_DE));
            nameDate.setText("На " + data[0]);

            number = (number * dollar) / euro;

            nameValue.setText("" + String.format("%.4f", number));

        } else if (currency1.equals("Euro") && currency2.equals("Dollar")) {
            nameCurrency.setText(getString(R.string.textTextView_nameCurrency_ED));
            nameDate.setText("На " + data[0]);

            number = (number * euro) / dollar;

            nameValue.setText("" + String.format("%.4f", number));

        } else if (currency1.equals("Dollar") && currency2.equals("Dollar") ||
                currency1.equals("Euro") && currency2.equals("Euro")) {
            Message(getString(R.string.Message_DollarEuro));
        }
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

    public void Message(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public Loader<ArrayList> onCreateLoader(int id, Bundle args) {
        Loader<ArrayList> mLoader = null;
        mLoader = new ConverterLoader(this, args);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {

        if (data.get(0).equals("not downloaded xml") && data.size() == 1) {
            Message(getString(R.string.Message_NotDownloadedXml));
        } else if (data.get(0).equals("Error in parameters") && data.size() == 1) {
            Message(getString(R.string.Message_ErrorInParameters));
        } else if (!data.get(0).equals("") && data.size() >= 1) {
            converter(data);
        }

        buttonConverter.setText(getString(R.string.textButtonConvert_noLoading));
    }

    public void resetLoader(String DATA) {
        Bundle bundle = new Bundle();
        bundle.putString(ConverterLoader.arg, DATA);
        mLoader = getSupportLoaderManager().restartLoader(1, bundle, this);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {
    }
}
