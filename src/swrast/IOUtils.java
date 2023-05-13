package swrast;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class IOUtils {

    static public void save(String fileName,byte m_pixelComponents [], int m_width, int m_height){
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName, false));
            writer.append("P3\n");
            writer.append(String.format("%s %s\n", m_width,m_height));
            writer.append("255\n");

            for(int i=0; i< m_height;i++){
                for(int k=0 ; k< m_width;k++){
                    writer.append(String.format("%d %d %d ",
                                    m_pixelComponents[(i* m_width+k)*4+1]&0xFF,
                                    m_pixelComponents[(i* m_width+k)*4+2]&0xFF,
                                    m_pixelComponents[(i* m_width+k)*4+3]&0xFF
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

    static public void saveTarga(String fileName,byte m_pixelComponents [], int m_width, int m_height){
        try (FileOutputStream fos = new FileOutputStream(fileName,false))
        {
            fos.write (0x00);	/* ID Length, 0 => No ID	*/
            fos.write (0x00);	/* Color Map Type, 0 => No color map included	*/
            fos.write (0x02);	/* Image Type, 2 => Uncompressed, True-color Image */
            fos.write (0x00);	/* Next five bytes are about the color map entries */
            fos.write (0x00);	/* 2 bytes Index, 2 bytes length, 1 byte size */
            fos.write (0x00);
            fos.write (0x00);
            fos.write (0x00);
            fos.write (0x00);	/* X-origin of Image	*/
            fos.write (0x00);
            fos.write (0x00);	/* Y-origin of Image	*/
            fos.write (0x00);
            fos.write (m_width & 0xff);      /* Image Width	*/
            fos.write ((m_width>>8) & 0xff);
            fos.write (m_height & 0xff);     /* Image Height	*/
            fos.write ((m_height>>8) & 0xff);
            fos.write (0x18);		/* Pixel Depth, 0x18 => 24 Bits	*/
            fos.write (0x20);		/* Image Descriptor	*/
            for (int y=0; y< m_height;y++) {
                for (int x=0; x<m_width; x++) {
                    byte r, g, b;
                    int i = (y*m_width + x) * 4;
                    r = (m_pixelComponents[i+1]);
                    g = (m_pixelComponents[i+2]);
                    b = (m_pixelComponents[i+3]);

                    fos.write(r); /* write blue */
                    fos.write(g); /* write green */
                    fos.write(b); /* write red */
                }
            }
            fos.close();
            System.out.println("Successfully written data to the file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
