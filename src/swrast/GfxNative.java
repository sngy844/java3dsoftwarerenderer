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
}
