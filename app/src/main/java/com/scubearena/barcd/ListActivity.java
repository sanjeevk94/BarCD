package com.scubearena.barcd;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;


public class ListActivity extends AppCompatActivity {
        String[] categories = new String[] {
                "Contact",
                "Product",
                "Normal Text",
        };
        Button proceed;
        String selectedCategory;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_list);
            final ListView listView = findViewById(R.id.listview);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice,categories);
            listView.setAdapter(adapter);
            listView.setSelection(2);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                    Object clickItemObj = adapterView.getAdapter().getItem(index);
                    selectedCategory = clickItemObj.toString();
                    Intent intent = new Intent(ListActivity.this,QrGenerate.class);
                    intent.putExtra("category",selectedCategory);
                    startActivity(intent);
                    //Toast.makeText(ListActivity.this, "You clicked " + clickItemObj.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }