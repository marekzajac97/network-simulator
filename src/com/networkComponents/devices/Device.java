package com.networkComponents.devices;

public abstract class Device {
    protected String hostname;
    protected boolean running = false;

    public String getHostname() {
        return hostname;
    }
    public boolean isRunning() {
        return running;
    }
    public void shutdown(){
        running = false;
    }

    protected Device(String hostname) {
        this.hostname = hostname;
    }
}
