public class RenderContext extends Bitmap
{
	public RenderContext(int width, int height)	{
		super(width, height);
	}

	public void drawLine(int x0, int y0, int x1, int y1, byte r, byte g, byte b){
		//DDA
		final int delta_x = x1 - x0;
		final int delta_y = y1 - y0;

		//Optimize for the case y=x line

		if(delta_x == 0){
			if(y0 > y1){
				int temp = y0;
				y0 = y1;
				y1 = temp;
			}
			for(int i =y0 ; i<= y1; i++ ){
				this.DrawPixel(x0, i , r, g, b);
			}
		}
		else if(delta_y  == 0){
			for(int i =x0 ; i<= x1; i++ ){
				this.DrawPixel(i, y1 , r, g, b);
			}
		}
		else {
			final float side_length = Math.abs(delta_x) > Math.abs(delta_y) ? Math.abs(delta_x) : Math.abs(delta_y);

			final float increment_x = delta_x / (float) side_length;
			final float increment_y = delta_y / (float) side_length;

			float currentX = x0;
			float currentY = y0;

			for (int i = 0; i <= side_length; i++) {
				this.DrawPixel((int) currentX, (int) currentY, r, g, b);
				currentX += increment_x;
				currentY += increment_y;
			}
		}
	}
}
