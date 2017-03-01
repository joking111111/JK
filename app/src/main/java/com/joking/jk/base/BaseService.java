package com.joking.jk.base;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Administrator on 2016/10/12.
 */

public class BaseService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
