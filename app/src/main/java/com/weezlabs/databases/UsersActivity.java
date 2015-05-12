package com.weezlabs.databases;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.User;
import com.weezlabs.databases.task.BaseUserTask;
import com.weezlabs.databases.task.DeleteUserTask;
import com.weezlabs.databases.task.InsertOrUpdateUserTask;
import com.weezlabs.databases.task.OnTaskCompletedListener;


public class UsersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnTaskCompletedListener, PopupMenu.OnMenuItemClickListener {
    private static final int USERS_LOADER = 18032015;

    private UserCursorAdapter mUserCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mUserCursorAdapter = new UserCursorAdapter(this, null, true);

        initListView();

        getLoaderManager().initLoader(USERS_LOADER, null, this);
    }

    private void initListView() {
        ListView usersListView = (ListView) findViewById(R.id.users_listview);
        usersListView.setEmptyView(findViewById(R.id.empty_user_view));
        usersListView.setAdapter(mUserCursorAdapter);

        usersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final User user = mUserCursorAdapter.getUser(position);
                PopupMenu popupMenu = new PopupMenu(getActivity(), view, GravityCompat.END);
                popupMenu.getMenuInflater().inflate(R.menu.menu_user_popup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        switch (id) {
                            case R.id.action_give_book:
                                // TODO: implement give book to user!
                                Toast.makeText(getApplicationContext(), "TODO: implement give book", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.action_edit_user:
                                showAddUserDialog(user);
                                break;
                            case R.id.action_delete_user:
                                executeUserTask(new DeleteUserTask(getActivity(), getTaskCompletedListener()), user);
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    public void onAddUserClick(View view) {
        showAddUserDialog(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_users, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_add_user) {
            showAddUserDialog(null);
            return true;
        } else if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Need for AppCompat PopupMenu
    private Activity getActivity() {
        return this;
    }

    private OnTaskCompletedListener getTaskCompletedListener() {
        return this;
    }

    private void showAddUserDialog(final User user) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);

        View inflatedView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_text, null);
        final EditText materialEdit = (EditText) inflatedView.findViewById(R.id.material_edit_text);

        final boolean isNewUser = user == null;
        if (!isNewUser) {
            materialEdit.setText(user.getName());
            materialEdit.setSelection(user.getName().length());
            builder.setTitle(getString(R.string.label_dialog_edit_user_title));
        } else {
            builder.setTitle(getString(R.string.label_dialog_add_user_title));
        }

        builder.setView(inflatedView);
        builder.setPositiveButton(isNewUser ? getString(R.string.label_dialog_add_user_ok_button) :
                getString(R.string.label_dialog_edit_user_ok_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isNewUser) {
                    User user = new User();
                    user.setName(materialEdit.getText().toString());
                    executeUserTask(new InsertOrUpdateUserTask(getApplicationContext(),
                            getTaskCompletedListener(), false), user);
                } else {
                    user.setName(materialEdit.getText().toString());
                    executeUserTask(new InsertOrUpdateUserTask(getApplicationContext(),
                            getTaskCompletedListener(), true), user);
                }

                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getString(R.string.label_dialog_add_user_cancel_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void executeUserTask(BaseUserTask userTask, User... params) {
        userTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void loadUserCursor() {
        Loader<Cursor> loader = getLoaderManager().getLoader(USERS_LOADER);
        if (loader == null) {
            loader = getLoaderManager().initLoader(USERS_LOADER, null, this);
        } else {
            loader = getLoaderManager().restartLoader(USERS_LOADER, null, this);
        }
        loader.forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case USERS_LOADER:
                return new UserCursorLoader(this);
            default:
                // An invalid id was passed in
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case USERS_LOADER:
                mUserCursorAdapter.changeCursor(data);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case USERS_LOADER:
                mUserCursorAdapter.changeCursor(null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTaskCompleted() {
        loadUserCursor();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    public static class UserCursorLoader extends CursorLoader {
        DatabaseHandler mDatabaseHandler;

        public UserCursorLoader(Context context) {
            super(context);
            mDatabaseHandler = new DatabaseHandler(context.getApplicationContext());
        }

        @Override
        public Cursor loadInBackground() {
            return mDatabaseHandler.getUsersCursor();
        }
    }
}
