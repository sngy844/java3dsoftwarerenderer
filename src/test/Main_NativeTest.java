package test;

import swrast.GfxMath;
import swrast.GfxNative;

public class Main_NativeTest {
    public static void main(String[] args) {
        GfxNative.testNative();
        GfxNative.testPassInteger(10);

        float weights_native[] = new float[3];
        GfxNative.baryCentricWeight(0, 0, 0, 1, 2, 0, 0.5f, 0.5f,weights_native);

        float weights_java[] = new float[3];
        GfxMath.barycentricWeight(0, 0, 0, 1, 2, 0, 0.5f, 0.5f,weights_java);

        float area3 = GfxNative.areaTriangle(0, 0, 1, 5, 4, 0);
        float area2 = GfxNative.areaTriangle(1, 1, 3, 3, 7, 1);
        float area1 = GfxNative.areaTriangle(0, 0, 0, 1, 2, 0);
        System.out.println(String.format("Area 1: %f - Area 2: %f - Area 3: %f\n\n\n", area1, area2, area3));
    }
}
