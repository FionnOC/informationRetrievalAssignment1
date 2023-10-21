package tcd.ie;

import java.io.*;

import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Files;

import java.util.Scanner;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.print.Doc;

public class Main {
    private static String INDEX_DIRECTORY = "index";
    private static String CRAN_ALL_1400 = "corpus/cran.all.1400";
    private static String CRAN_QUERY = "corpus/cran.qry";

    private static int MAX_RESULTS = 50;

    public static void main(String[] args) throws IOException, ParseException{
        // Analyzer that is used to process TextField
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select an analyzer:");
        System.out.println("1. Standard Analyzer");
        System.out.println("2. English Analyzer");
        System.out.print("Enter your choice (1, 2): ");
        Analyzer analyzer;

        int choice = scanner.nextInt();
//        Analyzer analyzer = new StandardAnalyzer();
//
        if (choice == 1) {
            analyzer = new StandardAnalyzer();
        }
        else if (choice == 2) {
            analyzer = new EnglishAnalyzer();
        }else {
            System.out.println("Invalid choice. Using the Standard Analyzer by default.");
            analyzer = new StandardAnalyzer();
        }
        System.out.println("Select a similarity metric:");
        System.out.println("1. Vector Space Model");
        System.out.println("2. BM25");
        System.out.print("Enter your choice (1, 2): ");

        choice = scanner.nextInt();

        // To store an index in memory
        // Directory directory = new RAMDirectory();
        // To store an index on disk

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        if (choice == 1) {
            config.setSimilarity(new ClassicSimilarity());
        }
        else if (choice == 2){
            config.setSimilarity(new BM25Similarity());
        } else {
            System.out.println("Invalid choice. Using VSM by default.");
            config.setSimilarity(new ClassicSimilarity());
        }
        // Index opening mode
        // IndexWriterConfig.OpenMode.CREATE = create a new index
        // IndexWriterConfig.OpenMode.APPEND = open an existing index
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND = create an index if it
        // does not exist, otherwise it opens it

        createIndex(config);
        queryIndex(analyzer);

//        scanner.close();

    }

    public static void queryIndex(Analyzer analyzer) throws IOException, ParseException {
        // Open the folder that contains our search index
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        BufferedReader lineReader = new BufferedReader(new FileReader(CRAN_QUERY));

        // create objects to read and search across the index
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);

        FileWriter fileWriter = new FileWriter("results/query_results.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        StringBuilder queryResultBuilder = new StringBuilder();

        String line = lineReader.readLine();

        int queryID = 0;
        String status = "";

        // go through until end of file
        while (line != null) {
            // new query
            StringBuilder queryTextBuilder = new StringBuilder();
            // If new document starting
            if (line.startsWith(".I")) {
                // add the id to the doc
                queryID++;
                String id_string = Integer.toString(queryID);
                // ok check next line and see if it is .T for title
                line = lineReader.readLine();

            }
            while (line != null && !(line.startsWith(".I"))) {
                if (line.startsWith(".W")) {
                    line = lineReader.readLine();
                }
                queryTextBuilder.append(" ");
                queryTextBuilder.append(line);
                line = lineReader.readLine();
            }

            QueryParser parser = new QueryParser("text", analyzer);

            // needed to replace "?" as they were causing errors
            String queryText_before = queryTextBuilder.toString();
            String queryText = queryText_before.replace("?", "");

            // now query it
            if (!queryText.isEmpty()) {
                // parse the query with the parser
                Query query = parser.parse(queryText);

                // Get set of results
                ScoreDoc[] hits = isearcher.search(query, MAX_RESULTS).scoreDocs;

                // need to save the results to a file
                queryResultBuilder.setLength(0);

                for (int i = 0; i < hits.length; i++) {
                    int docId = hits[i].doc;
                    Document doc = ireader.document(docId);

                    String resultLine = queryID + " Q0 " + doc.get("id") + " " + (i + 1) + " " + hits[i].score + " STANDARD\n";
                    queryResultBuilder.append(resultLine);

                }

                bufferedWriter.write(queryResultBuilder.toString());

            }

        }
        bufferedWriter.close();
    }

    public static void createIndex(IndexWriterConfig config) throws IOException {
        // logic for importing
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriter iwriter = new IndexWriter(directory, config);

        BufferedReader lineReader = new BufferedReader(new FileReader(CRAN_ALL_1400));
        String line = lineReader.readLine();
        String status = "";
        int count = 0;

        // Files.lines(Paths.get(CRAN_ALL_1400)).forEach(line -> {
        while ((line) != null) {
            Document doc = new Document();
            String id;
            StringBuilder title = new StringBuilder();
            StringBuilder author = new StringBuilder();
            StringBuilder textualDoc = new StringBuilder();
            // If new document starting
            if (line.startsWith(".I")) {
                // add the id to the doc
                id = line.substring(3).trim();
                doc.add((new StringField("id", id, Field.Store.YES)));
                // ok check next line and see if it is .T for title
                line = lineReader.readLine();
                status = ".I";
            }
            while (line != null && !(line.startsWith(".I"))) {

                if (line.startsWith((".T"))) {
                    status = ".T";
                    line = lineReader.readLine();
                    title.append(line);
                } else if (line.startsWith((".A"))) {
                    status = ".A";
                    line = lineReader.readLine();
                    author.append(line);
                } else if (line.startsWith(".B")) {
                    status = ".B";
                    line = lineReader.readLine();
                } else if (line.startsWith(".W")) {
                    status = ".W";
                    line = lineReader.readLine();
                    textualDoc.append(line);
                } else {
                    switch (status) {
                        case ".T":
                            title.append(line);
                            break;
                        case ".W":
                            textualDoc.append(line);
                            break;
                    }
                }
                line = lineReader.readLine();
            }

            doc.add(new TextField("title", title.toString(), Field.Store.YES));
            doc.add(new TextField("author", author.toString(), Field.Store.YES));
            doc.add(new TextField("text", textualDoc.toString(), Field.Store.YES));

            iwriter.addDocument(doc);
            count++;

        }
        iwriter.close();
        directory.close();

    }

}