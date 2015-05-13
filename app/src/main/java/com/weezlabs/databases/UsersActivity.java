package com.weezlabs.databases;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.weezlabs.databases.db.DatabaseHandler;
import com.weezlabs.databases.model.Book;
import com.weezlabs.databases.model.User;
import com.weezlabs.databases.task.BaseUserTask;
import com.weezlabs.databases.task.DeleteUserTask;
import com.weezlabs.databases.task.InsertOrUpdateUserTask;
import com.weezlabs.databases.task.OnTaskCompletedListener;

import java.util.ArrayList;
import java.util.List;


public class UsersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        OnTaskCompletedListener {
    private static final int USERS_LOADER = 18032015;
    private static final int AVAILABLE_BOOKS_LOADER = 15092010;
    private static final int INCORRECT_ID = -1;

    private UserCursorAdapter mUserCursorAdapter;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        mUserCursorAdapter = new UserCursorAdapter(this, null, true, R.layout.user_row);

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
                                mUser = user;
                                useLoader(AVAILABLE_BOOKS_LOADER);
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
            finish();
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
            builder.setTitle(R.string.label_dialog_edit_user_title);
        } else {
            builder.setTitle(R.string.label_dialog_add_user_title);
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
        builder.setNegativeButton(R.string.label_dialog_add_user_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void showChooseBookDialog(final Cursor cursor) {
        if (mUser == null) {
            return;
        }
        final List<Integer> bookIdList = new ArrayList<>();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(getString(R.string.label_dialog_give_book_title, mUser.getName()));

        CharSequence[] labels = getLabelsFromCursor(cursor);
        boolean[] checkedItems = new boolean[labels.length];

        builder.setMultiChoiceItems(labels, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                int bookId = INCORRECT_ID;
                if (cursor.moveToPosition(which)) {
                    bookId = cursor.getInt(cursor.getColumnIndex(Book.ID));
                }
                if (isChecked && bookId != INCORRECT_ID) {
                    bookIdList.add(bookId);
                } else if (bookIdList.contains(bookId) && bookId != INCORRECT_ID) {
                    bookIdList.remove(Integer.valueOf(bookId));
                }
            }
        });

        builder.setPositiveButton(R.string.label_dialog_give_book_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                GiveBooksTask task = new GiveBooksTask(getActivity().getApplicationContext(),
                        bookIdList, mUser.getId());
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                setResultToOk();
                dialog.dismiss();
            }
        });
        builder.setNeutralButton(R.string.label_dialog_give_book_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mUser = null;
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    private void setResultToOk() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
    }

    private CharSequence[] getLabelsFromCursor(Cursor cursor) {
        CharSequence[] labels = new CharSequence[cursor.getCount()];
        int position;
        if (cursor.moveToFirst()) {
            do {
                position = cursor.getPosition();
                labels[position] = cursor.getString(cursor.getColumnIndex(Book.TITLE));
            } while (cursor.moveToNext());
        }
        return labels;
    }


    private void executeUserTask(BaseUserTask userTask, User... params) {
        userTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    private void useLoader(int loaderId) {
        Loader<Cursor> loader = getLoaderManager().getLoader(loaderId);
        if (loader == null) {
            loader = getLoaderManager().initLoader(loaderId, null, this);
        } else {
            loader = getLoaderManager().restartLoader(loaderId, null, this);
        }
        loader.forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case USERS_LOADER:
                return new UserCursorLoader(this);
            case AVAILABLE_BOOKS_LOADER:
                return new AvailableBookCursorLoader(this);
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
            case AVAILABLE_BOOKS_LOADER:
                showChooseBookDialog(data);
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
            case AVAILABLE_BOOKS_LOADER:
                mUser = null;
                break;
            default:
                break;
        }
    }

    @Override
    public void onTaskCompleted() {
        setResultToOk();
        useLoader(USERS_LOADER);
    }

    private static class UserCursorLoader extends BaseCursorLoader {

        public UserCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return mDatabaseHandler.getUsersCursor();
        }
    }

    private static class AvailableBookCursorLoader extends BaseCursorLoader {

        public AvailableBookCursorLoader(Context context) {
            super(context);
        }

        @Override
        public Cursor loadInBackground() {
            return mDatabaseHandler.getAvailableBooks();
        }
    }

    private static class GiveBooksTask extends AsyncTask<Void, Void, Void> {
        DatabaseHandler mDatabaseHandler;
        List<Integer> mBookIdList;
        int mUserId;

        public GiveBooksTask(Context context, List<Integer> bookIdList, int userId) {
            mDatabaseHandler = new DatabaseHandler(context.getApplicationContext());
            mBookIdList = bookIdList;
            mUserId = userId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            mDatabaseHandler.giveBooksToUser(mBookIdList, mUserId);
            return null;
        }
    }
}
