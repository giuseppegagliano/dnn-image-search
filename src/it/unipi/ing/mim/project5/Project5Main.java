package it.unipi.ing.mim.project5;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.queryparser.classic.ParseException;

import it.unipi.ing.mim.deep.DNNExtractor;
import it.unipi.ing.mim.deep.ImgDescriptor;
import it.unipi.ing.mim.deep.Parameters;
import it.unipi.ing.mim.deep.seq.SeqImageStorage;
import it.unipi.ing.mim.deep.tools.FeaturesStorage;
import it.unipi.ing.mim.img.lucene.LucImageSearch;
import it.unipi.ing.mim.project5.GroundTruthLists;


public class Project5Main {

	
	private static HashSet rL;
	private static HashSet resLuceneHS;

	public static void main(String[] args) throws Exception {
		
		HashMap<String, GroundTruthLists> groundTruthMap; 
		
		int KNN=10;
		String LAYER="fc6";
		int Q=30;
		Parameters params=new Parameters(LAYER,Q,KNN);
		groundTruthMap=GroundTruthExtractor(params);
		
		
		for(Object key:groundTruthMap.keySet()){
	
			//System.out.println("RELEVANT_"+key+groundTruthMap.get(key).getRelevantList());
			//System.out.println("AMBIGUOS_"+key+groundTruthMap.get(key).getAmbiguousList());
			
		}
		
		//DeepFeatureExtraction(params);
		Benchmark(groundTruthMap,params);
			
		
		
		System.exit(0);

	}
	
	
	private static void Benchmark(HashMap <String, GroundTruthLists> groundTruthMap,Parameters params) throws IOException, ClassNotFoundException, ParseException{
		
		File result=null;
		double 	map=0;
		int j=0;
		ArrayList<Double> precision=new ArrayList<Double>();
	
		for(Object key:groundTruthMap.keySet())
			{

			System.out.println("CHIAVE "+key);
			HashSet<String> resLuceneHS = searchQuery(params,(String)key);
			
			HashSet<String> rl = new HashSet<>(groundTruthMap.get(key).getRelevantList());
			HashSet<String> al = new HashSet<>(groundTruthMap.get(key).getAmbiguousList());
			
			//System.out.println("RANKED  "+j+resLuceneHS);
			
			
			System.out.println("AMBIGOUS_"+j+al);
			System.out.println("RELEVENATN_"+j+rl);
			
			
			precision.add(Project5Main.ComputeAvgPrecision(rl,al,resLuceneHS));
			
			System.out.println("AP: "+precision);
			map+=precision.get(j);
			j++;
			
			}
		
		map/=groundTruthMap.keySet().size();

		result=new File("out/MAP_layer"+params.DEEP_LAYER+"_Q_"+params.QUANTIZATION_FACTOR);
		//System.out.println("MAP is: ");
			string2File(String.valueOf(map), result);

	

}

	private static HashSet<String> searchQuery(Parameters params,String query) throws IOException, ClassNotFoundException,ParseException{
		
		LucImageSearch imgSearch = new LucImageSearch(params);
		System.out.println("Opening index...");
		imgSearch.openIndex(params.LUCENE_PATH);
		
		//Image Query File
		System.out.println("Building query for "+query);
		File imgQuery = new File(params.SRC_FOLDER, query);
		
		System.out.println("Extracting features...");
		float[] imgFeatures =  (new DNNExtractor()).extract(imgQuery,params.DEEP_LAYER);
		
		ImgDescriptor q = new ImgDescriptor(imgFeatures, imgQuery.getName());
				
		System.out.println("Searching...");
		List<ImgDescriptor> resLucene = imgSearch.search(q, params);
		
		HashSet resLuceneHS=new HashSet<String>();
		
		for(ImgDescriptor item:resLucene)	resLuceneHS.add(item.getId());
		
		return resLuceneHS;
		
	}
	
