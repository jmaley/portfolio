/*
 * Title: WebPage.java
 * Name:  Joe Maley
 * Date:  March 8 2012
 * 
 * Description: A representation of a web-based page.
 *              Also crawls and saves files/images to the repository.
 */

import java.io.*;
import java.net.URL;
import java.util.LinkedList;

public class WebPage implements WebElement
{
    URL url;        // URL
    String addr;    // address
    
    // Linked list represent all the images/pages/files on this page
    LinkedList<WebPage>  pages  = new LinkedList<WebPage>();
    LinkedList<WebFile>  files  = new LinkedList<WebFile>();
    LinkedList<WebImage> images = new LinkedList<WebImage>();
    
    // Constructor
    public WebPage(String addr)
    {
        this.addr = addr;
    }
    
    // Recursive web crawling
    public void crawl(int depth)
    {
        if (depth < 0) // Reached maximum crawl depth
            return;
        
        try
        {
            url = new URL(addr); // Connect to the address

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            String buffer = bufferedReader.readLine();
            
            while (buffer != null)
            {
                buffer = buffer.replaceAll(" ", "");    // Remove whitespace for easy parsing
                parseBuffer(buffer, "ahref=\"");        // <a href = "...">
                parseBuffer(buffer, "imgsrc=\"");       // <img src = "...">
                buffer = bufferedReader.readLine();
            }
        }
        catch (IOException ioe)
        {
            System.err.println(ioe.toString());
            images = null;
            pages  = null;
            files  = null;
            return; // Exit this branch
        }
        
        DownloadRepository downloadRepository = DownloadRepository.getInstance();
        downloadRepository.addWebElements(this);
        
        for (WebPage page : pages)  // For each page in the pages LL
            page.crawl(depth - 1);  // Recursively crawl
    }
    
    // Parse the buffer by an HTML token
    private void parseBuffer(String buffer, String token)
    {
        int i, j;
        String subString;
        
        // Isolate the image/file/page URL
        while ((i = buffer.indexOf(token)) >= 0 && (j = buffer.indexOf("\"", i + token.length())) > i)
        {
            subString = buffer.substring(i + token.length(), j);    // Remove substring
            StoreElement(subString);                                // Store it in the LL
            buffer = buffer.substring(j);                           // Check for another token in the line
        }
    }
    
    // Decide if and where to store the WebElement
    private void StoreElement(String element)
    {
        if (!element.startsWith("https://") && !element.startsWith("http://"))
            return;
        
        /*
         * To decipher between images, files, and web pages:
         * web pages: anything with webPageExtensions or ending with "/",
         *            because rarely are file extensions proceeded by a backslash.
         * web images: anything ending with the webImageExtensions.
         * web files: anything with an extension that is not a web page or web image.
         */
        
        String[] webPageExtensions = { "html", "htm", "shtml", "com", "edu", "gov", "net", "org" };
        String[] webImageExtensions = { "jpeg", "jpg", "bmp", "ppm", "png", "gif" };
        
        int i;
        for (i = 0; i < webPageExtensions.length; i++)
            if (element.endsWith("." + webPageExtensions[i]) || element.endsWith("/") ||
                    (element.lastIndexOf("/") > element.lastIndexOf("."))) 
            {
                pages.add(new WebPage(element));
                return;
            }
        
        for (i = 0; i < webImageExtensions.length; i++)
            if (element.endsWith("." + webImageExtensions[i]))
            {
                images.add(new WebImage(element));
                return;
            }
        
        files.add(new WebFile(element));
    }
    
    // Return the images LL
    public LinkedList<WebImage> getImages()
    {
        return images;
    }
    
    // Return the files LL
    public LinkedList<WebFile> getFiles()
    {
        return files;
    }
    
    // Return pages LL
    public LinkedList<WebPage> getWebPages()
    {
        return pages;
    }
    
    // Save (UNUSED FOR WebPage)
    public void save(String savePath)
    {

    }
}
