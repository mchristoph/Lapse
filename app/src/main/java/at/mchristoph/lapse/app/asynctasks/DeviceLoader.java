package at.mchristoph.lapse.app.asynctasks;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;
import at.mchristoph.lapse.app.models.ServerDevice;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xris on 08.03.2016.
 */
public class DeviceLoader extends AsyncTaskLoader<List<ServerDevice>> {
    private static final String LOG_TAG = DeviceLoader.class.getSimpleName();

    private static final int SSDP_RECEIVE_TIMEOUT = 10000; // msec
    private static final int PACKET_BUFFER_SIZE = 1024;
    private static final int SSDP_PORT = 1900;
    private static final int SSDP_MX = 1;
    private static final String SSDP_ADDR = "239.255.255.250";
    private static final String SSDP_ST = "urn:schemas-sony-com:service:ScalarWebAPI:1";

    private boolean mSearching;
    private List<ServerDevice> mFoundDevices;

    public DeviceLoader(Context context) {
        super(context);
        mSearching = false;
        mFoundDevices = new ArrayList<>();
    }

    @Override
    public List<ServerDevice> loadInBackground() {
        mFoundDevices.clear();

        final String ssdpRequest =
                "M-SEARCH * HTTP/1.1\r\n" + String.format("HOST: %s:%d\r\n", SSDP_ADDR, SSDP_PORT)
                        + String.format("MAN: \"ssdp:discover\"\r\n")
                        + String.format("MX: %d\r\n", SSDP_MX)
                        + String.format("ST: %s\r\n", SSDP_ST) + "\r\n";

        final byte[] sendData = ssdpRequest.getBytes();

        // Send Datagram packets
        DatagramSocket socket = null;
        DatagramPacket receivePacket = null;
        DatagramPacket packet = null;
        try {
            socket = new DatagramSocket();
            InetSocketAddress iAddress = new InetSocketAddress(SSDP_ADDR, SSDP_PORT);
            packet = new DatagramPacket(sendData, sendData.length, iAddress);
            // send 3 times
            Log.i(LOG_TAG, "search() Send Datagram packet 3 times.");
            socket.send(packet);
            Thread.sleep(100);
            socket.send(packet);
            Thread.sleep(100);
            socket.send(packet);
        } catch (SocketException e) {
            Log.e(LOG_TAG, "search() DatagramSocket error:", e);
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            return null;
        } catch (IOException e) {
            Log.e(LOG_TAG, "search() IOException :", e);
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            return null;
        } catch (InterruptedException e) {
            // do nothing.
            Log.d(LOG_TAG, "search() InterruptedException :", e);
            return null;
        }

        // Receive reply packets
        mSearching = true;
        long startTime = System.currentTimeMillis();
        List<String> foundDevices = new ArrayList<String>();
        byte[] array = new byte[PACKET_BUFFER_SIZE];
        ServerDevice device = null;
        try {
            while (mSearching) {
                receivePacket = new DatagramPacket(array, array.length);
                socket.setSoTimeout(SSDP_RECEIVE_TIMEOUT);
                socket.receive(receivePacket);
                String ssdpReplyMessage = new String(receivePacket.getData(), 0, //
                        receivePacket.getLength(), "UTF-8");
                String ddUsn = findParameterValue(ssdpReplyMessage, "USN");

                /*
                 * There is possibility to receive multiple packets from
                 * a individual server.
                 */
                if (!foundDevices.contains(ddUsn)) {
                    String ddLocation = findParameterValue(ssdpReplyMessage, "LOCATION");
                    foundDevices.add(ddUsn);

                    // Fetch Device Description XML and parse it.
                    device = ServerDevice.fetch(ddLocation);
                    // Note that it's a irresponsible rule
                    // for the sample application.
                    if (device != null && device.hasApiService("camera")) {
                        mFoundDevices.add(device);
                    }
                }
                if (SSDP_RECEIVE_TIMEOUT < System.currentTimeMillis() - startTime) {
                    break;
                }
            }

        } catch (InterruptedIOException e) {
            Log.d(LOG_TAG, "search() Timeout.");

            if (device == null) {
                mSearching = false;
                return null;
            }
        } catch (IOException e) {
            Log.d(LOG_TAG, "search() IOException2. : " + e);
            mSearching = false;
            return null;
        } finally {
            Log.d(LOG_TAG, "search() Finish ");
            mSearching = false;

            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }

        return mFoundDevices;
    }

    private String findParameterValue(String ssdpMessage, String paramName) {
        String name = paramName;
        if (!name.endsWith(":")) {
            name = name + ":";
        }
        int start = ssdpMessage.indexOf(name);
        int end = ssdpMessage.indexOf("\r\n", start);
        if (start != -1 && end != -1) {
            start += name.length();
            String val = ssdpMessage.substring(start, end);
            if (val != null) {
                return val.trim();
            }
        }
        return null;
    }
}
