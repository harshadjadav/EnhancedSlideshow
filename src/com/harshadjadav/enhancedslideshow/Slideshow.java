// Slideshow.java
// Main Activity for the Slideshow class.
package com.harshadjadav.enhancedslideshow;

import java.io.File;                 
import java.io.FileInputStream;      
import java.io.FileOutputStream;     
import java.io.ObjectInputStream;    
import java.io.ObjectOutputStream;   
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class Slideshow extends ListActivity
{
   private static final String TAG = "SLIDESHOW"; // error logging tag
   
   // used when adding slideshow name as an extra to an Intent
   public static final String NAME_EXTRA = "NAME";  
   
   static List<SlideshowInfo> slideshowList; // List of slideshows
   private ListView slideshowListView; // this ListActivity's ListView
   private SlideshowAdapter slideshowAdapter; // adapter for the ListView
   private File slideshowFile; // File representing location of slideshows
   
   // called when the activity is first created
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      slideshowListView = getListView(); // get the built-in ListView
      
      // get File location and start task to load slideshows
      slideshowFile = new File(                             
         getExternalFilesDir(null).getAbsolutePath() +      
            "/EnhancedSlideshowData.ser");                  
      new LoadSlideshowsTask().execute((Object[]) null);    
      
      // create a new AlertDialog Builder
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setTitle(R.string.welcome_message_title); 
      builder.setMessage(R.string.welcome_message);
      builder.setPositiveButton(R.string.button_ok, null);
      builder.show();
   } // end method onCreate

   // Class to load the List<SlideshowInfo> object from the device
   private class LoadSlideshowsTask extends AsyncTask<Object,Object,Object>
   {
      // load from non-GUI thread
      @Override
      protected Object doInBackground(Object... arg0)
      {
         // if the file exists, read the file; otherwise, create it
         if (slideshowFile.exists())
         {
            try 
            {
               ObjectInputStream input = new ObjectInputStream(         
                  new FileInputStream(slideshowFile));                  
               slideshowList = (List<SlideshowInfo>) input.readObject();
            } // end try
            catch (final Exception e) 
            {
               runOnUiThread(
                  new Runnable()
                  {
                     public void run()
                     {
                        // display error reading message
                        Toast message = Toast.makeText(Slideshow.this, 
                           R.string.message_error_reading, 
                           Toast.LENGTH_LONG);
                        message.setGravity(Gravity.CENTER, 
                           message.getXOffset() / 2, 
                           message.getYOffset() / 2);
                        message.show(); // display the Toast
                        Log.v(TAG, e.toString());
                     } // end method run
                  } // end Runnable
               ); // end call to runOnUiThread
            } // end catch
         } // end if
         
         if (slideshowList == null) // if null, create it
            slideshowList = new ArrayList<SlideshowInfo>();         
      
         return (Object) null; // method must satisfy the return type
      } // end method doInBackground

      // create the ListView's adapter on the GUI thread
      @Override
      protected void onPostExecute(Object result)
      {
         super.onPostExecute(result);

         // create and set the ListView's adapter
         slideshowAdapter = 
            new SlideshowAdapter(Slideshow.this, slideshowList);
         slideshowListView.setAdapter(slideshowAdapter);
      } // end method onPostEecute
   } // end class LoadSlideshowsTask
   
   // Class to save the List<SlideshowInfo> object to the device
   private class SaveSlideshowsTask extends AsyncTask<Object,Object,Object>
   {
      // save from non-GUI thread
      @Override
      protected Object doInBackground(Object... arg0)
      {
         try  
         {
            // if the file doesn't exist, create it
            if (!slideshowFile.exists())     
               slideshowFile.createNewFile();
            
            // create ObjectOutputStream, then write slideshowList to it
            ObjectOutputStream output = new ObjectOutputStream(         
               new FileOutputStream(slideshowFile));                    
            output.writeObject(slideshowList);                          
            output.close();                          
         } // end try
         catch (final Exception e) 
         {
            runOnUiThread(
               new Runnable()
               {
                  public void run()
                  {
                     // display error reading message
                     Toast message = Toast.makeText(Slideshow.this, 
                        R.string.message_error_writing, Toast.LENGTH_LONG);
                     message.setGravity(Gravity.CENTER, 
                        message.getXOffset() / 2, 
                        message.getYOffset() / 2);
                     message.show(); // display the Toast
                     Log.v(TAG, e.toString());
                  } // end method run
               } // end Runnable
            ); // end call to runOnUiThread
         } // end catch
      
         return (Object) null; // method must satisfy the return type
      } // end method doInBackground
   } // end class SaveSlideshowsTask
      
   // create the Activity's menu from a menu resource XML file
   @Override
   public boolean onCreateOptionsMenu(Menu menu) 
   {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.slideshow_menu, menu);
      return true;
   } // end method onCreateOptionsMenu
   
   // SlideshowEditor request code passed to startActivityForResult
   private static final int EDIT_ID = 0; 

   // handle choice from options menu
   @Override
   public boolean onOptionsItemSelected(MenuItem item) 
   {
      // get a reference to the LayoutInflater service
      LayoutInflater inflater = (LayoutInflater) getSystemService(
         Context.LAYOUT_INFLATER_SERVICE);

      // inflate slideshow_name_edittext.xml to create an EditText
      View view = inflater.inflate(R.layout.slideshow_name_edittext, null);
      final EditText nameEditText = 
         (EditText) view.findViewById(R.id.nameEditText);
         
      // create an input dialog to get slideshow name from user
      AlertDialog.Builder inputDialog = new AlertDialog.Builder(this);
      inputDialog.setView(view); // set the dialog's custom View
      inputDialog.setTitle(R.string.dialog_set_name_title); 
       
      inputDialog.setPositiveButton(R.string.button_set_slideshow_name, 
         new DialogInterface.OnClickListener()
         { 
            public void onClick(DialogInterface dialog, int whichButton) 
            {
               // create a SlideshowInfo for a new slideshow
               String name = nameEditText.getText().toString().trim();
               
               if (name.length() != 0)
               {
                  slideshowList.add(new SlideshowInfo(name));
                  
                  // create Intent to launch the SlideshowEditor Activity,
                  // add slideshow name as an extra and start the Activity
                  Intent editSlideshowIntent =
                     new Intent(Slideshow.this, SlideshowEditor.class);
                  editSlideshowIntent.putExtra(NAME_EXTRA, name);
                  startActivityForResult(editSlideshowIntent, EDIT_ID);
               } // end if
               else
               {
                  // display message that slideshow must have a name
                  Toast message = Toast.makeText(Slideshow.this, 
                     R.string.message_name, Toast.LENGTH_SHORT);
                  message.setGravity(Gravity.CENTER, 
                     message.getXOffset() / 2, message.getYOffset() / 2);
                  message.show(); // display the Toast
               } // end else
            } // end method onClick 
         } // end anonymous inner class
      ); // end call to setPositiveButton
      
      inputDialog.setNegativeButton(R.string.button_cancel, null);
      inputDialog.show();
      
      return super.onOptionsItemSelected(item); // call super's method
   } // end method onOptionsItemSelected

   // refresh ListView after slideshow editing is complete 
   @Override
   protected void onActivityResult(int requestCode, int resultCode,
      Intent data)
   {
      super.onActivityResult(requestCode, resultCode, data);
      new SaveSlideshowsTask().execute((Object[]) null); // save slideshows
      slideshowAdapter.notifyDataSetChanged(); // refresh the adapter
   } // end method onActivityResult
    
   // Class for implementing the "ViewHolder pattern"
   // for better ListView performance
   private static class ViewHolder
   {
      TextView nameTextView; // refers to ListView item's TextView
      ImageView imageView; // refers to ListView item's ImageView
      Button playButton; // refers to ListView item's Play Button
      Button editButton; // refers to ListView item's Edit Button
      Button deleteButton; // refers to ListView item's Delete Button
   } // end class ViewHolder
   
   // ArrayAdapter subclass that displays a slideshow's name, first image
   // and "Play", "Edit" and "Delete" Buttons
   private class SlideshowAdapter extends ArrayAdapter<SlideshowInfo>
   {
      private List<SlideshowInfo> items;
      private LayoutInflater inflater;

      // public constructor for SlideshowAdapter
      public SlideshowAdapter(Context context, List<SlideshowInfo> items)
      {
         // call super constructor
         super(context, -1, items);
         this.items = items;
         inflater = (LayoutInflater) 
            getSystemService(Context.LAYOUT_INFLATER_SERVICE);
      } // end SlideshowAdapter constructor

      // returns the View to display at the given position
      @Override
      public View getView(int position, View convertView, 
         ViewGroup parent)
      {
         ViewHolder viewHolder; // holds references to current item's GUI

         // if convertView is null, inflate GUI and create ViewHolder;
         // otherwise, get existing ViewHolder
         if (convertView == null) 
         {
            convertView = 
               inflater.inflate(R.layout.slideshow_list_item, null);

            // set up ViewHolder for this ListView item
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) 
               convertView.findViewById(R.id.nameTextView);
            viewHolder.imageView = (ImageView) 
               convertView.findViewById(R.id.slideshowImageView);
            viewHolder.playButton = 
               (Button) convertView.findViewById(R.id.playButton);
            viewHolder.editButton = 
               (Button) convertView.findViewById(R.id.editButton);
            viewHolder.deleteButton = 
               (Button) convertView.findViewById(R.id.deleteButton);
            convertView.setTag(viewHolder); // store as View's tag
         } // end if
         else // get the ViewHolder from the convertView's tag
            viewHolder = (ViewHolder) convertView.getTag();

         // get the slideshow the display its name in nameTextView
         SlideshowInfo slideshowInfo = items.get(position);
         viewHolder.nameTextView.setText(slideshowInfo.getName());

         // if there is at least one image in this slideshow
         if (slideshowInfo.size() > 0)
         {
            // create a bitmap using the slideshow's first image or video
            MediaItem firstItem = slideshowInfo.getMediaItemAt(0);
            new LoadThumbnailTask().execute(viewHolder.imageView, 
               firstItem.getType(), Uri.parse(firstItem.getPath()));
         } // end if

         // set tag and OnClickListener for the "Play" Button
         viewHolder.playButton.setTag(slideshowInfo);
         viewHolder.playButton.setOnClickListener(playButtonListener);

         // set tag and OnClickListener for the "Edit" Button
         viewHolder.editButton.setTag(slideshowInfo);
         viewHolder.editButton.setOnClickListener(editButtonListener);

         // set and tag OnClickListener for the "Delete" Button
         viewHolder.deleteButton.setTag(slideshowInfo);
         viewHolder.deleteButton.setOnClickListener(deleteButtonListener);
         
         return convertView; // return the View for this position
      } // end getView
   } // end class SlideshowAdapter   

   // task to load thumbnails in a separate thread
   private class LoadThumbnailTask extends AsyncTask<Object, Void, Bitmap>
   {
      ImageView imageView; // displays the thumbnail
      
      // load thumbnail: ImageView, MediaType and Uri as args
      @Override
      protected Bitmap doInBackground(Object... params)
      {
         imageView = (ImageView) params[0];
         
         return Slideshow.getThumbnail((MediaItem.MediaType)params[1], 
            (Uri) params[2], getContentResolver(), 
            new BitmapFactory.Options());
      } // end method doInBackground

      // set thumbnail on ListView
      @Override
      protected void onPostExecute(Bitmap result)
      {
         super.onPostExecute(result);
         imageView.setImageBitmap(result);
      } // end method onPostExecute  
   } // end class LoadThumbnailTask 
   
   // respond to events generated by the "Play" Button
   OnClickListener playButtonListener = new OnClickListener()
   {
      @Override
      public void onClick(View v)
      {
         // create an intent to launch the SlideshowPlayer Activity
         Intent playSlideshow =
            new Intent(Slideshow.this, SlideshowPlayer.class);
         playSlideshow.putExtra(
            NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
         startActivity(playSlideshow); // launch SlideshowPlayer Activity
      } // end method onClick
   }; // end playButtonListener

   // respond to events generated by the "Edit" Button
   private OnClickListener editButtonListener = new OnClickListener()
   {
      @Override
      public void onClick(View v)
      {
         // create an intent to launch the SlideshowEditor Activity
         Intent editSlideshow =
            new Intent(Slideshow.this, SlideshowEditor.class);
         editSlideshow.putExtra(
            NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
         startActivityForResult(editSlideshow, 0);
      } // end method onClick
   }; // end playButtonListener

   // respond to events generated by the "Delete" Button
   private OnClickListener deleteButtonListener = new OnClickListener()
   {
      @Override
      public void onClick(final View v)
      {
         // create a new AlertDialog Builder
         AlertDialog.Builder builder = 
            new AlertDialog.Builder(Slideshow.this);
         builder.setTitle(R.string.dialog_confirm_delete); 
         builder.setMessage(R.string.dialog_confirm_delete_message);
         builder.setPositiveButton(R.string.button_ok, 
            new DialogInterface.OnClickListener()
            {
               @Override
               public void onClick(DialogInterface dialog, int which)
               {
                  Slideshow.slideshowList.remove(
                     (SlideshowInfo) v.getTag());
                  new SaveSlideshowsTask().execute((Void) null); // save
                  slideshowAdapter.notifyDataSetChanged(); // refresh 
               } // end method onClick
            } // end anonymous inner class
         ); // end call to setPositiveButton 
         builder.setNegativeButton(R.string.button_cancel, null);
         builder.show();
      } // end method onClick
   }; // end playButtonListener
   
   // utility method to locate SlideshowInfo object by slideshow name
   public static SlideshowInfo getSlideshowInfo(String name)
   {
      // locate and return slideshow with specified name
      for (SlideshowInfo slideshowInfo : slideshowList)
         if (slideshowInfo.getName().equals(name))
            return slideshowInfo;
      
      return null; // no matching object
   } // end method getSlideshowInfo
   
   // utility method to get a thumbnail image Bitmap
   public static Bitmap getThumbnail(MediaItem.MediaType type, Uri uri,
      ContentResolver cr, BitmapFactory.Options options)
   {
      Bitmap bitmap = null;
      int id = Integer.parseInt(uri.getLastPathSegment());

      if (type == MediaItem.MediaType.IMAGE) // if it is an image
         bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, 
            MediaStore.Images.Thumbnails.MICRO_KIND, options);         
      else if (type == MediaItem.MediaType.VIDEO) // if it is a video
         bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr, id,
            MediaStore.Video.Thumbnails.MICRO_KIND, options);     
      
      return bitmap;
   } // end method getThumbnail
} // end class Slideshow

