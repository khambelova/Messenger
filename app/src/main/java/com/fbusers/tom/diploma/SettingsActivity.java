package com.fbusers.tom.diploma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Tom on 19.05.2018.
 */

public class SettingsActivity extends AppCompatActivity {
    String[] colors = {"magenta","red","green","blue","black","white","gray","yellow","cyan"};
    String[] sizes = {"14","16","18","20","24","30"};
    String[] backgrounds = {"Голубые цветы","Голубые листья","Пузыри","Кофе","Забор","Серая вязка","Джинс","Лимоны","Светло-серый","Блекло-зеленый","Сиреневый","Лофт","Розовые цветы","Розы","Мягкий зеленый",
            "Украина","Белые цветы","Белые розы","Белый шелк"};
    int selectedColor =0;
    int selectedSize = 0;
    int selectedBackground = 0;

    @BindView(R.id.button_save)
    Button btn_save;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ArrayAdapter<String> adapter_col = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, colors);
        ArrayAdapter<String> adapter_sz = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sizes);
        ArrayAdapter<String> adapter_bg = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, backgrounds);
        adapter_col.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter_sz.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        adapter_bg.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        ButterKnife.bind(this);
        Spinner spinner_col = findViewById(R.id.spinner_color);
        Spinner spinner_sz = findViewById(R.id.spinner_size);
        Spinner spinner_bg = findViewById(R.id.spinner_background);
        spinner_col.setAdapter(adapter_col);
        spinner_sz.setAdapter(adapter_sz);
        spinner_bg.setAdapter(adapter_bg);
        spinner_col.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedColor=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinner_sz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectedSize=position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    });
        spinner_bg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBackground=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @OnClick(R.id.button_save)
    public void onClick(View view){
            Intent intent = new Intent();
            intent.putExtra("color", selectedColor);
            intent.putExtra("size", selectedSize);
            intent.putExtra("background", selectedBackground);
            setResult(RESULT_OK, intent);
            finish();
    }

}
