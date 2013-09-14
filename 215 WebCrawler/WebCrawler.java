/*
 * Title: WebCrawler.java
 * Name:  Joe Maley
 * Date:  March 8 2012
 * 
 * Description: Reads command line arguments and starts crawling
 *              from the given web address.
 */

public class WebCrawler
{
    public static void main(String[] args)
    {
        if (args.length < 3)
        {
            System.err.println("Usage: <URL> <Depth> <Save Directory>");
            return;
        }
        
        String initAddr = args[0];                      // Initial crawl address
        int    maxDepth = Integer.parseInt(args[1]);    // Max crawl depth
        String savePath = args[2];                      // Save path
        
        DownloadRepository.getInstance();               // Initialize the singleton
        
        System.out.println("crawling...");
        
        WebPage init = new WebPage(initAddr);           // Initialize the web address
        init.crawl(maxDepth);                           // Begin the all-inclusive crawling

        DownloadRepository.getInstance().download(savePath);
        
        System.out.println("repository downloaded.");
    }
}
