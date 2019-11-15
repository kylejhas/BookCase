package edu.temple.bookcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;


public class ViewPagerFragment extends Fragment {
    ViewPager pager;
    PagerAdapter pagerAdapter;
    ArrayList<Book> books;
    ArrayList<BookDetailsFragment> bookDetailsFragments;
    public final static String BOOK_KEYS = "books";


    public ViewPagerFragment() {
        // Required empty public constructor
    }

    public static ViewPagerFragment newInstance(ArrayList<Book> books){
        ViewPagerFragment fragment = new ViewPagerFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(BOOK_KEYS, books);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        bookDetailsFragments = new ArrayList<>();
        if(args != null){
            books = args.getParcelableArrayList(BOOK_KEYS);

            if(books != null){
                for(int i = 0; i < books.size(); i++){
                    bookDetailsFragments.add(BookDetailsFragment.newInstance(books.get(i)));
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        pager = view.findViewById(R.id.pager);
        pagerAdapter = new BookCasePagerAdapter(getChildFragmentManager(), bookDetailsFragments);
        pager.setAdapter(pagerAdapter);
        return view;
    }

    public ArrayList<Book> getBooks(){
        return this.books;
    }


    private class BookCasePagerAdapter extends FragmentStatePagerAdapter{

            ArrayList<BookDetailsFragment> bookDetailsFragments;

            BookCasePagerAdapter(FragmentManager fm, ArrayList<BookDetailsFragment> bookDetailsFragments){
                super(fm);
                this.bookDetailsFragments = bookDetailsFragments;
            }


        @Override
        public Fragment getItem(int position) {
                return bookDetailsFragments.get(position);
            }



        @Override
        public int getCount() {
            return bookDetailsFragments.size();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }

}