package wikipedia_stats

/** Spark imports */
import org.apache.spark.{HashPartitioner, SparkConf, SparkContext}
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel

/** Scala imports */
import scala.util.matching.Regex
import scala.collection.mutable.ListBuffer

/** Hadoop imports */
import org.apache.hadoop.io.LongWritable
import org.apache.hadoop.io.Text
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.conf.Configuration

/** Java imports */
import java.time.Instant
import java.time.ZoneId


object WikipediaStats {
 
     
     /** Represents a wikipedia article extracted from "<page>....</page>" 
     * 
     * @param aid: the unique id of this wikipedia article, indicated by "<id>...</id>"
     * @param title: the title of this article,  indicated by "<title>...</title>"
     * @param revisions: a list of revisions in this article, indicated by "<revision>....</revision>....<revision>....</revision>"
     */
    case class WikipediaArticle(aid: Long, title: String, revisions: List[ArticleRevision]){
      
      /** returns the number of revisions this article has */
      def revisionCount() : Int = revisions.length
   
      /** returns a list of contributors */
      def contributors() : List[String] = {
         revisions.map(rv => rv.contributor)
      }
           
      /** a helper method that returns a list of tuple(Item1, Item2)
       *  Item1: contributor
       *  Item2: the revision that contributor made
       */
     def contributorAndRevision(): List[(String,ArticleRevision)] = null
      
     /** a helper method that returns a list of years in which revisions were made */
     def revisionYears() : List[Int] = null
       
     override def toString() : String = {
         val buf = new StringBuilder
          buf ++= aid+","+title +"\n"
         for( revision <- revisions){
            buf ++= revision.toString 
         }        
         buf.toString()
      }
  
    }
    
    
    /** Represents an article revision extracted from "<page>...<revision>....</revision>...</page>"
     * 
     * @param rid: the id of this revision, indicated by <revision><id>...</id>....</revision>
     * @param contributor: a person or an ip address that contributes to this revision, 
     *                     indicated by "<username>...</username>" or "<contributor><ip>...</ip></contributor>"
     * @param timestamp: a timestamp in ISO8601 format, e.g. 2009-10-24T03:36:18Z (year-month-day hh:mm:ss)
     *                   where T indicates time, and Z is the zone designator for the zero UTC (Coordinated Universal Time) offset.
     *                   java.time.Instant is used to store this value                    
     */
    case class ArticleRevision(rid: Long, contributor:String, timestamp:Instant){
      
      /**returns the year in which this revision was made*/
      def revisionYear() : Int = timestamp.atZone(ZoneId.systemDefault).getYear()   
      override def toString(): String = "\t"+rid + "," + contributor +"," + timestamp+"\n"
 
    }
   
   
   /** a helper method that extracts elements of a given name, e.g. revision
    * 
    * @param elemXML: the xml of the parent element 
    * @param elemName: the element name to look for, e.g. "revision" in "<page>..<revision>...</revision><revision>...</revision>..</page>" 
	  * @return a list of elements with the given name, e.g. "[<revision>...</revision>,...,<revision>...</revision>]"
    */
  def extractElements(elemXML:String, elemName:String): List[String] = {    
      val regx = new Regex("(?s)(<"+elemName+"[^>]*?>.+?</"+elemName+">)");
      val mi = for (m <- regx.findAllMatchIn(elemXML)) yield m.group(1);
      mi.toList     
    }
    
  
   /** a helper method that extracts the text content of an element
    * 
    * @param elemXml: the xml of an element
    * @param elemName: the element name to look for, e.g. "title" in "<title>XYZ</title>" 
	  * @return the text content of the element, e.g. "XYZ" in "<title>XYZ</title>"
    */
   def extractElementText(elemXml:String, elemName:String): String = {    
      val regx = new Regex("(?s)<"+elemName+"[^>]*?>(.+?)</"+elemName+">");
      val m = regx.findFirstMatchIn(elemXml)
      m match {
           case Some(m) => m.group(1)
           case None => ""
       }
      
    }
    
