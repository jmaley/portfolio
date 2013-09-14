/*
 * Title: DownloadRepositoryInterface.java
 * Name:  Joe Maley
 * Date:  March 8, 2012
 * 
 * Description: Provides an interface for a Download Repository
 */

public interface DownloadRepositoryInterface
{ 
    /*
     * Adds all of the files & images in the webPage into
     * the current repository
     */
    public void addWebElements(WebPage webPage);
    
    /*
     * Saves all files & images in the repository to disk
     */
    public void download(String savePath);
}
