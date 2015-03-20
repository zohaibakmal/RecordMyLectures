package com.example.haider.recordmylectures;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    // Fragment managing the behaviors, interactions and presentation of the navigation drawer.
    private NavigationDrawerFragment mNavigationDrawerFragment;
    //Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;

    private MediaRecorder mRecorder = null;

    //File Directory
    File dir = Environment.getExternalStorageDirectory();
    File audioFile = null;
    String mFileName =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/test.mp4";
    MediaMetadataRetriever metaRetriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

//        metaRetriver = new MediaMetadataRetriever();
//        metaRetriver.setDataSource(mFileName);
//        Toast.makeText(getApplicationContext(), metaRetriver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM), Toast.LENGTH_LONG).show();

        // start recording audio
        try {
            startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
        final Button recordButton = (Button) findViewById(R.id.recordButton);

        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });


/*        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
           public void onClick(View v) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setOutputFile("testing");
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            }
        });
*/
    }

    public void startRecording() throws IOException {
        Log.d("test","We are recording the audio at:" + dir);

        try {
            audioFile = File.createTempFile("testaudio", ".3gp", dir);
        } catch (IOException e) {
            Log.e("ErrorCreatingFile", "external storage access error");
            return;
        }

        // Create Media Recorder and specify audio source, o/p format, encoder, & o/p format
        mRecorder = new MediaRecorder();

        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(audioFile.getAbsolutePath());

        mRecorder.prepare();
        mRecorder.start();
        long timestamp = System.currentTimeMillis();
        Log.d("test","Time-recording-started: "+ timestamp);

    }

    public void stopRecording() {
        if(mRecorder!=null) {
            mRecorder.stop();
            mRecorder.release();
        }
        addRecordingToMediaLibrary(); // add meta tags?
        mRecorder = null;

        Log.d("testing", "Are we here?");
    }
    protected void addRecordingToMediaLibrary() {
        ContentValues values = new ContentValues();
        //long current = System.currentTimeMillis();
        //values.put(MediaStore.Audio.Media.TITLE, mFileName);
        //values.put(MediaStore.Audio.Media.DATE_ADDED, (int) (current / 1000));
        //values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/3gpp");
        values.put(MediaStore.Audio.Media.ALBUM, Environment.getExternalStorageDirectory().getAbsolutePath());
        ContentResolver contentResolver = getContentResolver();

        Uri audioFileURI = Uri.fromFile(audioFile);
        //Uri newUri = audioFileURI;
        Uri newUri = MediaStore.Audio.Media.getContentUriForPath(audioFileURI.getPath());

        //Uri base = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
//        Toast.makeText(this,"Uri: " + base, Toast.LENGTH_LONG).show();
        Uri newUri2 = contentResolver.insert(newUri, values);
        Toast.makeText(this,"Uri: " + newUri, Toast.LENGTH_LONG).show();

        //getApplicationContext().getContentResolver().update(newUri, values, null, null);

//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, newUri));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopRecording();

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
