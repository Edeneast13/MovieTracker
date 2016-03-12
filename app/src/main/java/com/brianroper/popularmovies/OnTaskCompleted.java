package com.brianroper.popularmovies;

import android.graphics.Bitmap;

public interface OnTaskCompleted {

    void processFinish(String output);
    void processFinish(Bitmap output);
}
