package tcd.ie;

import java.io.IOException;

import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import javax.print.Doc;

public class Main {
    private static String INDEX_DIRECTORY = "index";
    private static String CRAN_ALL_1400 = "corpus/cran.all.1400";

    public static void main(String[] args) throws IOException {
        // Analyzer that is used to process TextField
        Analyzer analyzer = new StandardAnalyzer();

        // To store an index in memory
        // Directory directory = new RAMDirectory();
        // To store an index on disk

        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // Index opening mode
        // IndexWriterConfig.OpenMode.CREATE = create a new index
        // IndexWriterConfig.OpenMode.APPEND = open an existing index
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND = create an index if it
        // does not exist, otherwise it opens it

        createIndex(config);

        // Commit changes and close everything

    }

    public static void createIndex(IndexWriterConfig config) throws IOException {
        // logic for importing
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));
        IndexWriter iwriter = new IndexWriter(directory, config);

        Files.lines(Paths.get(CRAN_ALL_1400)).forEach(line -> {
            try {
                if (line.startsWith(".I")) {
                    Document doc = new Document();
                    String id = line.substring(3).trim();
                    doc.add((new StringField("id", id, Field.Store.YES)));

                    // as long as the next line is not .I

                }

            }
            catch(IOException e) {
               e.printStackTrace();
            }
        });





        // loop to go through and sort documents
//        for (int i = 0; i < 5; i++) {
        while () {
            System.out.printf("Indexing ...\n");

            // define title, Author, and text
//            String id = new String();
//            String title = new String();
//            String author = new String();
//            String text = new String();
//            // just ignore the bibliography
//
//            // read in line
//            // if .I
//                // Document doc = new Document()
//                // doc.add (spiderman example)
//
//            // if t
//                //
//
//            // else if .A do this
//
//            // else if .B read next line
//
//            // else if .W that is text
//
//
//            iwriter.addDocument(doc);

        }
        iwriter.close();
        directory.close();
    }




}