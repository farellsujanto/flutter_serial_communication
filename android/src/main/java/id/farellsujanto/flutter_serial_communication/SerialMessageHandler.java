package id.farellsujanto.flutter_serial_communication;

import io.flutter.Log;
import io.flutter.plugin.common.EventChannel;

class SerialMessageHandler implements EventChannel.StreamHandler {

    private EventChannel.EventSink eventSink;

    public void success(byte[] data) {
        if (eventSink != null) {
            eventSink.success(data);
        }
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        Log.w("SerialMessageHandler", "Listening");
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {
        Log.w("SerialMessageHandler", "Cancel");
        eventSink = null;
    }
}
