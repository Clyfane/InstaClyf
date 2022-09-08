package fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.Post;
import com.example.myapplication.PostAdapter;
import com.example.myapplication.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;



public class PostsFragment extends Fragment {
    private RecyclerView rvPosts;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;
    public static final String TAG = "PostsFragment";
    private List<Post> allPosts;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return onCreateView(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        adapter = new PostAdapter(getContext()), allPosts;
    }


    // TODO: Rename and change types and number of parameters
        rvPosts.setAdapter(adapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryPosts();

    protected void queryPosts() {
        ParseQuery<Post> query =e ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;

                }
                Post[] posts = new Post[0];

                for (Post post : posts) {
                    Log.i(TAG, "Post:" + post.getDescription() + post.getUser().getUsername());

                }
                allPosts.notifyDataSetChanged();

            }});}}
