package net.tsbe.tetrominoes.models.mechanics;

import net.tsbe.tetrominoes.gles.GLESSurfaceView;
import net.tsbe.tetrominoes.models.GameUpdateable;

import java.util.Date;

public class Clock extends Thread {

    private Date lastTick = new Date();
    public boolean stopped = false;

    private GameUpdateable[] updateables;
    private GLESSurfaceView surfaceView;

    public Clock(GLESSurfaceView surfaceView, GameUpdateable... updateables){
        this.updateables = updateables;
        this.surfaceView = surfaceView;
    }

    @Override
    public void run() {
        while(!stopped){
            long delta = (new Date()).getTime() - lastTick.getTime();
            lastTick = new Date();
            for(GameUpdateable u : updateables) surfaceView.post(() -> u.Tick(delta));
        }
    }
}
