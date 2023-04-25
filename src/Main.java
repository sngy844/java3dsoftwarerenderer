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

			//stars.UpdateAndRender(target, delta);
			target.Clear((byte)0x00);

			target.DrawPixel(50,50,(byte)0xFF,(byte)0xFF,(byte)0xFF);

			for(int x = 40 ; x< 100 ;x++){
				target.DrawPixel(x,60, (byte)0xFF,(byte)0x00,(byte)0x00);
				target.DrawPixel(x,70, (byte)0x00,(byte)0xFF,(byte)0x00);
				target.DrawPixel(x,80, (byte)0x00,(byte)0x00,(byte)0xFF);
			}

			display.SwapBuffers();
		}
	}
}
