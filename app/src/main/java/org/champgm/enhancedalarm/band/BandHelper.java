package org.champgm.enhancedalarm.band;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.microsoft.band.BandClient;
import com.microsoft.band.BandClientManager;
import com.microsoft.band.BandDeviceInfo;
import com.microsoft.band.BandException;
import com.microsoft.band.notification.VibrationType;
import com.microsoft.band.tiles.BandIcon;
import com.microsoft.band.tiles.BandTile;
import com.microsoft.band.tiles.BandTileManager;

import org.champgm.enhancedalarm.MainActivity;
import org.champgm.enhancedalarm.R;

import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by mc023219 on 4/6/15.
 */
public class BandHelper {
    public final UUID TILE_UUID = UUID.fromString("7fb1372d-c9ed-41ed-8941-49b12a74d2bd");
    public final String TILE_NAME = "EnhancedTimer";
    private final MainActivity mainActivity;
    private BandTile bandTile;
    private BandClient bandClient;

    public BandHelper(final MainActivity mainActivity) throws BandException, InterruptedException, TimeoutException {
        this.mainActivity = mainActivity;
        connectToBand();
        addTile();
    }

    public BandClient getBandClient() throws BandException, InterruptedException, TimeoutException {
        connectToBand();
        addTile();
        return bandClient;
    }

    public void vibrate(final VibrationType vibrationType) throws BandException, InterruptedException, TimeoutException {
        bandClient.getNotificationManager().vibrate(vibrationType);
    }

    private void addTile() throws BandException, InterruptedException, TimeoutException {
        connectToBand();
        if (bandClient == null || !bandClient.isConnected()) {
            Log.i("BandHelper", "NOT CONNECTED");
        } else {
            final BandTileManager tileManager = bandClient.getTileManager();
            final Collection<BandTile> tiles = bandClient.getTileManager().getTiles().await();

            boolean foundTile = false;
            for (final BandTile tile : tiles) {
                if (tile.getTileId() == TILE_UUID) {
                    foundTile = true;
                    break;
                }
            }

            if (!foundTile) {
                tileManager.addTile(mainActivity, getBandTile());
            }
        }
    }

    private BandTile getBandTile() {
        if (bandTile != null) {
            return bandTile;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        final BandIcon tileIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(mainActivity.getResources(), R.raw.tile, options));
        final BandIcon badgeIcon = BandIcon.toBandIcon(BitmapFactory.decodeResource(mainActivity.getResources(), R.raw.badge, options));

        final BandTile.Builder bandTileBuilder = new BandTile.Builder(TILE_UUID, TILE_NAME, tileIcon)
                .setTileSmallIcon(badgeIcon);

        bandTile = bandTileBuilder.build();
        return bandTile;
    }

    private void connectToBand() throws BandException, InterruptedException {
        final BandDeviceInfo[] pairedBands = BandClientManager.getInstance().getPairedBands();
        Log.i("=======", "=======================");
        Log.i("=======", Arrays.toString(pairedBands));
        bandClient = BandClientManager.getInstance().create(mainActivity, pairedBands[0]);

        new ConnectToBand().execute(bandClient);
    }

    public void disconnect() {
        if (bandClient != null && bandClient.isConnected()) {
            bandClient.disconnect();
        }
    }
}