	private static double ComputeAvgPrecision(HashSet<String> relevantList, HashSet<String> ambiguousList, HashSet<String> rankedList){
		  double old_recall = 0.0;
		  double old_precision = 1.0;
		  double ap = 0.0;
		  
		  int intersect_size = 0;
		  int j = 0;
		  rankedList.add("bodleian_000356.jpg");
		  for (String ranked : rankedList) {
			  System.out.println("stringa "+ranked);
			  System.out.println("RELEVENT "+relevantList);
			  System.out.println("AMBIGUOS" +ambiguousList);
		    if (ambiguousList.contains(ranked)) continue;
		    if (relevantList.contains(ranked)) intersect_size++;
	
		   // System.out.println("FOTO IN COMUNE"+intersect_size);
		    
		    double recall = intersect_size / (double)relevantList.size();
		    double precision = intersect_size / (j + 1.0);
	
		    ap += (recall - old_recall)*((old_precision + precision)/2.0);
	
		    old_recall = recall;
		    old_precision = precision;
		    j++;
		  }
		  return ap;
		  }
	
	private static void DeepFeatureExtraction(Parameters params) throws IOException{
				System.out.println("Sequential image indexing...");
				SeqImageStorage indexing = new SeqImageStorage(params);
				List<ImgDescriptor> descriptors = indexing.extractFeatures(Parameters.SRC_FOLDER);
				System.out.println("Features extracted.");
				String srcName="data/deep.seq."+params.DEEP_LAYER+".dat";
				FeaturesStorage.store(descriptors, new File(srcName));
				System.out.println("File created.");
	}
	
	private static void string2File(String text, File file) throws IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file);
			fileWriter.append(text);
		} finally {
			if (fileWriter != null)
				fileWriter.close();
		}
	}

	public static String features2Text(ImgDescriptor imgF, Parameters params) throws ClassNotFoundException, IOException{
			  StringBuilder textual_features=new StringBuilder();
			  int integer_feature;
			  float[] features = imgF.getFeatures();
			  double feature;
			  for (int j=0;j<features.length;j++){
			   feature=features[j];
			   if(feature!=0){
			    feature*=params.QUANTIZATION_FACTOR;
			    integer_feature=(int) Math.floor(feature);
			    for(int i=0;i<integer_feature;i++){
			     textual_features.append("f"+j+" ");
			     }
			    }
			   }
			  return textual_features.toString();
			  }

public static HashMap GroundTruthExtractor(Parameters params) throws IOException{
	
	
	HashMap<String, GroundTruthLists> groundTruthMap; 
	groundTruthMap=new HashMap<String,GroundTruthLists>();
	
	String query=null;
	
	File[] files = (params.GROUNDTRUTH).listFiles();
	


	
	for(int i=0,j=0;i<files.length;i+=4,j++){
		
		
		ArrayList<String> relevantList=new ArrayList<String>();
		ArrayList<String> ambiguousList=new ArrayList<String>();
		
		System.out.println("nome FILE "+files[i]);
		
		try (BufferedReader br = new BufferedReader(new FileReader(files[i]))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		      relevantList.add(line+".jpg");
		    }
		}
		
		try (BufferedReader br = new BufferedReader(new FileReader(files[i+2]))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		      relevantList.add(line+".jpg");
		    }
		}
		
	
		try (BufferedReader br = new BufferedReader(new FileReader(files[i+1]))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		      ambiguousList.add(line+".jpg");
		    }
		}
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(files[i+3]))) {
		    String content;
		    while ((content = br.readLine()) != null) {

				content=(content.split(" "))[0];
				content=content.substring(5);
				content+=".jpg";
				query=content;
		    }
		}
		
		
		GroundTruthLists value=new GroundTruthLists(relevantList,ambiguousList) ; 
		groundTruthMap.put(query, value);
	
		
	
}
	
	System.out.println("NUMERO"+files.length);
	return groundTruthMap;
}



}


