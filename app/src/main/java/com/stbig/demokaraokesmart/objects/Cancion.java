package com.stbig.demokaraokesmart.objects;

/**
 * Created by root on 29/12/15.
 */
public class Cancion {

    int id;
    String name;
    String duration;
    int initial_second;
    String mp4_source;
    String video;
    String sound;
    String artist;

    public Cancion(int id, String name, String duration, int initial_second, String mp4_source, String video, String sound,
                   String artist){

        this.id=id;
        this.name=name;
        this.duration=duration;
        this.initial_second=initial_second;
        this.mp4_source=mp4_source;
        this.video=video;
        this.sound=sound;
        this.artist=artist;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getInitial_second() {
        return initial_second;
    }

    public void setInitial_second(int initial_second) {
        this.initial_second = initial_second;
    }

    public String getMp4_source() {
        return mp4_source;
    }

    public void setMp4_source(String mp4_source) {
        this.mp4_source = mp4_source;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
