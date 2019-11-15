package edu.temple.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookListFragment.onBookSelectedInterface} interface
 * to handle interaction events.
 * Use the {@link BookListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookListFragment extends Fragment {

    ArrayList<String> bookNames = new ArrayList<>();
    ArrayList<Book> books;
    private static final String BOOK_KEY = "books";


    private onBookSelectedInterface fragmentParent;

    public BookListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param books     Parameter 1.
     * @return A new instance of fragment BookListFragment.
     */
    public static BookListFragment newInstance(ArrayList<Book> books) {
        BookListFragment fragment = new BookListFragment();
        Bundle args = new Bundle();

        args.putParcelableArrayList(BOOK_KEY, books);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            books = args.getParcelableArrayList(BOOK_KEY);
        }

        if(books != null){
            for(int i = 0; i < books.size(); i++){
                bookNames.add(books.get(i).getTitle());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ListView listview = (ListView) inflater.inflate(R.layout.fragment_book_list, container, false);

        listview.setAdapter(new ArrayAdapter<>((Context) fragmentParent, android.R.layout.simple_list_item_1, bookNames ));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                fragmentParent.selected(position);
            }
        });

        return listview;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onBookSelectedInterface) {
            fragmentParent = (onBookSelectedInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentParent = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onBookSelectedInterface {
        void selected(int pos);
    }

    public ArrayList<Book> getBooks(){
        return this.books;
    }
}
