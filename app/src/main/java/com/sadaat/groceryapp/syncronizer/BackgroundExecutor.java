package com.sadaat.groceryapp.syncronizer;

import android.os.AsyncTask;

/**
 * This Class is being coded by AHSAN IQBAL on 10-02-2022 - 12:34 am
 * The Purpose of this class is to enhance the Performance of Threads and Background ASyncTask Handlers
 * */

public abstract class BackgroundExecutor extends AsyncTask<Object, Object, Object> {

    private Object[] objects;

    protected abstract Object backgroundTaskOnThread(Object... objects);
    protected abstract void beforeTask();
    protected abstract void afterExecutionOnMainThread(Object o);

    public BackgroundExecutor(Object... objects) {
        super();
        this.objects = objects;
    }

    @Override
    protected Object doInBackground(Object... objects) {
        return backgroundTaskOnThread(objects);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        beforeTask();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        afterExecutionOnMainThread(o);
    }

    public void startExecution(){
        super.execute(objects);
    }
}
