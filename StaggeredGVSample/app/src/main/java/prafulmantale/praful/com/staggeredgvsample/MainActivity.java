package prafulmantale.praful.com.staggeredgvsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;


public class MainActivity extends Activity {

    private StaggeredGridView staggeredGridView;

    public static TreeMap<String, List<String>> seedData;
    CutomAdapter adapter;
    List<String> keys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTestData();
        initialize();
    }

    private void createTestData(){

        seedData = new TreeMap<String, List<String>>();

        List<String> list1 = new ArrayList<String>();
        list1.add("Item 1");
        list1.add("Item 2");

        seedData.put("List 1", list1);

        List<String> list2 = new ArrayList<String>();
        list2.add("List 2 Item 1");
        list2.add("List 2 Item 2");
        list2.add("List 2 Item 3");
        list2.add("List 2 Item 4");

        seedData.put("List 2", list2);

        List<String> list3 = new ArrayList<String>();
        list3.add("List 3 Item 1");
        list3.add("List 3 Item 2");
        list3.add("List 3 Item 3");

        seedData.put("List 3", list3);


        List<String> list4 = new ArrayList<String>();
        list4.add("List 4 Item 1");
        list4.add("List 4 Item 2");
        list4.add("List 4 Item 3");
        list4.add("List 4 Item 4");
        list4.add("List 4 Item 5");

        seedData.put("List 4", list4);

        keys = new ArrayList<String>(seedData.keySet());

    }


    private void initialize(){
        staggeredGridView = (StaggeredGridView)findViewById(R.id.grid_view);

        setTitle("SmarTodo");

        adapter = new CutomAdapter(this, keys);
        staggeredGridView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
