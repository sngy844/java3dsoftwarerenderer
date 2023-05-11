package test;

import swrast.GfxMath;
import swrast.GfxNative;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class Main_NativeTest {
    public static void main(String[] args) {
        //Get regular array returned from native call
        int dims [] = new int[2]; //Dim[0] width, dim[2] height
        byte[] pngData =GfxNative.openPNGFile("res/67gu9oeb.png",dims);
        save("nativepng.ppm",pngData,dims[0],dims[1]);

        //Direct Buffer test
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

    static void save(String fileName,byte[] m_displayComponents,int width, int height){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.append("P3\n");
            writer.append(String.format("%s %s\n", width,height));
            writer.append("255\n");

            for(int i=0; i< height;i++){
                for(int k=0 ; k< width;k++){
                    writer.append(String.format("%d %d %d ",
                                m_displayComponents[(i* width+k)*4+2]&0xFF,
                                m_displayComponents[(i* width+k)*4+1]&0xFF,
                                m_displayComponents[(i* width+k)*4+0]&0xFF
                            )
                    );
                }
                writer.append("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
