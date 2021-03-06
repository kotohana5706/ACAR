package kr.edcan.acar.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by JunseokOh on 2016. 9. 3..
 */
public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.e("asdf", message.getFrom());
        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String title = data.get("data1");
        String msg = data.get("data2");
    }
}