    /** a helper method that parses "<page>....</page>" into a WikipediaArticle instance
     *  
     * @param page: the text of a wikipedia page, indicated by "<page>....</page>" 
	   * @return an instance of WikipediaArticle class  
     */     
   def parse(page: String): WikipediaArticle = {
    val title = extractElementText(page, "title");
    val aid  = extractElementText(page, "id");
    val aidLong = if (aid != "") aid.toLong else -1L
    val revisionElems = extractElements(page, "revision");
    val revisonListBuffer = ListBuffer[ArticleRevision](); 
    for( revisionElem <- revisionElems){
       var contributor = extractElementText(revisionElem, "username");
       if(contributor == "") contributor = extractElementText(revisionElem, "ip");
       val rid = extractElementText(revisionElem, "id");
       val ridLong = if (rid != "") rid.toLong else -1L
       val timestamp =  Instant.parse(extractElementText(revisionElem, "timestamp"));
        revisonListBuffer += ArticleRevision(ridLong, contributor, timestamp)
    }  
 
    WikipediaArticle(aidLong, title, revisonListBuffer.toList)
  }
   
  
  
   /** a helper method that reads in files  
    *  
    * @param path: the path to the wikipedia files 
    * @param sc: a spark context
	  * @return a RDD that contains a collection of page strings, i.e., "<page>...</page>"
    */    
  def readInWikipediaPages(path:String, sc:SparkContext):RDD[String] ={
     val hadoopConf = new Configuration()
     //use Hadoop TextInputFormat to read in wikipedia files that allows us to chop files into individual pages by using the delimiter "</page>"
     hadoopConf.set("textinputformat.record.delimiter", "</page>")
     //pages is a pair RDD[(K,V)] with LongWritable and Text as Key and Value type correspondingly, i.e. RDD[(LongWritable,Text)] 
     val pages = sc.newAPIHadoopFile(path, classOf[TextInputFormat], classOf[LongWritable], classOf[Text], hadoopConf)
      //we are only interested in the value (i.e. page content) and return a RDD[String]  
      pages.map(x=> x._2.toString).filter(s=>s!="").map(s=>s+"</page>")
   }
  
