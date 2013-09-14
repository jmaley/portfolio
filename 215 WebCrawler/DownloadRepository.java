import java.util.LinkedList;

/*
 * Title: DownloadRepository.java
 * Name:  Joe Maley
 * Date:  March 8 2012
 * 
 * Description: Keeps a list of files and images to save at the end of crawling
 */

public class DownloadRepository implements DownloadRepositoryInterface
{
    private static DownloadRepository instance;
    
    private LinkedList<WebFile>  files;
    private LinkedList<WebImage> images;
    
    private DownloadRepository()
    {
        files  = new LinkedList<WebFile>();
        images = new LinkedList<WebImage>();
    }
    
    public static DownloadRepository getInstance()
    {
        if (instance == null)
            instance = new DownloadRepository();
        return instance;
    }

    public void addWebElements(WebPage webPage)
    {
        for (WebFile webFile : webPage.getFiles())
            files.add(webFile);
                
        for (WebImage webImage : webPage.getImages())
            images.add(webImage);
    }

    public void download(String savePath)
    {
        for (WebFile webFile : files)
            webFile.save(savePath);
                
        for (WebImage webImage : images)
            webImage.save(savePath);

    }
}
