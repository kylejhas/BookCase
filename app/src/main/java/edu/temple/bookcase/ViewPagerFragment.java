package edu.temple.bookcase;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class ViewPagerFragment extends Fragment {
    ViewPager pager;
    PagerAdapter pagerAdapter;


    public ViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        pager = view.findViewById(R.id.pager);
        pagerAdapter = new BookCasePagerAdapter(getChildFragmentManager());
        pager.setAdapter(pagerAdapter);
        return view;
    }

    private class BookCasePagerAdapter extends FragmentStatePagerAdapter{
        BookCasePagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return BookDetailsFragment.newInstance(getResources().getStringArray(R.array.book_names)[position]);
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.book_names).length;
        }
    }

}