   /**a helper method that transforms RDD[String] to RDD[WikipediaArticle]  
    * 
    * @param path: the path to the wikipedia files 
    * @param sc: a spark context
	  * @return a RDD that contains a collection of WikipediaArticle instances 
    */ 
   def generateWikipediaRdd(path: String,sc:SparkContext): RDD[WikipediaArticle] = {     
       readInWikipediaPages(path, sc).map(x=>parse(x)).filter(wa=>wa.revisions.size >=1)
    }
   
   
    val timing = new StringBuffer
    /**a helper method that record the execution time of a piece of code**/ 
    def timed[T](label: String, code: => T): T = {
        val start = System.currentTimeMillis()
        val result = code
        val stop = System.currentTimeMillis()
        timing.append(s"Processing $label took ${stop - start} ms.\n")
        result
      }
    
        
   def main(args: Array[String]) {    
   //Start the Spark context
    val sparkConf = new SparkConf()
      .setAppName("wikipedia stats")
      .setMaster("local") // run in local mode
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer") //a better serializer than that of Java
      //a handle to the Spark framework
      val sc = new SparkContext(sparkConf)
      val waRdd: RDD[WikipediaArticle] = timed("generateWikipediaRdd", generateWikipediaRdd(args(0),sc))

     //waRdd.take(1).foreach(println)
      println(timing)

     val persistedWaRdd = waRdd.persist()
      //test the functions and generate answers to questions here
     val q1 = numOfArticlesAndRevsions(persistedWaRdd);
     val q2 = numOfUniqueContributors(persistedWaRdd);
     val q3 = yearsWikipediaArticlesCreated(persistedWaRdd);
     val q4 = numOfArticlesWithMinRevisionsAndMinContributors(persistedWaRdd, 100, 10);
     val q5 = sortArticlesByNumRevisions(persistedWaRdd).take(3);
     val q6 = sortContributorsByNumRevisions(persistedWaRdd).take(3);


     val q9 = contributorAndNumRevisionsPerYear(groupArticleRevisionsByYear(persistedWaRdd));


     println("Q1. How many wikipedia articles and revisions are in wikipedia_stats.wikipedia_meta_history1?");
      println(">" + q1.toString());

     println("Q2. How many unique contributors in wikipedia_stats.wikipedia_meta_history1?");
     println(">" + q2.toString());

     println("Q3.What are the years in which the wikipedia articles were created(i.e.the first revision) in wikipedia_stats.wikipedia_meta_history1 ?")
     println(">" + q3.mkString(", "));

     println("Q4.How many wikipedia articles that have at least 100(inclusive) revisions and were contributed by at least 10(inclusive) different contributors in wikipedia_stats.wikipedia_meta_history1 ?")
     println(">" + q4.toString());

     println("Q5.What are the top three wikipedia articles that have the largest number of revisions in wikipedia_stats.wikipedia_meta_history1 ?")
     println(">" + q5.foreach(print));

     println("Q6.Who are the top three contributors that have made the largest number of revisions in wikipedia_stats.wikipedia_meta_history1 ?")
     println(">" + q6.foreach(print));

     println("Q7. How many revisions were made in 2014 in wikipedia_stats.wikipedia_meta_history1?")
     println("Q8. How many unique contributors have made revisions in 2014 in wikipedia_stats.wikipedia_meta_history1?")
     testLookupYearGroupedArticleRevisions(groupArticleRevisionsByYear(persistedWaRdd));

     println("Q9.How many revisions were made by a contributor called \"Magioladitis\" in 2013 in wikipedia_stats.wikipedia_meta_history1?")
     println(">" + q9.filter(_._1 == 2013).take(1).foreach(obj => println(obj._2("Magioladitis"))));

     val waRdd2: RDD[WikipediaArticle] = timed("generateWikipediaRdd", generateWikipediaRdd(args(1),sc))
     println("Q10.How many contributors who have contributed to both wikipedia_stats.wikipedia_meta_history1 and wikipedia_stats.wikipedia_meta_history2 ?")
     println("Q11.How many contributors who have contributed to both wikipedia_stats.wikipedia_meta_history1 and wikipedia_stats.wikipedia_meta_history2 in 2013 ?")
     testFilterContributorCogroupedDatasets(waRdd, waRdd2)

     sc.stop()
  } 
   
   
    /**calculates the total number of wikipedia articles and revisions
     * 
     * @param waRdd: a RDD of WikipediaArticle instances    
     * @return a tuple(Item1, Item2) 
     * Item1: the total number of revisions 
     * Item2: the total number of articles 
     */
    def numOfArticlesAndRevsions(waRdd: RDD[WikipediaArticle]): (Int,Int) = {
      val numArticles = waRdd.count();
      val numRevisions = waRdd.map(obj => obj.revisionCount()).sum()
      (numArticles.toInt, numRevisions.toInt)
    }

    
    /**calculates the total number of unique contributors
     * 
     * @param waRdd a RDD of WikipediaArticle instances  
     * @return the number of unique contributors
     */
    def numOfUniqueContributors(waRdd: RDD[WikipediaArticle]): Long = {
      waRdd.flatMap(obj => obj.contributors()).distinct().count();
    }
  
     /** produces a list of years in which wikipedia articles were created (the year of the first revision)
      * 
      * @param waRdd : a RDD of WikipediaArticle instances  
      * @return a list of unique years in which wikipedia articles were created (e.g. the first revision)  
      */
     def yearsWikipediaArticlesCreated(waRdd: RDD[WikipediaArticle]): List[Int] = {
       waRdd.map(article => article.revisions.head.revisionYear()).distinct().collect().toList;
     }
    
     
    /**calculates the number of wikipedia articles that have at least a certain number of revisions 
     *  and were contributed by at least a certain number of different contributors
     * 
     * @param waRdd:  a RDD of WikipediaArticle instances    
     * @param minRevisions: the minimum number of revisions
     * @param minContributors: the minimum number of different contributors
     * @return the number of articles that have at least minRevisions number of revisions (inclusive) 
     *        and at least minContributors number of unique contributors (inclusive) 
     */
    def numOfArticlesWithMinRevisionsAndMinContributors(waRdd: RDD[WikipediaArticle], minRevisions:Int, minContributors:Int): Long = {
      waRdd.filter(obj => obj.revisionCount() >= minRevisions && obj.contributors().distinct.size >= minContributors).count();
    }
   
    
    /** generates a pair RDD of tuples of the title of an article and the number of revisions it has, 
     *  sorted by the number of revisions in a descending order
     * 
     * @param waRdd:  a RDD of WikipediaArticle instances 
     * @Return a pair RDD[(K,V)], sorted by V (the number of revisions) in a descending order 
     * K: title of an article, 
     * V: the number of revisions 
     */
    def sortArticlesByNumRevisions(waRdd: RDD[WikipediaArticle]): RDD[(String, Int)]= {
      waRdd.map(obj=> (obj.title, obj.revisionCount())).sortBy(_._2, false);
    }
    
