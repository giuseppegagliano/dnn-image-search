package it.unipi.ing.mim.img.lucene;

import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.Parameters;
import it.unipi.ing.mim.deep.tools.FeaturesStorage;
import it.unipi.ing.mim.project5.Project5Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.FSDirectory;

public class LucImageIndexing {
	
	private List<ImgDescriptor> idsDataset;
	private IndexWriter indexWriter;
	private static Parameters params;
		
	
	public LucImageIndexing(File datasetFile, Parameters params) throws IOException, ClassNotFoundException {
		//load the dataset and the pivots
		this.idsDataset = FeaturesStorage.load(Parameters.STORAGE_FILE);
		this.params = params;
	}
	
	public void openIndex(String lucenePath) throws IOException {
		//initialize Lucene stuff
		Path absolutePath= Paths.get(lucenePath, "");
		FSDirectory index= FSDirectory.open(absolutePath);
		Analyzer analyzer= new WhitespaceAnalyzer();
		IndexWriterConfig conf= new IndexWriterConfig(analyzer);
		conf.setOpenMode(OpenMode.CREATE);
		
		//create the index
		indexWriter = new IndexWriter(index,conf);
	}
	
	public void closeIndex() throws IOException {
		//close Lucene writer
		indexWriter.close();
	}
	
	public void index() throws ClassNotFoundException, IOException {
		//LOOP
			//index all dataset features into Lucene
		for(int i = 0; i < idsDataset.size(); i++){
			ImgDescriptor imgDes = idsDataset.get(i);
			String imgTXT = Project5Main.features2Text(imgDes, params);
			Document d = createDoc(imgDes, imgTXT);
			indexWriter.addDocument(d);
			}
		//commit Lucene
		indexWriter.commit();
	}
	
	private Document createDoc(ImgDescriptor imgDes, String imgTXT) throws IOException{
		Document doc = new Document();
		
		// img field
	    FieldType ft = new FieldType(TextField.TYPE_STORED);
	    ft.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
	    ft.setStoreTermVectors(true);
	    ft.setStoreTermVectorPositions(true);
	    ft.storeTermVectorOffsets();
	    Field f = new Field(Fields.IMG, imgTXT, ft);    
	    doc.add(f);		
	    
		//ID field
		ft = new FieldType(StringField.TYPE_STORED);
		ft.setIndexOptions(IndexOptions.DOCS);
		f = new Field(Fields.ID,imgDes.getId(),ft);
		doc.add(f);

		//Create Fields.IMG and Fields.ID (and Fields.BINARY for the optional step) fields and add them in doc
	    return doc;
	}
	
}
