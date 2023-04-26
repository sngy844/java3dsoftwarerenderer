public class Main
{
	public static void main(String[] args)
	{
		Display display = new Display(1024, 768, "Software Rendering");
		RenderContext target = display.GetFrameBuffer();

		long previousTime = System.nanoTime();
		double elapsedTime = 0;
		int frame = 0;

		int y = 600;
		int move =1;
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000.0);
			elapsedTime +=delta;
			previousTime = currentTime;

			target.Clear((byte) 0x00);
			target.drawGrid();

			target.DrawPixel(50,50,(byte)0xFF,(byte)0xFF,(byte)0xFF);

			target.drawLine(200,200, 400,400, (byte)0xFF,(byte)0xFF,(byte)0xFF);
			target.drawLine(200,300, 400,300, (byte)0xFF,(byte)0xFF,(byte)0xFF);
			target.drawLine(500,500, 300 ,150, (byte)0xFF,(byte)0x00,(byte)0xFF);

			target.drawLine(50,50, 400,50, (byte)0x00,(byte)0xFF,(byte)0xFF);
			target.drawLine(50,50, 50,400, (byte)0xFF,(byte)0xFF,(byte)0xFF);
			target.drawLine(50,400, 400,400, (byte)0xFF,(byte)0x00,(byte)0xFF);
			target.drawLine(400,400, 400,50, (byte)125,(byte)125,(byte)0xFF);

			target.drawLine(400,50, 50,400, (byte)125,(byte)125,(byte)125);
			target.drawLine(50,50, 400,400, (byte)75,(byte)125,(byte)75);


			target.drawTriangle(y, y, 600, 285, 400, 500, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF);
			y+=move;
			if(y == 768-1)
				move = -1;
			if(y == 1)
				move =1;

			display.SwapBuffers();

			frame++;
			if(elapsedTime >= 1000){
				System.out.println("FPS:"+frame);
				elapsedTime=0;
				frame =0;
			}
		}
	}
}
