package test;

import swrast.GfxMath;
import swrast.GfxNative;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class Main_NativeTest {
    public static void main(String[] args) {
        final int INT_BYTE_SIZES = 4;
        ByteBuffer bb = ByteBuffer.allocateDirect(100  * INT_BYTE_SIZES);
        bb.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer= bb.asIntBuffer();
        GfxNative.testDirectBuffer(intBuffer);
        for(int i =0; i< intBuffer.capacity(); i++){
            System.out.print(intBuffer.get(i)+" ");
        }

        GfxNative.testNative();
        GfxNative.testPassInteger(10);

        float weights_native[] = new float[3];
        GfxNative.baryCentricWeight(0, 0, 0, 1, 2, 0, 0.5f, 0.5f,weights_native);

        float weights_java[] = new float[3];
        GfxMath.baryCentricWeight(0, 0, 0, 1, 2, 0, 0.5f, 0.5f,weights_java);

        float area3 = GfxNative.areaTriangle(0, 0, 1, 5, 4, 0);
        float area2 = GfxNative.areaTriangle(1, 1, 3, 3, 7, 1);
        float area1 = GfxNative.areaTriangle(0, 0, 0, 1, 2, 0);
        System.out.println(String.format("Area 1: %f - Area 2: %f - Area 3: %f\n\n\n", area1, area2, area3));
    }
}
