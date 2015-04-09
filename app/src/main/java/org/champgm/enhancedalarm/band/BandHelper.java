package org.champgm.enhancedalarm.band;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;
import com.microsoft.band.BandException;
import com.microsoft.band.notification.VibrationType;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.BandTileManager;

import org.champgm.enhancedalarm.R;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * A helper class for interacting with the Microsoft Band
 */
public class BandHelper {
    public static final UUID TILE_UUID = UUID.fromString("7fb1372d-c9ed-41ed-8941-49b12a74d2bd");
    public static final String TILE_NAME = "EnhancedTimer";

    // private final Context context;
    // private BandTile bandTile;
    // private BandClient bandClient;

    /**
     * Creates an instance and immediately attemps to connect to the band.
     * 
     * @param context
     *            the context to use when creating intents
     * @throws com.microsoft.band.BandException
     *             if the band cannot be connected
     * @throws InterruptedException
     *             if connecting to the band takes too long
     * @throws java.util.concurrent.TimeoutException
     *             if something takes too long
     */
    // public BandHelper(final Context context) throws BandException, InterruptedException, TimeoutException {
    // this.context = context;
    // connectToBand();
    // }

    /**
     * Adds this app's tile to the band.
     * 
     * @param activity
     *            the activity to use when creating the intent
     * @throws com.microsoft.band.BandException
     *             if the band cannot be connected
     * @throws InterruptedException
     *             if there is some unexpected issue
     * @throws java.util.concurrent.TimeoutException
     *             if something takes too long
     */
    public static void addTile(final BandClient bandClient, final Activity activity) throws BandException, InterruptedException, TimeoutException {
        Preconditions.checkNotNull(bandClient, "bandClient may not be null.");
        Preconditions.checkNotNull(activity, "activity may not be null.");

        // Make sure we're connected
        connectToBand(bandClient);
        if (bandClient == null || !bandClient.isConnected()) {
            Log.i("BandHelper", "NOT CONNECTED");
        } else {
            // Instantiate the tile manager and get a list of existing tiles
            final BandTileManager tileManager = bandClient.getTileManager();
            final Collection<BandTile> tiles = bandClient.getTileManager().getTiles().await();

            // Search for our tile
            boolean foundTile = false;
            for (final BandTile tile : tiles) {
                if (tile.getTileId() == TILE_UUID) {
                    foundTile = true;
                    break;
                }
            }

            // Add it if it's not there.
            if (!foundTile) {
                tileManager.addTile(activity, getBandTile(activity));
            }
        }
    }

    /**
     * Creates the {@link com.microsoft.band.tiles.BandTile} object for this app
     * 
     * @return the tile object
     */
    private static BandTile getBandTile(final Context context) {
        Preconditions.checkNotNull(context, "context may not be null.");

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final BandIcon tileIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(context.getResources(), R.raw.tile, options));
        final BandIcon badgeIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(context.getResources(), R.raw.badge, options));

        final BandTile.Builder bandTileBuilder = new BandTile.Builder(TILE_UUID, TILE_NAME, tileIcon)
                .setTileSmallIcon(badgeIcon);

        // Return it
        return bandTileBuilder.build();
    }

    /**
     * Connects to the band
     * 
     * @throws com.microsoft.band.BandException
     *             if the band cannot be connected
     * @throws InterruptedException
     *             if there is some unexpected issue
     */
    public static void connectToBand(final BandClient bandClient) {
        if (bandClient != null && !bandClient.isConnected()) {
            Preconditions.checkNotNull(bandClient, "bandClient may not be null.");
            Log.d("BandHelper", "Connecting to band.");
            new ConnectToBand().execute(bandClient);
        } else {
            Log.d("BandHelper", "BandClient already connected.");
        }
    }

    /**
     * Disconnects.
     */
    public static void disconnect(final BandClient bandClient) {
        if (bandClient != null && bandClient.isConnected()) {
            Log.d("BandHelper", "Disconnecting from band.");
            bandClient.disconnect();
        } else {
            Log.d("BandHelper", "Band not connected, cannot disconnect.");
        }
    }

    public static BandClient getBandClient(final Context context, final int position) {
        final BandDeviceInfo[] bands = getBands();
        return BandClientManager.getInstance().create(context, bands[0]);
    }

    public static BandDeviceInfo[] getBands() {
        return BandClientManager.getInstance().getPairedBands();
    }

    public static void sendVibration(final VibrationType vibrationType, final BandClient bandClient) {
        Log.d("BandHelper", "Sending vibration type: " + vibrationType.toString());
        new SendVibration(vibrationType).execute(bandClient);
    }
}
