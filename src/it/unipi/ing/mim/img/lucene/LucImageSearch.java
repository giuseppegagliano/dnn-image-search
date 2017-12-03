package it.unipi.ing.mim.img.lucene;

import it.unipi.ing.mim.deep.DNNExtractor;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.Parameters;
import it.unipi.ing.mim.deep.tools.FeaturesStorage;
import it.unipi.ing.mim.deep.tools.Output;
import it.unipi.ing.mim.project5.Project5Main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;


public class LucImageSearch {

	private IndexSearcher indexSearcher;
	private Parameters params;
	private static final String DEEP_LAYER="fc7";
	private static final int K=10;
	

	public LucImageSearch(Parameters params) throws ClassNotFoundException, IOException {
		//Initialize fields
		this.params = params;
	}

	public void openIndex(String lucenePath) throws IOException {	
		//Initialize Lucene stuff
		Path absolutePath = Paths.get(lucenePath, ""); 
		FSDirectory index = FSDirectory.open(absolutePath); 
		DirectoryReader ir = DirectoryReader.open(index); 
		indexSearcher = new IndexSearcher(ir);
	}
	
	public List<ImgDescriptor> search(ImgDescriptor queryF, Parameters params) throws ParseException, IOException, ClassNotFoundException{
		
		List<ImgDescriptor> res = new ArrayList<ImgDescriptor>(params.K);
		
		//convert queryF to text
		String query = Project5Main.features2Text(queryF, params);
		BooleanQuery.setMaxClauseCount(10000);
		//System.out.println("QUERY MODIFICATA"+query);
		QueryParser p = new QueryParser(Fields.IMG, new WhitespaceAnalyzer());
		Query q = p.parse(query);
		//perform Lucene search
		TopDocs hits = indexSearcher.search(q, params.K);
		DNNExtractor extractor = new DNNExtractor();
		//LOOP
			//for each result reconstruct the ImgDescriptor, set the dist and add it to res
		for(int i = 0; i < params.K; i++){
			String img = indexSearcher.doc(hits.scoreDocs[i].doc).get(Fields.ID);
			String path = (Parameters.SRC_FOLDER.getAbsolutePath() + "/" + img);
			File f = new File(path);
			float[] fl = extractor.extract(f, params.DEEP_LAYER);
			ImgDescriptor imgdes = new ImgDescriptor(fl, indexSearcher.doc(hits.scoreDocs[i].doc).get(Fields.ID));
			double d = imgdes.distance(queryF);
			imgdes.setDist(d);
			res.add(imgdes);
		}
		
		return res;
	}
	
	public List<ImgDescriptor> reorder(List<ImgDescriptor> res) throws IOException, ClassNotFoundException {
		Collections.sort(res, ImgDescriptor.ImgDescriptorComparator);
		return res;
	}
	
	
	
}
