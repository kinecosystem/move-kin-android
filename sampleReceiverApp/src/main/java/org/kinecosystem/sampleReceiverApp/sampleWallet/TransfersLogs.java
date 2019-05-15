package org.kinecosystem.sampleReceiverApp.sampleWallet;

import android.content.SharedPreferences;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransfersLogs {
    private SharedPreferences sharedPreferences;
    private final String TRANSFERS_KEY = "TRANSFERS_KEY";

    public TransfersLogs(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public void clear() {
        sharedPreferences.edit().putString(TRANSFERS_KEY, "").apply();
    }

    public void addTransferData(String data) {
        String appendData = getTransfers() + "\n[" + getDateTime() + "] " + data + "\n";
        sharedPreferences.edit().putString(TRANSFERS_KEY, appendData).apply();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getTransfers() {
        return sharedPreferences.getString(TRANSFERS_KEY, "");
    }
}
