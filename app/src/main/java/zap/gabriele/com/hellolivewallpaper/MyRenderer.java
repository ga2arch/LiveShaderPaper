package zap.gabriele.com.hellolivewallpaper;

import android.opengl.GLES20;
import android.util.Log;

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

    long start;
    long old;

    String vertexSource = "attribute vec3 position;" +
            "uniform vec2 resolution;" +
            "uniform float time;" +
            "varying vec2 iResolution;" +
            "varying float iGlobalTime;" +
            "void main(void) {" +
            "   iResolution = resolution;" +
            "   iGlobalTime = time;" +
            "   gl_Position = vec4(position, 1.0);" +
            "} ";

    String fragmentSource = "varying float iGlobalTime;" +
            "varying vec2 iResolution;" +
            "void main(void)\n" +
            "{\n" +
            "\tvec2 p = (2.0*gl_FragCoord.xy-iResolution.xy)/min(iResolution.y,iResolution.x);\n" +
            "\t\n" +
            "\tp.y -= 0.25;\n" +
            "\n" +
            "    vec3 bcol = vec3(1.0,0.8,0.7-0.07*p.y)*(1.0-0.25*length(p));\n" +
            "    float tt = mod(iGlobalTime,1.5)/1.5;\n" +
            "    float ss = pow(tt,.2)*0.5 + 0.5;\n" +
            "    ss -= ss*0.2*sin(tt*6.2831*3.0)*exp(-tt*4.0);\n" +
            "    p *= vec2(0.5,1.5) + ss*vec2(0.5,-0.5);\n" +
            "   \n" +
            "\n" +
            "    // shape\n" +
            "    float a = atan(p.x,p.y)/3.141593;\n" +
            "    float r = length(p);\n" +
            "    float h = abs(a);\n" +
            "    float d = (13.0*h - 22.0*h*h + 10.0*h*h*h)/(6.0-5.0*h);\n" +
            "\n" +
            "\t// color\n" +
            "\tfloat s = 1.0-0.5*clamp(r/d,0.0,1.0);\n" +
            "\ts = 0.75 + 0.75*p.x;\n" +
            "\ts *= 1.0-0.25*r;\n" +
            "\ts = 0.5 + 0.6*s;\n" +
            "\ts *= 0.5+0.5*pow( 1.0-clamp(r/d, 0.0, 1.0 ), 0.1 );\n" +
            "\tvec3 hcol = vec3(1.0,0.5*r,0.3)*s;\n" +
            "\t\n" +
            "    vec3 col = mix( bcol, hcol, smoothstep( -0.01, 0.01, d-r) );\n" +
            "\n" +
            "    gl_FragColor = vec4(col,1.0);\n" +
            "}";

    float points[] = {
            -1.0f, -1.0f,  0.0f,
            1.0f, -1.0f,  0.0f,
            1.0f,  1.0f,  0.0f,
            1.0f,  1.0f,  0.0f,
            -1.0f,  1.0f,  0.0f,
            -1.0f, -1.0f,  0.0f
    };

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        start = System.currentTimeMillis();

        Log.d("CREATE", "CREATED");

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
        GLES20.glVertexAttribPointer(aPosition, 3,
                GLES20.GL_FLOAT, false, 0, pointsVbo);

        uResolution = GLES20.glGetUniformLocation(mProgram, "resolution");
        uTime = GLES20.glGetUniformLocation(mProgram, "time");

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

        long current = System.currentTimeMillis() - start;

        GLES20.glUniform2f(uResolution, ((float) width), ((float) height));
        GLES20.glUniform1f(uTime, (float) current/1000);

        GLES20.glUseProgram(mProgram);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 6);
    }

    public void release() {
    }
}
