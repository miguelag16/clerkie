package clerkie.clerkie_android_coding_challenge;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Movie;
import android.graphics.PorterDuff;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.security.Permission;
import java.util.ArrayList;

public class ActivityChat extends AppCompatActivity implements View.OnClickListener {

    private boolean galleryIsLoading = false;
    private Cursor media_cursor = null;


    private class MediaViewBlob {

        private boolean isVideo;
        private Bitmap bitmap;
        private String uri;

        MediaViewBlob(Bitmap bitmap, String uri, boolean isVideo){
            this.bitmap = bitmap;
            this.uri = uri;
            this.isVideo = isVideo;
        }

        public boolean isVideo() {
            return this.isVideo;
        }

        public String getUri() {
            return uri;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }

    /*
        Loads at most 10 images at a time
     */
    private class LoadMediaToGalleryTask extends AsyncTask<Integer, MediaViewBlob, Void> {

        @Override
        protected void onPreExecute() {
            galleryIsLoading = true;
            mGalleryContents.removeAllViews();

            if (media_cursor != null) return;

            // Get relevant columns for use later.
            String[] projection = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
            };

            // Return only video and image metadata.
            String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

            Uri queryUri = MediaStore.Files.getContentUri("external");

            CursorLoader cursorLoader = new CursorLoader(
                    getApplicationContext(),
                    queryUri,
                    projection,
                    selection,
                    null, // Selection args (none).
                    MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
            );

            media_cursor = cursorLoader.loadInBackground();
        }

        // Receives a single integer that is the start index of the group of images to be shown
        protected Void doInBackground(Integer ...startIndex) {
            int start = startIndex[0];

            if (!media_cursor.moveToPosition(start)) return null;

            for (int i = 0; i < 10 && i < media_cursor.getCount(); i++){
                media_cursor.moveToNext();
                int thumbnail_dimension = (int) getResources().getDimension(R.dimen.thumbnail_dims);
                String uri = media_cursor.getString(media_cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                if (MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE ==
                        media_cursor.getInt(media_cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE))) {
                    Bitmap thumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(uri),
                            thumbnail_dimension, thumbnail_dimension);
                    publishProgress(new MediaViewBlob(thumbImage, uri, false));
                } else {
                    Bitmap thumbImage = ThumbnailUtils.createVideoThumbnail(uri, MediaStore.Images.Thumbnails.MINI_KIND);
                    publishProgress(new MediaViewBlob(thumbImage, uri, true));
                }
            }
            return null;
        }

        protected void onProgressUpdate(MediaViewBlob ...list) {
            for (final MediaViewBlob media: list){
                int thumbnail_padding = (int) getResources().getDimension(R.dimen.thumbnail_padding);
                int thumbnail_dimension = (int) getResources().getDimension(R.dimen.thumbnail_dims);

                ImageView image = new ImageView(getApplicationContext());
                image.setPadding(thumbnail_padding, thumbnail_padding, thumbnail_padding, thumbnail_padding);

                if (media.isVideo()) {
                    //image.setOnLongClickListener(); // use this to send

                    // Find a better way to display the shadow in
                    image.setColorFilter(Color.parseColor("#80FFFFFF"), PorterDuff.Mode.LIGHTEN);

                    image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(media.uri));
                            intent.setDataAndType(Uri.parse(media.uri), "video/mp4");
                            startActivity(intent);
                            return false;
                        }
                    });
                }

//                imageView.setId(i);
//                imageView.setImageURI(Uri.parse(thumbnail));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(thumbnail_dimension, thumbnail_dimension);
                image.setLayoutParams(layoutParams);
                image.setImageBitmap(media.getBitmap());
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                mGalleryContents.addView(image);
            }
        }

        protected void onPostExecute(Void param) {
//            showDialog("Downloaded " + result + " bytes");
            galleryIsLoading = false;
        }
    }

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private HorizontalScrollView mGalleryScrollView;
    private LinearLayout mGalleryContents;
    private ImageView mGalleryRightArrow;
    private ImageView mGalleryLeftArrow;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(mGalleryIndex, 0);
        editor.commit();

        // Gallery UI references
        mGalleryScrollView = findViewById(R.id.chat_gallery_scrollview);
        mGalleryContents = findViewById(R.id.chat_gallery_contents);
        mGalleryRightArrow = findViewById(R.id.chat_gallery_right);
        mGalleryLeftArrow = findViewById(R.id.chat_gallery_left);

        mGalleryRightArrow.setOnClickListener(this);
        mGalleryLeftArrow.setOnClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            //disable gallery feature
        }

        new LoadMediaToGalleryTask().execute(0);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    private final String mGalleryIndex = "gallery_index";
    @Override
    public void onClick(View view){
        int id = view.getId();


        if(id == R.id.chat_gallery_left){
            if (galleryIsLoading) return;
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int group = sharedPref.getInt(mGalleryIndex, 0);

            if (group == 0) return; // If in the first section of gallery, do nothing

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(mGalleryIndex, group-1);
            editor.commit();
            new LoadMediaToGalleryTask().execute(10*(group-1));

        } else if(id == R.id.chat_gallery_right){
            if (galleryIsLoading) return;

            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            int group = sharedPref.getInt(mGalleryIndex, 0);

            if (group == media_cursor.getCount()/10) return; // If in the last section of gallery, do nothing

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(mGalleryIndex, group+1);
            editor.commit();
            new LoadMediaToGalleryTask().execute(10*(group+1));

        }
    }

}
