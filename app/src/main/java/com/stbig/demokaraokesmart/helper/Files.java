package com.stbig.demokaraokesmart.helper;

import android.os.Environment;

import java.io.File;

public class Files {

    private String FILE_SERVICES;
    private static final String SERVICES_FILE_FOLDER = "KaraokeSmartPistas";
    private File file;

    public Files() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        file = new File(filepath, SERVICES_FILE_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public void setNameFiles(String fileServices) {
        FILE_SERVICES = fileServices;
    }


    public String getService() {
        return FILE_SERVICES;
    }


    public String getPathService() {
        return (file.getAbsolutePath() + "/" + FILE_SERVICES);
    }

}
