public class Main
{
	public static void main(String[] args)
	{
		Display display = new Display(1024, 768, "Software Rendering");
		RenderContext target = display.GetFrameBuffer();

		long previousTime = System.nanoTime();
		while(true)
		{
			long currentTime = System.nanoTime();
			float delta = (float)((currentTime - previousTime)/1000000000.0);
			previousTime = currentTime;

			target.Clear((byte)0x00);
			target.drawGrid();

			target.DrawPixel(50,50,(byte)0xFF,(byte)0xFF,(byte)0xFF);
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


			target.drawTriangle(600,600,600,285,400,500,(byte)0xFF,(byte)0xFF,(byte)0xFF);

			display.SwapBuffers();

		}
	}
}
