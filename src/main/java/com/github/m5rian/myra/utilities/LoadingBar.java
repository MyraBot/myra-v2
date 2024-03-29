package com.github.m5rian.myra.utilities;

public class LoadingBar {
    private final String notLoaded;
    private final String loaded;
    private final float barLength;
    private final float maxValue;
    private boolean pointer;

    public LoadingBar(String notLoaded, String loaded, Long barLength, Long maxValue) {
        this.notLoaded = notLoaded;
        this.loaded = loaded;
        this.barLength = barLength;
        this.maxValue = maxValue;
    }

    public LoadingBar asPointer() {
        this.pointer = true;
        return this;
    }

    public String render(long currentValue) {
        String bar;
        if (pointer) {
            // Generate loading bar
            final float section = this.maxValue / this.barLength; // Split in parts
            final float atSection = currentValue / section; // Get part

            // Generate empty loading bar
            StringBuilder emptyBar = new StringBuilder();
            for (int i = 0; i < this.barLength; i++) {
                emptyBar.append("0"); // Add empty symbol
            }

            StringBuilder positionRaw = new StringBuilder(emptyBar);
            positionRaw.setCharAt(Math.round(atSection), '1');

            bar = positionRaw.toString().replace("0", this.notLoaded).replace("1", this.loaded);
        }
        // Loading bar
        else {
            // Generate loading bar
            final float section = this.barLength / this.maxValue; // Split in parts
            final float atSection = currentValue * section; // Get part

            // Generate empty loading bar
            StringBuilder emptyBar = new StringBuilder();
            for (int i = 0; i < this.barLength; i++) {
                emptyBar.append("0"); // Add empty symbol
            }

            StringBuilder loadingBar = new StringBuilder(emptyBar);
            for (int i = 0; i < atSection; i++) {
                loadingBar.setCharAt(Math.toIntExact(i), '1');
            }

            // Replace with right characters
            bar = loadingBar.toString().replace("0", this.notLoaded).replace("1", this.loaded);
        }

        return bar;
    }
}
