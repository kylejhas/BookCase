package edu.temple.bookcase;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

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

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedInterface, BookDetailsFragment.BookPlay {

    BookDetailsFragment bookDetailsFragment;
    boolean single, playing;
    Fragment container1Fragment, container2Fragment;
    ArrayList<Book> books;
    EditText userSearch;
    String query = "";
    Button searchButton, pause, stop;
    TextView playedBookTitle;
    SeekBar seekBar;
    private static int playedDur, playedProg;
    private static String playedTitle;
    private static boolean paused;

    boolean connected;
    Intent bookPlayIntent;
    AudiobookService.MediaControlBinder mediaControlBinder;
    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            connected = true;
            mediaControlBinder = (AudiobookService.MediaControlBinder) service;
            mediaControlBinder.setProgressHandler(seekBarHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
            mediaControlBinder = null;

        }
    };


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
                            bookObject.getInt("duration"),
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

    Handler seekBarHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if(msg.obj != null) {
                seekBar = findViewById(R.id.seekBar);
                if (seekBar != null) {
                    AudiobookService.BookProgress bookProgress = (AudiobookService.BookProgress) msg.obj;

                    if (bookProgress.getProgress() == -1) {
                        if (connected) {
                            mediaControlBinder.stop();
                            playedBookTitle.setText("");
                            seekBar.setProgress(0);
                            playedTitle = "";

                        }
                    }

                    if (seekBar != null && (bookProgress.getProgress() < MainActivity.playedDur)) {
                        seekBar.setProgress(bookProgress.getProgress());
                        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    if (connected) {
                                        mediaControlBinder.seekTo(progress);
                                        playedProg = progress;
                                    }
                                } else {
                                    playedProg = progress;
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });
                    } else if (seekBar != null && (bookProgress.getProgress() == playedDur)) {
                        if (connected) {
                            mediaControlBinder.stop();
                            playedTitle = "";
                            playedBookTitle.setText("");
                            seekBar.setProgress(0);
                        }
                    }
                }
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userSearch = findViewById(R.id.userSearch);
        searchButton = findViewById(R.id.searchButton);
        pause = findViewById(R.id.pauseButton);
        stop = findViewById(R.id.stopButton);
        seekBar = findViewById(R.id.seekBar);
        playedBookTitle = findViewById(R.id.playedBookTitle);

        playedBookTitle.setText(playedTitle);
        seekBar.setProgress(MainActivity.playedProg);
        seekBar.setMax(MainActivity.playedDur);


        container1Fragment = getSupportFragmentManager().findFragmentById(R.id.container_1);
        container2Fragment = getSupportFragmentManager().findFragmentById(R.id.container_2);
        single = (findViewById(R.id.container_2) == null);

        bookPlayIntent = new Intent(this, AudiobookService.class);
        bindService(bookPlayIntent, serviceConnection, Context.BIND_AUTO_CREATE);

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

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected && !paused){
                    mediaControlBinder.pause();
                    playing = false;
                    paused = true;
                }else if(connected){
                    mediaControlBinder.pause();
                    playing = true;
                    paused = false;
                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected){
                    mediaControlBinder.stop();
                    playedTitle = "";
                    playedBookTitle.setText("");
                    seekBar.setProgress(0);
                    playing = false;
                    paused = false;
                    stopService(bookPlayIntent);
                }
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
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
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

    @Override
    public void playBook(Book book) {
        startService(bookPlayIntent);
        playing = true;
        paused = false;
        seekBar.setMax(book.getDuration());
        playedDur = book.getDuration();
        playedTitle = book.getTitle();
        playedBookTitle.setText(playedTitle);
        mediaControlBinder.play(book.getId());


    }
}

