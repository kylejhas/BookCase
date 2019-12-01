package edu.temple.bookcase;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class BookDetailsFragment extends Fragment {
    public static final String BOOK_KEY = "book";
    ConstraintLayout bookDetailsView;
    ImageView cover;
    TextView title, author, published, bookLength;
    Book book;
    private BookPlay fragmentParent;
    Button playButton;


    public BookDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param book Book
     * @return A new instance of fragment BookDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookDetailsFragment newInstance(Book book) {
        BookDetailsFragment fragment = new BookDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(BOOK_KEY, book);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            book = getArguments().getParcelable(BOOK_KEY);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        bookDetailsView = (ConstraintLayout) inflater.inflate(R.layout.fragment_book_details, container, false);
        
        return bookDetailsView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        cover = Objects.requireNonNull(getView()).findViewById(R.id.cover);
        title = Objects.requireNonNull(getView()).findViewById(R.id.title);
        author = getView().findViewById(R.id.author);
        published = getView().findViewById(R.id.published);
        bookLength = getView().findViewById(R.id.bookLength);

        if(book != null){
            displayBook(book);
            playButton = getView().findViewById(R.id.playButton);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentParent.playBook(book);
                }
            });
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof BookDetailsFragment.BookPlay){
            fragmentParent = (BookDetailsFragment.BookPlay) context;
        }else{
            throw new RuntimeException(context.toString() +
                    " must implement BookPlay interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentParent = null;
    }


    private void displayBook(Book book) {
        Picasso.get().load(book.getCoverUrl()).into(cover);
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        author.setGravity(Gravity.CENTER);
        published.setText(String.format(getResources().getString(R.string.published), book.getPublished()));
        bookLength.setText(String.format(getResources().getString(R.string.bookLength), book.getDuration()));

    }

    public interface BookPlay{
        void playBook(Book book);
    }
}