  /** generates a pair RDD of tuples of the contributor and the number of revisions the contributor made,
   *  sorted by the number of revisions in a descending order
   * 
   * @param waRdd:  a RDD of WikipediaArticle instances  
   * @return a pair of RDD[(K,V)] , sorted by V (the number of revisions) in a descending order
   * K: contributor
   * V: the number of revisions the contributor made                     
   */ 
  def sortContributorsByNumRevisions(waRdd: RDD[WikipediaArticle]): RDD[(String, Int)]= {
    waRdd.flatMap(obj => obj.revisions.map(rv => (rv.contributor, 1))).reduceByKey(_+_).sortBy(_._2, false)
  }
  
     
   /**generates a pair RDD of tuples of the year of revision and an Iterable of ArticleRevisions made in that year 
    * 
   * @param waRdd:  a RDD of WikipediaArticle instances  
   * @return a pair RDD[(K,V)]
   * K: the year of the revision 
   * V: an Iterable of ArticleRevsions made in that year
   */ 
  def groupArticleRevisionsByYear(waRdd: RDD[WikipediaArticle]): RDD[(Int, Iterable[ArticleRevision])]= {
    waRdd.flatMap(obj => obj.revisions.map(rv => (rv.revisionYear(), rv))).groupByKey()
  }
   
    
  
   /**generates a pair RDD of tuples of the year of revision and the integer result returned by the function passed in as an argument
    * 
    * @param yearGroupedRdd is a pair RDD of tuple of the year of the revision and an Iterable of ArticleRevisions
    * @param func is a function that takes an Iterable of ArticleRevisions as a parameter and returns an integer result 
    * @return a pair RDD[(K,V)]
    * K: the year of the revision
    * V: the result of func
    */
   def lookupYearGroupedArticleRevisions(yearGroupedRdd: RDD[(Int, Iterable[ArticleRevision])], func: (Iterable[ArticleRevision])=>Int): RDD[(Int, Int)]= {
     yearGroupedRdd.map(obj=>(obj._1, func(obj._2)))
   }

   
   /**uses groupArticleRevisionsByYear and lookupYearGroupedArticleRevisions 
    * to answer questions Q7-Q8
    * 
    * @param yearGroupedRdd is a pair RDD of tuple of the year of the revision and an Iterable of ArticleRevisions 
    *
    */
   def testLookupYearGroupedArticleRevisions(yearGroupedRdd: RDD[(Int, Iterable[ArticleRevision])]): Unit ={
     val persistedYG = yearGroupedRdd.persist(StorageLevel.MEMORY_ONLY);
       //a function to calculate the number of revisions 
       val totalArticlesFunc: (Iterable[ArticleRevision])=>Int = {
         _.size;
       }

       //a function to calculate the number of unique contributors
       val totalContributorsFunc: (Iterable[ArticleRevision])=>Int = {
         _.map(_.contributor).toSet.size;
       }

     persistedYG.map(rdd => (rdd._1, totalArticlesFunc(rdd._2))).filter(_._1 == 2014).reduceByKey(_+_).foreach(println);
     persistedYG.map(rdd => (rdd._1, totalContributorsFunc(rdd._2))).filter(_._1 == 2014).reduceByKey(_+_).foreach(println);
    }
   
   /**generates a pair RDD of tuples of the revision year and a map of contributor and the number of revisions that contributor made
    * 
   * @param yearGroupedRdd is a pair RDD of tuple of the year of the revision and an Iterable of ArticleRevisions
   * @return A pair RDD[(K1, V1)]
   * K1:  the year of the revision 
   * V1: Map[k2,v2]
   * k2: contributor
   * v2: the number of revisions that contributor made
   */
   def  contributorAndNumRevisionsPerYear(yearGroupedRdd: RDD[(Int, Iterable[ArticleRevision])]): RDD[(Int, Map[String,Int])] = {
     //Hint: use groupArticleRevsionsByYear to get yearGroupedRdd
     yearGroupedRdd.mapValues(revisions => {
       revisions.groupBy(_.contributor).mapValues(_.size)
     })
     }

   
   /**A helper method that generates a pair RDD of (contributor, ArticleRevision), partitioned by contributor
    * 
    * @param waRdd:  a RDD of WikipediaArticle instances 
    * @return a pair RDD[(K, V)], partitioned by K (contributor)
    * K: contributor 
    * V: the revision made by the contributor
    */
   def generateContributorPatitionedRdd(waRdd: RDD[WikipediaArticle], numPartitions:Int): RDD[(String,ArticleRevision)] = {
     waRdd.flatMap(wa => wa.revisions.map(rev => (rev.contributor, rev))).partitionBy(new HashPartitioner(numPartitions))
   }

     
     
