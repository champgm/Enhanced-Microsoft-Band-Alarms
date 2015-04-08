package org.champgm.enhancedalarm.band;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;
import com.microsoft.band.BandException;
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
    public final UUID TILE_UUID = UUID.fromString("7fb1372d-c9ed-41ed-8941-49b12a74d2bd");
    public final String TILE_NAME = "EnhancedTimer";
    private final Context context;
    private BandTile bandTile;
    private BandClient bandClient;

    /**
     * Creates an instance and immediately attemps to connect to the band.
     * 
     * @param context
     *            the context to use when creating intents
     * @throws BandException
     *             if the band cannot be connected
     * @throws InterruptedException
     *             if connecting to the band takes too long
     * @throws TimeoutException
     *             if something takes too long
     */
    public BandHelper(final Context context) throws BandException, InterruptedException, TimeoutException {
        this.context = context;
        connectToBand();
    }

    /**
     * Adds this app's tile to the band.
     * 
     * @param activity
     *            the activity to use when creating the intent
     * @throws BandException
     *             if the band cannot be connected
     * @throws InterruptedException
     *             if there is some unexpected issue
     * @throws TimeoutException
     *             if something takes too long
     */
    public void addTile(final Activity activity) throws BandException, InterruptedException, TimeoutException {
        // Make sure we're connected
        connectToBand();
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
                tileManager.addTile(activity, getBandTile());
            }
        }
    }

    /**
     * Creates the {@link com.microsoft.band.tiles.BandTile} object for this app
     * 
     * @return the tile object
     */
    private BandTile getBandTile() {
        // If we already did that, just return what we already made
        if (bandTile != null) {
            return bandTile;
        }

        // Otherwise, use bitmap resources to build the tile
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final BandIcon tileIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(context.getResources(), R.raw.tile, options));
        final BandIcon badgeIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(context.getResources(), R.raw.badge, options));

        final BandTile.Builder bandTileBuilder = new BandTile.Builder(TILE_UUID, TILE_NAME, tileIcon)
                .setTileSmallIcon(badgeIcon);

        // Save it for later, just in case
        bandTile = bandTileBuilder.build();

        // Return it
        return bandTile;
    }

    /**
     * Connects to the band
     * 
     * @throws BandException
     *             if the band cannot be connected
     * @throws InterruptedException
     *             if there is some unexpected issue
     */
    private void connectToBand() throws BandException, InterruptedException {
        final BandDeviceInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
        bandClient = BandClientManager.getInstance().create(context, pairedBands[0]);
        new ConnectToBand().execute(bandClient);
    }

    /**
     * Disconnects.
     */
    public void disconnect() {
        if (bandClient != null && bandClient.isConnected()) {
            bandClient.disconnect();
        }
    }
}
