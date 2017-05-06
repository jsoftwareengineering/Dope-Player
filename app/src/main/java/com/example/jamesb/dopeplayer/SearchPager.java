package com.example.jamesb.dopeplayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.TracksPager;
import retrofit.client.Response;
import retrofit.http.Path;
import retrofit.http.QueryMap;

public class SearchPager {

    private final SpotifyService mSpotifyApi;
    private int mCurrentOffset;
    private int mPageSize;
    private String mCurrentQuery;

    public interface CompleteListener {
        void onComplete(List<Track> items);
        void onError(Throwable error);
    }

    public SearchPager(SpotifyService spotifyApi) {
        mSpotifyApi = spotifyApi;
    }

    public void getFirstPage(String query, int pageSize, CompleteListener listener) {
        mCurrentOffset = 0;
        mPageSize = pageSize;
        mCurrentQuery = query;
        getData(query, 0, pageSize, listener);
    }

    public void getNextPage(CompleteListener listener) {
        mCurrentOffset += mPageSize;
        getData(mCurrentQuery, mCurrentOffset, mPageSize, listener);
    }

    private void getData(String query, int offset, final int limit, final CompleteListener listener) {

        Map<String, Object> options = new HashMap<>();
        options.put(SpotifyService.OFFSET, offset);
        options.put(SpotifyService.LIMIT, limit);
//
//        mSpotifyApi.getPlaylist("nneeraj", "6hal73jyVVzSFblTIWOm1R");
//        mSpotifyApi.getAlbum("Beatles");
  //  void getPlaylistTracks(@Path("user_id") String var1, @Path("playlist_id") String var2, @QueryMap Map<String, Object> var3, Callback<Pager< PlaylistTrack >> var4);
//        void getPlaylistTracks(@Path("user_id") String var1, @Path("playlist_id") String var2, Callback<Pager<PlaylistTrack>> var3);

        mSpotifyApi.getPlaylistTracks("nneeraj2", "6hal73jyVVzSFblTIWOm1R", new SpotifyCallback<Pager<PlaylistTrack>>() {
            @Override
            public void failure(SpotifyError spotifyError) {
                listener.onError(spotifyError);

            }

            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
              List<Track> tracks = new ArrayList<Track>();
                for (PlaylistTrack playlistTrack : playlistTrackPager.items) {
                    tracks.add(playlistTrack.track);
                }
                //tracks.add(playlistTrackPager.items.get(0).track);
                listener.onComplete(tracks);
            }
        });
//        mSpotifyApi.searchTracks(query, options, new SpotifyCallback<TracksPager>() {
//            @Override
//            public void success(TracksPager tracksPager, Response response) {
//              //  listener.onComplete(tracksPager.tracks.items);
//            }
//
//            @Override
//            public void failure(SpotifyError error) {
//                listener.onError(error);
//            }
//        });
    }
}
