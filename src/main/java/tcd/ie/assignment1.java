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

public class assignment1 {
    // Directory where search index will be saved
    private static String INDEX_DIRECTORY = "../index";

    public static void main(String[] args) throws IOException {
        // Analyser used to proceed TextField
        Analyzer analyzer = new StandardAnalyzer();

        // To store an index in memory
        // Directory directory = new RAMDirectory
        // To store an index on disk
        Directory directory = FSDirectory.open(Paths.get(INDEX_DIRECTORY));

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        // does not exist, otherwise opens it
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        IndexWriter iWriter = new IndexWriter(directory, config);

        // create new doc
        Document doc = new Document();
        doc.add(new TextField("super_name", "Spider-MAN1", Field.Store.YES));
        doc.add(new TextField("name", "Peter ParkER1", Field.Store.YES));
        doc.add(new TextField("category", "superheRO0", Field.Store.YES));

        // save doc to index
        iWriter.addDocument(doc);

        // commit changes and close
        iWriter.close();
        directory.close();
    }
}