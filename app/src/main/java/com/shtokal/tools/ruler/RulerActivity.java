package com.shtokal.tools.ruler;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.shtokal.tools.R;

public class RulerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruler);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_ruler, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            RulerView rulerView = findViewById(R.id.ruler_view);
            if (rulerView.getUnitType() == RulerView.Unit.CM) {
                rulerView.setUnitType(RulerView.Unit.INCH);
            } else {
                rulerView.setUnitType(RulerView.Unit.CM);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
