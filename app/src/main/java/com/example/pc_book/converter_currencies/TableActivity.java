package com.example.pc_book.converter_currencies;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TableActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);
        setTitle(getString(R.string.NameTitle_TableActivity));
        eventButton();
        yearSpinner();
    }

    private final int fromYear = 2001, beforeYear = 2016;
    private Spinner yearSpinner;
    private Button buttonTable;
    private Loader<ArrayList> mLoader;

    public void yearSpinner() {
        int year = beforeYear - fromYear;
        yearSpinner = (Spinner) findViewById(R.id.spinnerYear);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < year; i++) {
            if (i == 0) {
                list.add("Год");
            }
            list.add("" + (fromYear + (i + 1)));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(adapter);
    }


    private void eventButton() {
        buttonTable = (Button) findViewById(R.id.buttonTable);

        buttonTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String spinnerYear = yearSpinner.getSelectedItem().toString();

                if (spinnerYear != "Год") {
                    LinearLayout l = (LinearLayout) findViewById(R.id.listTable);
                    l.removeAllViews();

                    buttonTable.setText(getString(R.string.textButtonTable_loading));
                    resetLoader("table " + spinnerYear);
                    mLoader.onContentChanged();
                } else {
                    Message(getString(R.string.Message_NoYear));
                }

            }
        });
    }


    private void addView(String month, String eur, String usd) {
        LayoutInflater intflater = getLayoutInflater();
        LinearLayout l = (LinearLayout) findViewById(R.id.listTable);

        View v = intflater.inflate(R.layout.table, l, false);
        TextView textMonth = (TextView) v.findViewById(R.id.textMonth);
        TextView textEUR = (TextView) v.findViewById(R.id.textEUR);
        TextView textUSD = (TextView) v.findViewById(R.id.textUSD);

        textMonth.setText(month);
        textEUR.setText(eur);
        textUSD.setText(usd);

        addViewParametr(v, l);
    }


    private void addViewParametr(View v, LinearLayout l) {
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        v.getLayoutParams().width = ActionBar.LayoutParams.MATCH_PARENT;
        v.getLayoutParams().height = ActionBar.LayoutParams.MATCH_PARENT;
        l.addView(v);
    }


    @Override
    public void onLoadFinished(Loader<ArrayList> loader, ArrayList data) {
        if (!data.equals("")) {
            createTable(data);
        }
        buttonTable.setText(getString(R.string.textButtonTable_noloading));
    }

    public void createTable(ArrayList data) {

        ArrayList EUR = new ArrayList();
        ArrayList USD = new ArrayList();

        String Month[] = getResources().getStringArray(R.array.month);

        for (int i = 0; i < data.size(); i++) {
            if (i % 2 == 0) {//EUR
                EUR.add(data.get(i));
            } else if (i % 2 == 1) { //USD
                USD.add(data.get(i));
            }
        }


        for (int i = 0; i < EUR.size(); i++) {
            addView(Month[i], String.format("%.2f", Double.parseDouble(EUR.get(i).toString())), String.format("%.2f", Double.parseDouble(USD.get(i).toString())));
        }
    }

    public void resetLoader(String DATA) {
        Bundle bundle = new Bundle();
        bundle.putString(ConverterLoader.arg, DATA);
        mLoader = getSupportLoaderManager().restartLoader(1, bundle, this);
    }


    @Override
    public Loader<ArrayList> onCreateLoader(int id, Bundle args) {
        Loader<ArrayList> mLoader = null;
        mLoader = new ConverterLoader(this, args);
        return mLoader;
    }

    @Override
    public void onLoaderReset(Loader<ArrayList> loader) {
    }

    public void Message(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}
