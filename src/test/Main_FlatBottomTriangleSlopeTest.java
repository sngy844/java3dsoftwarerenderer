package test;

import swrast.Display;
import swrast.RenderContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main_FlatBottomTriangleSlopeTest {
    public static void main(String[] args) throws IOException {
        InputStream inputStream = Main_SignedAreaTest.class.getResourceAsStream("texture.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        String [] brickPxString =properties.getProperty("tile").split(",");
        byte brickTexture[] = new byte[brickPxString.length];
        for(int i =0 ; i< brickPxString.length; i+=4){
            brickTexture [i] = (byte) Integer.parseInt(brickPxString[i+3],16); //Alpha
            brickTexture [i+1] = (byte) Integer.parseInt(brickPxString[i+2],16); //B
            brickTexture [i+2] = (byte) Integer.parseInt(brickPxString[i+1],16); //G
            brickTexture [i+3] = (byte) Integer.parseInt(brickPxString[i],16);//R
        }


        //
        Display display = new Display(1024, 1024, "Software Rendering");
        RenderContext target = display.GetFrameBuffer();

        long previousTime = System.nanoTime();
        double elapsedTime = 0;
        boolean isDrawVertices = true;
        // TODO: check winding
        int tris[] = new int[]{
                // Condition y1 = y2
                500,100,850,600,150,600
        };
        target.bindTexture(brickTexture);
        int frame = 0;
        while (true) {
            long currentTime = System.nanoTime();

            target.Clear((byte) 0x00);

            for (int i = 0; i < tris.length; i += 2)
                target.drawPoint(tris[i], tris[i + 1], (byte) 255, (byte) 0, (byte) 0);

            if (isDrawVertices) {
                for (int i = 0; i < tris.length; i += 6)
                    target.drawFlatBottomTriangleSlopeFill(tris[i], tris[i + 1],
                            tris[i + 2], tris[i + 3],
                            tris[i + 4], tris[i + 5],
                            (byte) 255, (byte) 255, (byte) 255);
            }

            for(int x =0 ; x<128; x++)
                for(int y =0 ; y<128; y++){
                    byte r = brickTexture[(y*128+x)*4 +1];
                    byte g = brickTexture[(y*128+x)*4 +2];
                    byte b = brickTexture[(y*128+x)*4 +3];
                    target.DrawPixel(x,y,r,g,b);
                }

            display.SwapBuffers();

            float delta = (float) ((currentTime - previousTime) / 1000000.0);
            elapsedTime += delta;
            previousTime = currentTime;


            frame++;
            if (elapsedTime >= 1000) {
                System.out.println("FPS:" + frame+" delta:"+delta);
                //isDrawVertices = !isDrawVertices;
                elapsedTime = 0;
                frame = 0;
            }

        }
    }
}