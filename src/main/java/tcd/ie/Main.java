package tcd.ie;

import java.io.IOException;

import java.util.ArrayList;

import java.nio.file.Paths;
import java.nio.file.Files;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;

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
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select an analyzer:");
        System.out.println("1. Standard Analyzer");
        System.out.println("2. English Analyzer");
        System.out.print("Enter your choice (1, 2): ");
//        Analyzer analyzer = new StandardAnalyzer();

        int choice = scanner.nextInt();
        Analyzer analyzer;

        if (choice == 1) {
            analyzer = new StandardAnalyzer();
        }
        else if (choice == 2) {
            analyzer = new EnglishAnalyzer();
        }else {
            System.out.println("Invalid choice. Using the Standard Analyzer by default.");
            analyzer = new StandardAnalyzer();
        }

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
//                            System.out.println(title);
                            break;
                        case ".W":
                            // fix the spacing
                            textualDoc.append(line);
                            break;
                    }
                }
                line = lineReader.readLine();
            }
//            System.out.println(count);
//            System.out.println((author));

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