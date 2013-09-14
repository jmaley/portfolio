import java.io.*;
import java.net.URL;

/*
 * Title: WebImage.java
 * Name:  Joe Maley
 * Date:  March 8 2012
 * 
 * Description: A representation of a web-based image
 */

public class WebImage implements WebElement
{
    String addr;
    
    public WebImage(String addr)
    {
        this.addr = addr;
    }
    
    public void save(String savePath)
    {
        URL url;
        
        try
        {
            url = new URL(addr); // Connect to the address
            
            String fileName = addr.substring(addr.lastIndexOf("/") + 1);
            
            savePath += "images/";
            new File(savePath).mkdirs();
            
            File localFile = new File(savePath + fileName);
            
            InputStream  reader = url.openStream();
            OutputStream writer = new FileOutputStream(localFile);
            
            byte[] b = new byte[2048];
            
            int length;
            while ((length = reader.read(b)) != -1)
            {
                writer.write(b, 0, length);
            }
            
            //System.out.println("saved " + savePath + fileName);

            reader.close();
            writer.close();

        }
        catch (IOException ioe)
        {
            System.err.println(ioe.toString());
            return;
        }
    }
}
