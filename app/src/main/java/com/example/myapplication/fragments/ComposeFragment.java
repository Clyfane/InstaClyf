package fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.Post;
import com.example.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends Fragment {

    public static final String TAG = "ComposeFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    private static final int RESULT_OK = 0;
    public String photoFileName = "photo.jpg";
    File photoFile;
    Button SumbitB;
    Button ButtonTakePic;
    BottomNavigationView bottomNavigationView;
    EditText etDescription;
    ImageView ivPostImage;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.0
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);

    }
    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SumbitB = view.findViewById(R.id.btnSubmit);
        ButtonTakePic = view.findViewById(R.id.btnCaptureImage);
        etDescription = view.findViewById(R.id.etDescription);
        ivPostImage = view.findViewById(R.id.ivPostImage);

        ButtonTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchCamera();

            }

            private void LaunchCamera() {
            }
        });


        SumbitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();

                if (description.isEmpty()) {
                    Toast.makeText(getContext(), "can't be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if (photoFile == null || ivPostImage.getDrawable() == null) {
                    Toast.makeText(getContext(), "ERROR PICTURE", Toast.LENGTH_LONG).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser, photoFile);

            }

            private void LaunchCamera() {

                // create Intent to take a picture and return control to the calling application
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Create a File reference for future access
                photoFile = getPhotoFileUri(photoFileName);

                // wrap File object into a content provider
                // required for API >= 24
                // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    // Start
                    // the image capture intent to take photo
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }

            ActivityResultLauncher<Intent> cameraResultLauncher;


            public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                onActivityResult(requestCode, resultCode, data);
                if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                    if (resultCode == RESULT_OK) {
                        Bitmap Takenmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                        ivPostImage.setImageBitmap(Takenmap);
                    }
                }
            }

            // Returns the File for a photo stored on disk given the fileName
            public File getPhotoFileUri(String fileName) {
                // Get safe storage directory for photos
                // Use `getExternalFilesDir` on Context to access package-specific directories.
                // This way, we don't need to request external read/write runtime permissions.
                File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                }

                // Return the file target for the photo based on filename
                File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

                return file;
            }


            private void savePost(String description, ParseUser currentUser, File photoFile) {
                Post post = new Post();
                post.setDescription(description);
                post.setImage(new ParseFile(photoFile));
                post.setUser(currentUser);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "error while save", e);
                            Toast.makeText(getContext(), "error save", Toast.LENGTH_LONG).show();
                        }
                        Log.i(TAG, "Post save was successful");
                        etDescription.setText("");
                        ivPostImage.setImageResource(0);
                    }

                    ;

                });

            }
            private void queryPosts() {
                ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
                query.include(Post.KEY_USER);
                query.findInBackground(new FindCallback<Post>() {
                    @Override
                    public void done(List<Post> objects, ParseException e) {
                        if(e !=null) {
                            Log.e(TAG, "Issue with getting posts", e);
                            return;

                        }

                        Post[] posts = new Post[0];
                        for (Post post: posts) {
                            Log.i(TAG, "Post:" + post.getDescription() + post.getUser().getUsername());

                        }
                    }
                });
            }
        });}}