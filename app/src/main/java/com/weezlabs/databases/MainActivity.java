package com.weezlabs.databases;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ExpandableListView booksListView = (ExpandableListView) findViewById(R.id.books_listview);

        View emptyView = getLayoutInflater().inflate(R.layout.empty_list_view, null);
        addContentView(emptyView,
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT));
    }

    public void onAddBookClick(View view) {
        startAddBookActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_book) {
            startAddBookActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAddBookActivity() {
        Intent intent = new Intent(getApplicationContext(), AddBookActivity.class);
        startActivity(intent);
    }
}
