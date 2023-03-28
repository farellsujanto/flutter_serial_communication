package id.farellsujanto.flutter_serial_communication;

import io.flutter.Log;
import io.flutter.plugin.common.EventChannel;

public class DeviceConnectionHandler implements EventChannel.StreamHandler {

    private EventChannel.EventSink eventSink;

    public void success(boolean data) {
        if (eventSink != null) {
            eventSink.success(data);
        }
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        Log.w("DeviceConnectionHandler", "Listening");
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        Log.w("DeviceConnectionHandler", "Cancel");
        eventSink = null;
    }
}
