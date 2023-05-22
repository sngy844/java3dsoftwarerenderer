package test;

import swrast.RenderContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main_WindowlessTest {
    static int TEXT_WIDTH  = 0;
    static int TEXT_HEIGHT = 0;
    public static byte [] genTexture() throws IOException {
        InputStream inputStream = Main_SignedAreaTest.class.getResourceAsStream("texture.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        final String textureName = "doge";
        String [] brickPxString =properties.getProperty(textureName).split(",");
        TEXT_WIDTH = Integer.parseInt(properties.getProperty(String.format("%s_width",textureName)));
        TEXT_HEIGHT = TEXT_WIDTH;
        byte dogeTexture[] = new byte[brickPxString.length];
        for(int i =0 ; i< brickPxString.length; i+=4){
            dogeTexture [i] = (byte) Integer.parseInt(brickPxString[i+3].trim(),16); //Alpha
            dogeTexture [i+1] = (byte) Integer.parseInt(brickPxString[i+2].trim(),16); //B
            dogeTexture [i+2] = (byte) Integer.parseInt(brickPxString[i+1].trim(),16); //G
            dogeTexture [i+3] = (byte) Integer.parseInt(brickPxString[i+0].trim(),16);//R
        }
        return dogeTexture;
    }

    public static void main(String[] args) throws IOException {
        RenderContext target = new RenderContext(1024,768);

        byte [] textureData = genTexture();

        target.setDepthTest(false);
        target.bindTexture(textureData,TEXT_WIDTH,TEXT_HEIGHT,0);
        target.Clear((byte) 255, (byte) 255, (byte) 255);

        target.drawTriangleTexture(
                            500,10,0,
                            850,500,0,
                            130,720,0,
                            0.5f,0,
                            1,1,
                            0,1
        );

        target.save("headless.ppm");
        target.saveTarga("headless.tga");

    }
}
