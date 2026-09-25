package com.moviebooking.model;

public final class Theatre {

    private final long id;
    private final String name;
    private final String location;
    private final int screenCount;

    public Theatre(long id, String name, String location, int screenCount) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.screenCount = screenCount;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public int getScreenCount() {
        return screenCount;
    }
}
