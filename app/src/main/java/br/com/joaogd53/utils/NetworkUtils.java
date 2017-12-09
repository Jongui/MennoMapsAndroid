package br.com.joaogd53.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Utils for network
 */

public class NetworkUtils {

    public static boolean networkIsConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        if (isWifiConn) return true;
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return networkInfo.isConnected();

    }
}
