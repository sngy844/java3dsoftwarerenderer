package test;

import swrast.Display;
import swrast.RenderContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main_FlatTopTriangleSlopeTest {


    public static void main(String[] args) throws IOException {
        InputStream inputStream = Main_SignedAreaTest.class.getResourceAsStream("texture.properties");
        Properties properties = new Properties();
        properties.load(inputStream);

        final String textureName = "brick";
        String [] brickPxString =properties.getProperty(textureName).split(",");
        final int textW = Integer.parseInt(properties.getProperty(String.format("%s_width",textureName)));
        byte brickTexture[] = new byte[brickPxString.length];
        for(int i =0 ; i< brickPxString.length; i+=4){
            brickTexture [i] = (byte) Integer.parseInt(brickPxString[i+3],16); //Alpha
            brickTexture [i+1] = (byte) Integer.parseInt(brickPxString[i+0],16); //B
            brickTexture [i+2] = (byte) Integer.parseInt(brickPxString[i+1],16); //G
            brickTexture [i+3] = (byte) Integer.parseInt(brickPxString[i+2],16);//R
        }

        //
        Display display = new Display(200, 200,1024,1014, "Software Rendering");
        RenderContext target = display.GetFrameBuffer();

        long previousTime = System.nanoTime();
        double elapsedTime = 0;
        boolean isDrawVertices = true; int filter =0;

        // TODO: check winding
        int tris[] = new int[]{
                // Condition y0 = y1

                30,109,
                104,109,
                129,159
               // 87,25,262,25,175,150
//                190+500/4,100/4,190+850/4,600/4,190+150/4,600/4,
//                2*190+500/4,100/4,2*190+850/4,600/4,2*190+150/4,600/4
        };
        target.bindTexture(brickTexture,textW,filter);
        int frame = 0;
        while (true) {
            long currentTime = System.nanoTime();

            target.Clear((byte) 0x00);

            for (int i = 0; i < tris.length; i += 2)
                target.drawPoint(tris[i], tris[i + 1], (byte) 255, (byte) 0, (byte) 0);

            if (isDrawVertices) {
                for (int i = 0; i < tris.length; i += 6)
                    target.drawFlatTopTriangleSlopeFill(tris[i], tris[i + 1],
                            tris[i + 2], tris[i + 3],
                            tris[i + 4], tris[i + 5],
                            0,0.5f,
                            0.75f, 0.5f,
                            1,1

                    );
            }

            for(int x =0 ; x<textW; x++)
                for(int y =0 ; y<textW; y++){
                    byte r = brickTexture[(y*textW+x)*4 +3];
                    byte g = brickTexture[(y*textW+x)*4 +2];
                    byte b = brickTexture[(y*textW+x)*4 +1];
                    target.DrawPixel(x,y,r,g,b);
                }
            target.DrawPixel((int) (0.f* (textW-1)), (int) (0.5f*(textW-1)), (byte) 255, (byte) 255, (byte) 0);
            target.DrawPixel((int) (0.75f*(textW-1)), (int) (0.5f*(textW-1)), (byte) 255, (byte) 255, (byte) 0);
            target.DrawPixel((int) (1f* (textW-1)), (int) (1f*(textW-1)), (byte) 255, (byte) 255, (byte) 0);

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
                //filter = (filter+1)%3;
                //target.bindTexture(brickTexture,textW,filter);
            }

        }
    }
}
