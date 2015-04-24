package org.champgm.enhancedalarm.band;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;
import com.microsoft.band.BandException;
import com.microsoft.band.notification.VibrationType;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.BandTileManager;

import org.champgm.enhancedalarm.R;
import org.champgm.enhancedalarm.util.Toaster;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * A helper class for interacting with the Microsoft Band
 */
public class BandHelper {
    /**
     * The UUID for this app's tile.
     * (Currently this app does not use a tile, but if any messages or notifications are to be displayed in the future,
     * it will need one)
     */
    public static final UUID TILE_UUID = UUID.fromString("7fb1372d-c9ed-41ed-8941-49b12a74d2bd");
    /**
     * The display name for this app's tile.
     * (Currently this app does not use a tile, but if any messages or notifications are to be displayed in the
     * future, it will need one)
     */
    public static final String TILE_NAME = "EnhancedTimer";

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
        // Make sure we're connected
        connectToBand(bandClient);
        if (bandClient != null && bandClient.isConnected()) {
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
            if (!foundTile && activity != null) {
                tileManager.addTile(activity, getBandTile(activity));
            }
        } else {
            Toaster.send(activity, "Cannot add tile, band is not connected.");
        }
    }

    /**
     * Connects to the band specified
     * 
     * @param bandClient
     *            the client to connect.
     */
    public static void connectToBand(final BandClient bandClient) {
        if (bandClient != null && !bandClient.isConnected()) {
            new ConnectToBand().execute(bandClient);
        }
    }

    /**
     * Disconnects the given client, if it is connected.
     * 
     * @param bandClient
     *            the client to disconnect.
     */
    public static void disconnect(final BandClient bandClient) {
        if (bandClient != null && bandClient.isConnected()) {
            bandClient.disconnect();
        }
    }

    /**
     * Will attempt to select a band and return an instance of its {@link com.microsoft.band.BandClient}
     * 
     * @param context
     *            the context from which to start the client-getting activity
     * @param position
     *            the position of the desired band in the {@link com.microsoft.band.BandDeviceInfo} array. You can use
     *            {@link BandHelper#getBands()} to see what bands are currently available.
     * @return the {@link com.microsoft.band.BandClient} for the band at the specified position
     */
    public static BandClient getBandClient(final Context context, final int position) {
        final BandDeviceInfo[] bands = getBands();
        if (position > bands.length - 1) {
            Toaster.send(context, "Unable to retrieve band at position '" + position +
                    "'. Please check the list of connected bands in the settings menu.");
            return null;
        }
        return BandClientManager.getInstance().create(context, bands[position]);
    }

    /**
     * Gets a list of currently connected bands.
     * 
     * @return list of current bands
     */
    public static BandDeviceInfo[] getBands() {
        return BandClientManager.getInstance().getPairedBands();
    }

    /**
     * Sends a vibration with a {@link com.microsoft.band.BandClient}
     * 
     * @param vibrationType
     *            the {@link com.microsoft.band.notification.VibrationType}
     * @param bandClient
     *            the client to use to send the specified vibration
     */
    public static void sendVibration(final VibrationType vibrationType, final BandClient bandClient) {
        new SendVibration(vibrationType).execute(bandClient);
    }

    /**
     * Creates the {@link com.microsoft.band.tiles.BandTile} object for this app
     * 
     * @return the tile object
     */
    private static BandTile getBandTile(final Context context) {
        if (context != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final BandIcon tileIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(context.getResources(), R.raw.tile, options));
            final BandIcon badgeIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(context.getResources(), R.raw.badge, options));

            final BandTile.Builder bandTileBuilder = new BandTile.Builder(TILE_UUID, TILE_NAME, tileIcon)
                    .setTileSmallIcon(badgeIcon);

            // Return it
            return bandTileBuilder.build();
        } else {
            return null;
        }
    }

    /**
     * Checks to see if any bands are connected
     * 
     * @return false if there are none
     */
    public static boolean anyBandsConnected() {
        return getBands().length > 0;
    }
}
