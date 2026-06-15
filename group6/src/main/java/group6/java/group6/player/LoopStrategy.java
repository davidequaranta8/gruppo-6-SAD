package group6.java.group6.player;

import java.util.ArrayList;
import java.util.List;

import group6.java.group6.models.Track;

public class LoopStrategy implements PlaybackStrategy {
   @Override
   public Track nextTrack(List<Track> queue, Track current) {
      return current;
   }

   @Override
   public Track prevTrack(List<Track> queue, Track current) {
      return current;
   }

   @Override
   public List<Track> buildQueue(List<Track> tracks, Track current) {
      return new ArrayList<>(tracks);
   }
}
