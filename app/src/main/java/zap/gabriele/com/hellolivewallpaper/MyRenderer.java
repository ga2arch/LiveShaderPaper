package zap.gabriele.com.hellolivewallpaper;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by ga2arch on 18/10/14.
 */
public class MyRenderer implements GLWallpaperService.Renderer {

    int width;
    int height;

    // Program
    int mProgram;

    // Uniform indices
    int uResolution;
    int uTime;

    // Attributes indices
    int aPosition;

    // Time
    long start;

    // Shaders
    String vertexSource;
    String fragmentSource;

    float points[] = {
            -1.0f, -1.0f,  0.0f,
            1.0f, -1.0f,  0.0f,
            1.0f,  1.0f,  0.0f,
            1.0f,  1.0f,  0.0f,
            -1.0f,  1.0f,  0.0f,
            -1.0f, -1.0f,  0.0f
    };

    public MyRenderer(String vS, String fS) {
        vertexSource = vS;
        fragmentSource = fS;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        int vertex = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        int fragment = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

        GLES20.glShaderSource(vertex, vertexSource);
        GLES20.glShaderSource(fragment, fragmentSource);

        mProgram = GLES20.glCreateProgram();
        GLES20.glCompileShader(vertex);
        GLES20.glCompileShader(fragment);

        GLES20.glAttachShader(mProgram, vertex);
        GLES20.glAttachShader(mProgram, fragment);

        GLES20.glLinkProgram(mProgram);
        GLES20.glUseProgram(mProgram);

        FloatBuffer pointsVbo = ByteBuffer.allocateDirect(points.length * 4)
                                          .order(ByteOrder.nativeOrder())
                                          .asFloatBuffer();

        pointsVbo.put(points).position(0);

        aPosition = GLES20.glGetAttribLocation(mProgram, "position");
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, pointsVbo);

        uResolution = GLES20.glGetUniformLocation(mProgram, "resolution");
        uTime = GLES20.glGetUniformLocation(mProgram, "time");

        start = System.currentTimeMillis();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        this.width = width;
        this.height = height;
        GLES20.glViewport(0, 0, this.width, this.height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        float current = (System.currentTimeMillis() - start) / 1000.0f;
        current = (float) Math.floor(current * 100) / 100;

        GLES20.glUniform2f(uResolution, ((float) width), ((float) height));
        GLES20.glUniform1f(uTime, current);

        GLES20.glUseProgram(mProgram);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 6);
    }

    public void release() {

    }
}