package edu.temple.bookcase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.res.Resources;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements BookListFragment.onBookSelectedInterface{

    BookDetailsFragment bookDetailsFragment;
    boolean single;
    ArrayList<String> book = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        book.addAll(Arrays.asList(res.getStringArray(R.array.book_names)));

        single = (findViewById(R.id.container_2) == null);

        Fragment containerFrag = getSupportFragmentManager().findFragmentById(R.id.container_1);

        if(containerFrag == null && single){
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null)
                    .add(R.id.container_1, new ViewPagerFragment())
                    .commit();
        } else if(containerFrag instanceof BookListFragment && single){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, new ViewPagerFragment())
                    .commit();
        } else{
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_1, BookListFragment.newInstance(book))
                    .commit();
        }


    }

    @Override
    public void selected(int pos) {
        String title = book.get(pos);

        bookDetailsFragment = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BookDetailsFragment.TITLE_KEY, title);
        bookDetailsFragment.setArguments(bundle);

        if(!single){
            getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null) // allows user to hit back arrow and go back to last BookDetailsFragment rather than going back to home screen and closing the app
                    .replace(R.id.container_2, bookDetailsFragment)
                    .commit();
        }

    }
}