    /** a helper method that cogroups two co-partitioned pair RDD of (contributor, ArticleRevision),  
     *  e.g., generated using the generateContributorPatitionedRdd method
     *  
     *  @param waRdd1: a RDD of WikipediaArticle instances created from  wikidepia_meta_history1
     *  @param waRdd2: a RDD of WikipediaArticle instances created from  wikidepia_meta_history2
     *  @return a pair RDD[(K,V)]
     *  K: contributor
     *  V: (V1,V2)
     *  V1: an iterable of ArticleRevisions from waRdd1 (wikidepia_meta_history1)
     *  V2: an iterable of ArticleRevisions from waRdd2 (wikidepia_meta_history2)
     *  
     */
     def cogroupTwoDatasetsByContributor(waRdd1: RDD[WikipediaArticle], waRdd2: RDD[WikipediaArticle]): RDD[(String,(Iterable[ArticleRevision], Iterable[ArticleRevision]))] = {
       //you may want to use generateContributorPatitionedRdd in this method
       generateContributorPatitionedRdd(waRdd1, 8).cogroup(generateContributorPatitionedRdd(waRdd2, 8))
     }
   
    
     
     /** generates a subset of RDD using a filter predicate passed in as an argument 
      *  
      * @param contributorCogroupedRdd: a pair RDD[(K,V)]
      *         K: contributor
      *         V: (V1,V2) 
      *         V1: an iterable of ArticleRevisions from waRdd1 (wikidepia_meta_history1)
      *         V2: an iterable of ArticleRevisions from waRdd2 (wikidepia_meta_history2)
      * @param  filterPred: a predicate T => Boolean         
      *         T: an iterable of ArticleRevisions
      *         
      * @return a subset of contributorCogroupedRdd after being filtered by filterPred 
      */  
     def filterContributorCogroupedDatasets(contributorCogroupedRdd:RDD[(String,(Iterable[ArticleRevision], Iterable[ArticleRevision]))],
         filterPred: (Iterable[ArticleRevision]) => Boolean): RDD[(String,(Iterable[ArticleRevision], Iterable[ArticleRevision]))] = {
       contributorCogroupedRdd.filter(obj => filterPred(obj._2._1) && filterPred(obj._2._2))
     }
     
     
         
         
   /**uses cogroupTwoDatasetsByContributor and filterContributorCogroupedDatasets 
    * to answer questions Q10-Q11
    * 
    * @param waRdd1: a RDD of WikipediaArticle instances created from  wikidepia_meta_history1
    * @param waRdd2: a RDD of WikipediaArticle instances created from  wikidepia_meta_history2
    *
    */    
     def testFilterContributorCogroupedDatasets(waRdd1: RDD[WikipediaArticle], waRdd2: RDD[WikipediaArticle]): Unit={
     
       //a Predicate that tests the Iterable is not empty 
       val notEmptyPredic : (Iterable[ArticleRevision]) => Boolean  = {
         _.size > 0
       }
       
       //A Predicate that tests there is at least one revision made in 2013 
       val inYearPredic : (Iterable[ArticleRevision]) => Boolean  = {
         _.exists(_.revisionYear() == 2013)
       }

       val cogroupedFiles = cogroupTwoDatasetsByContributor(waRdd1, waRdd2)
       val allBothContrib =  filterContributorCogroupedDatasets(cogroupedFiles, notEmptyPredic).persist(StorageLevel.MEMORY_ONLY)

       println(allBothContrib.count())
       println(filterContributorCogroupedDatasets(allBothContrib, inYearPredic).count())
   }
        
         
}
