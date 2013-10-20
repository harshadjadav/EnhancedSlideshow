// SlideshowInfo.java
// Stores the data for a single slideshow.
package com.harshadjadav.enhancedslideshow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SlideshowInfo implements Serializable
{
   private static final long serialVersionUID = 1L; // class's version #
   private String name; // name of this slideshow
   private List<MediaItem> mediaItemList; // this slideshow's images
   private String musicPath; // location of music to play
   
   // constructor 
   public SlideshowInfo(String slideshowName)
   {
      name = slideshowName; // set the slideshow name
      mediaItemList = new ArrayList<MediaItem>(); 
      musicPath = null; // currently there is no music for the slideshow
   } // end SlideshowInfo constructor

   // return this slideshow's name
   public String getName()
   {
      return name;
   } // end method getName

   // return List of MediaItems pointing to the slideshow's images
   public List<MediaItem> getMediaItemList()
   {
      return mediaItemList;
   } // end method getMediaItemList

   // add a new MediaItem
   public void addMediaItem(MediaItem.MediaType type, String path)
   {
      mediaItemList.add(new MediaItem(type, path));
   } // end method addMediaItem
   
   // return MediaItem at position index
   public MediaItem getMediaItemAt(int index)
   {
      if (index >= 0 && index < mediaItemList.size())
         return mediaItemList.get(index);
      else
         return null;
   } // end method getMediaItemAt

   // return this slideshow's music
   public String getMusicPath()
   {
      return musicPath;
   } // end method getSlideshowMusic

   // set this slideshow's music
   public void setMusicPath(String path)
   {
      musicPath = path;
   } // end method setMusicUri
   
   // return number of images/videos in the slideshow
   public int size()
   {
      return mediaItemList.size();
   } // end method size
} // end class SlideshowInfo

