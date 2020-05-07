package ce.yildiz.edu.tr.calendar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class NewEventActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_new_event);

        defineViews();

        setSupportActionBar(toolbar);

    }


    private void defineViews() {
        toolbar = (Toolbar) findViewById(R.id.AddNewEventActivity_Toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "You clicked Back button Icon!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ToolBar_Item_Save:
                Toast.makeText(this, "You clicked Save Icon!", Toast.LENGTH_SHORT).show();
                break;
        }

        return true;
    }
}
