ABOUT:
  This program was developed in my Software Engineering Foundations course (CPSC215).

  The program will start on a certain website, and crawl through all links on the page, continuing the process through the crawled pages until the specified depth has been reached. During this process, all web files/pictures will be downloaded to a local repository.

USAGE:
  <URL> <Depth> <Relative Save Path>
  
  E.x.: http://www.clemson.edu/ 3 save/

  This will begin crawling at http://www.clemson.edu/, and crawl three additional times, saving all files and images to ./save/
