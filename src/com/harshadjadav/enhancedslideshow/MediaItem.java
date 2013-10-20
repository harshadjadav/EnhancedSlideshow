// MediaItem.java
// Represents an image or video in a slideshow.
package com.harshadjadav.enhancedslideshow;

import java.io.Serializable;

public class MediaItem implements Serializable
{
   private static final long serialVersionUID = 1L; // class's version #

   // constants for media types
   public static enum MediaType { IMAGE, VIDEO } 
   
   private MediaType type; // this MediaItem is an IMAGE or VIDEO
   private String path; // location of this MediaItem
   
   // constructor
   public MediaItem(MediaType mediaType, String location)
   {
      type = mediaType;
      path = location;
   } // end constructor
   
   // get the MediaType of this image or video
   public MediaType getType()
   {
      return type;
   } // end method MediaType
   
   // return the description of this image or video
   public String getPath()
   {
      return path;
   } // end method getDescription
} // end class MediaItem


