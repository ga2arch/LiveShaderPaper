package zap.gabriele.com.hellolivewallpaper;

import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ga2arch on 18/10/14.
 */
public class MyWallpaperService extends GLWallpaperService {

    public MyWallpaperService() {
        super();
    }

    public Engine onCreateEngine() {
        MyEngine engine = new MyEngine();
        return engine;
    }

    class MyEngine extends GLEngine {
        MyRenderer renderer;
        public MyEngine() {
            super();
            renderer = new MyRenderer(loadShader("s.vert"), loadShader("s.frag"));

            setEGLContextClientVersion(2);
            setRenderer(renderer);
            setRenderMode(RENDERMODE_CONTINUOUSLY);
        }

        public void onDestroy() {
            super.onDestroy();
            if (renderer != null) {
                renderer.release();
            }
            renderer = null;
        }
    }

    String loadShader(String filename) {
        AssetManager am = getApplicationContext().getAssets();
        try {
            InputStream is = am.open(filename);
            return convertStreamToString(is);

        } catch (IOException ex) {

        }
        return "";
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
