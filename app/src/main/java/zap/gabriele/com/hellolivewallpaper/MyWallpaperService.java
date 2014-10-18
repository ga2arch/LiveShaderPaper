package zap.gabriele.com.hellolivewallpaper;

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
            renderer = new MyRenderer();
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

}
