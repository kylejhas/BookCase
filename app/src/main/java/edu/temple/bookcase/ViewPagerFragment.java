package edu.temple.bookcase;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;



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
            switch (position) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                    return BookDetailsFragment.newInstance(getResources().getStringArray(R.array.book_names)[position]);
                default:
                    return null;
            }



        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.book_names).length;
        }
    }

}