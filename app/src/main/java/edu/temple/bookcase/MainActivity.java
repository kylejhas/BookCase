package edu.temple.bookcase;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedInterface {

    BookDetailsFragment bookDetailsFragment;
    boolean single;
    Fragment container1Fragment, container2Fragment;
    ArrayList<Book> books;
    EditText userSearch;
    String query = "";
    Button searchButton;

    Handler bookHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            try {
                JSONArray bookArray = new JSONArray(msg.obj.toString());
                books = new ArrayList<>();
                for (int i = 0; i < bookArray.length(); i++) {
                    JSONObject bookObject = bookArray.getJSONObject(i);
                    Book fetchedBook = new Book(
                            bookObject.getInt("book_id"),
                            bookObject.getString("title"),
                            bookObject.getString("author"),
                            bookObject.getString("duration"),
                            bookObject.getInt("published"),
                            bookObject.getString("cover_url"));

                    books.add(fetchedBook);
                }
                container1Fragment = getSupportFragmentManager().findFragmentById(R.id.container_1);
                container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
                single = (findViewById(R.id.container_2) == null);

                if (container1Fragment == null && single) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                            .commit();
                } else if (container1Fragment == null) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFragment.newInstance(books))
                            .commit();
                } else if (container1Fragment instanceof ViewPagerFragment && !query.equals("")) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                            .commit();
                } else if (container1Fragment instanceof BookListFragment && !query.equals("")) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFragment.newInstance(books))
                            .commit();
                } else if (query.equals("") && ((container1Fragment instanceof ViewPagerFragment))) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                            .commit();
                } else if (query.equals("") && ((container1Fragment instanceof BookListFragment))) {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFragment.newInstance(books))
                            .commit();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userSearch = findViewById(R.id.userSearch);
        searchButton = findViewById(R.id.searchButton);

        container1Fragment = getSupportFragmentManager().findFragmentById(R.id.container_1);
        container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
        single = (findViewById(R.id.container_2) == null);

        if (container1Fragment == null && container2Fragment == null) {
            fetchBooks(null);
        }

        if (container1Fragment instanceof BookListFragment && single) {

            if (container1Fragment != null && ((BookListFragment) container1Fragment).getBooks() != null) {
                books = ((BookListFragment) container1Fragment).getBooks();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_1, ViewPagerFragment.newInstance(books))
                        .commit();
            }
        }else if (container1Fragment instanceof ViewPagerFragment && !single) {

                if (container1Fragment != null && ((ViewPagerFragment) container1Fragment).getBooks() != null) {
                    books = ((ViewPagerFragment) container1Fragment).getBooks();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFragment.newInstance(books))
                            .commit();
                }
            } else if (container1Fragment instanceof BookListFragment) { // Tablet size

                if (container1Fragment != null && ((BookListFragment) container1Fragment).getBooks() != null) {
                    books = ((BookListFragment) container1Fragment).getBooks();
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.container_1, BookListFragment.newInstance(books))
                            .commit();
                }
            }

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    query = userSearch.getText().toString();
                    container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
                    if (container2Fragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .remove(container2Fragment)
                                .commit();
                    }
                    fetchBooks(query);
                }
            });
        }

    public void fetchBooks(final String toSearch) {
        if (toSearch == null || toSearch.length() == 0) {
            new Thread() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuilder builder = new StringBuilder();
                        String response;
                        while ((response = reader.readLine()) != null) {
                            builder.append(response);
                        }
                        // Need to use Handler
                        Message msg = Message.obtain();
                        msg.obj = builder.toString();
                        bookHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        } else {
            new Thread() {
                @Override
                public void run() {
                    URL url = null;
                    try {
                        url = new URL("https://kamorris.com/lab/audlib/booksearch.php?search=" + toSearch);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuilder builder = new StringBuilder();
                        String response;
                        while ((response = reader.readLine()) != null) {
                            builder.append(response);
                        }
                        Message msg = Message.obtain();
                        msg.obj = builder.toString();
                        bookHandler.sendMessage(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }



    @Override
        public void selected (int pos){
            Book book = books.get(pos);

            bookDetailsFragment = new BookDetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable(BookDetailsFragment.BOOK_KEY, book);
            bookDetailsFragment.setArguments(bundle);

            if (!single) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container_2, bookDetailsFragment)
                        .commit();
            }

        }
    }

