package com.shengmingji.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void bindServer(View view) {
        if (ssoAuth == null) {
            Intent mIntent = new Intent();
            mIntent.setAction("com.shengmingji.aidl_server");
            mIntent.setAction("com.shengmingji.aidl_server.SsoAuthService");
            Intent eintent = new Intent(getExplicitIntent(this, mIntent));
            bindService(eintent, connection, BIND_AUTO_CREATE);
        } else {
            try {
                ssoAuth.ssoAuth("hahaha", "123456");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private SsoAuth ssoAuth;
    ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ssoAuth = SsoAuth.Stub.asInterface(service);
            Log.i("TAG", "onService " + name.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("TAG", "onServiceDisconnected: " + name.toString());
            ssoAuth = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    public Intent getExplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }
        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);
        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);
        // Set the component to be explicit
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

}
