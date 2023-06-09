package swrast; /**
@file
@author Benny Bobaganoosh <thebennybox@gmail.com>
@section LICENSE

Copyright (c) 2014, Benny Bobaganoosh
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer. 
2. Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.util.Arrays;

/**
 * Stores a set of pixels in a component-based format.
 * The component-based format stores colors as follows:
 *
 * Byte 0: Alpha
 * Byte 1: Blue 
 * Byte 2: Green
 * Byte 3: Red
 *
 * This format is fast, compact, and ideal for software rendering.
 * It has the following key advantages:
 * - Entire images can be copied to the screen with a single call to
 * System.arrayCopy. (If the screen is not in ABGR pixel format, it requires
 * some conversion. However, the conversion is typically quick and simple).
 * - Per component operations, such as  lighting, can be performed cheaply without any 
 * pixel format converison.
 *
 * This class is primarily intended to be a high-performance image storing 
 * facility for software rendering. As such, there are points where ease of 
 * use is compromised for the sake of performance. If you need to store and 
 * use images outside of a software renderer, it is recommended that you use 
 * Java's standard image classes instead.
 */
public class Bitmap
{
	/** The width, in pixels, of the image */
	protected final int  m_width;
	/** The height, in pixels, of the image */
	protected final int  m_height;
	/** Every pixel component in the image */
	protected final byte m_pixelComponents[];

	/** Basic getter */
	public int GetWidth() { return m_width; }
	/** Basic getter */
	public int GetHeight() { return m_height; }

	/**
	 * Creates and initializes a Bitmap.
	 *
	 * @param width The width, in pixels, of the image.
	 * @param height The height, in pixels, of the image.
	 */
	public Bitmap(int width, int height)
	{
		m_width      = width;
		m_height     = height;
		m_pixelComponents = new byte[m_width * m_height * 4];
	}

	/**
	 * Sets every pixel in the bitmap to a specific shade of grey.
	 */
	public void Clear(byte shade)
	{
		Arrays.fill(m_pixelComponents, shade);
	}

	public void Clear(byte r, byte g, byte b){
		final int totalElems =m_height*m_width*4;
		for(int i = 0 ; i<totalElems; i+=4){
			m_pixelComponents[i] = (byte) 255;
			m_pixelComponents[i+1] = b;
			m_pixelComponents[i+2] = g;
			m_pixelComponents[i+3] = r;
		}
	}

	/**
	 * Sets the pixel at (x, y) to the color specified by (a,b,g,r).
	 */
	public void DrawPixel(int x, int y, byte a, byte b, byte g, byte r)
	{
		int index = (x + y * m_width) * 4;
		m_pixelComponents[index    ] = a;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;
	}

	public void DrawPixel(int x, int y, byte r, byte g, byte b)
	{
		int index = (x + y * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;
	}

	public void drawPoint(int x, int y, byte r, byte g, byte b)
	{
		int index = (x + y * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;

		index = (x-1 + y * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;

		index = (x+1 + y * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;

		index = (x-1 + (y-1) * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;

		index = (x+1 + (y+1) * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;

		index = (x + (y+1) * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;

		index = (x + (y-1) * m_width) * 4;
		m_pixelComponents[index    ] = (byte)0xFF;
		m_pixelComponents[index + 1] = b;
		m_pixelComponents[index + 2] = g;
		m_pixelComponents[index + 3] = r;
	}
	/**
	 * Copies the Bitmap into a BGR byte array.
	 */
	public void CopyToByteArray(byte[] dest)
	{
//		int index3;
//		int index4;
//		final int totalPixels =  m_width * m_height;
//		for(int i = 0; i < totalPixels; i++)
//		{
//			index3 =  i*3; 		index4 = i*4;
//			dest[index3    ] = m_pixelComponents[index4 + 1];
//			dest[index3 + 1] = m_pixelComponents[index4 + 2];
//			dest[index3 + 2] = m_pixelComponents[index4 + 3];
//		}
		GfxNative.copyToByteArray(dest,m_pixelComponents,m_width * m_height);
	}
}
