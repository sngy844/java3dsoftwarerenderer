import swrast.Display;
import swrast.DrawHead;
import swrast.RenderContext;

import java.io.IOException;
import java.io.InputStream;

public class Main
{
	public static void main(String[] args) throws IOException {
		//
		Display display = new Display(801, 801,1024,1014, "Software Rendering");
		RenderContext target = display.GetFrameBuffer();

		DrawHead draw = new DrawHead(target, Main.class.getResourceAsStream("vertices.properties"));

		long previousTime = System.nanoTime();
		double elapsedTime = 0;
		int frame = 0;

		int y = 600;
		int move =1;
		boolean drawWire = true;
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000.0);
			elapsedTime +=delta;
			previousTime = currentTime;

			target.Clear((byte) 0x00);
			//target.drawGrid();

//			target.DrawPixel(50,50,(byte)0xFF,(byte)0xFF,(byte)0xFF);
//
//			target.drawLine(200,200, 400,400, (byte)0xFF,(byte)0xFF,(byte)0xFF);
//			target.drawLine(200,300, 400,300, (byte)0xFF,(byte)0xFF,(byte)0xFF);
//			target.drawLine(500,500, 300 ,150, (byte)0xFF,(byte)0x00,(byte)0xFF);
//
//			target.drawLine(50,50, 400,50, (byte)0x00,(byte)0xFF,(byte)0xFF);
//			target.drawLine(50,50, 50,400, (byte)0xFF,(byte)0xFF,(byte)0xFF);
//			target.drawLine(50,400, 400,400, (byte)0xFF,(byte)0x00,(byte)0xFF);
//			target.drawLine(400,400, 400,50, (byte)125,(byte)125,(byte)0xFF);
//
//			target.drawLine(400,50, 50,400, (byte)125,(byte)125,(byte)125);
//			target.drawLine(50,50, 400,400, (byte)75,(byte)125,(byte)75);
//
//
//			target.drawTriangle(y, y, 600, 285, 400, 500, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
//			y+=move;
//			if(y == 768-1)
//				move = -1;
//			if(y == 1)
//				move =1;

			//Since vertices from obj range from -1.0 1.0 we add 1.0 to make range 0.0 to 2.0 then scaled with width and height for vx, vy
			draw.drawPoints();

			if(drawWire) {
				draw.drawWire();
			}

			display.SwapBuffers();

			frame++;

			if(elapsedTime >= 1000){
				System.out.println("FPS:"+frame);
				elapsedTime=0;
				frame =0;
				drawWire = !drawWire;
			}
		}
	}
}
