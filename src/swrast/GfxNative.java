package swrast;

public class GfxNative {
    static{
        System.loadLibrary("gfx_native");
    }

    static public native void testNative();
    static public native void testPassInteger(int t);
    static public native void baryCentricWeight(float ax, float ay,
                                                float bx, float by,
                                                float cx, float cy,
                                                float px, float py,
                                                float weights[]);
    static public native float areaTriangle(float x0, float y0, 	float x1, float y1,	float x2, float y2);

    static public native void drawGrid(int gridIndex[] , byte pixelComponent[], int length);


    static public native void drawFlatBottomTriangleSlopeFill(int x0, int y0, int x1, int y1, int x2, int y2,
                                                              float u0, float v0, float u1, float v1, float u2, float v2,
                                                              int filter,
                                                              byte[] texture, int textureWidth ,byte[] pixelComponents,int frameBufferWidth
    );


    static public native void drawFlatTopTriangleSlopeFill(int x0, int y0, int x1, int y1, int x2, int y2,
                                                              float u0, float v0, float u1, float v1, float u2, float v2,
                                                              int filter,
                                                              byte[] texture, int textureWidth ,byte[] pixelComponents,int frameBufferWidth
    );
}
