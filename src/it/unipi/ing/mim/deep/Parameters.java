package it.unipi.ing.mim.deep;

import java.io.File;

public class Parameters {
		
	public Parameters(String DEEP_LAYER, int QUANTIZATION_FACTOR,int K) {
		this.DEEP_LAYER = DEEP_LAYER;
		this.QUANTIZATION_FACTOR = QUANTIZATION_FACTOR;
		this.K=K;
	}
	
	//DEEP parameters
	public static final String DEEP_PROTO = "data/caffe/train_val.prototxt";
	public static final String DEEP_MODEL = "data/caffe/bvlc_reference_caffenet.caffemodel";
	public static final String DEEP_MEAN_IMG = "data/caffe/meanImage.png";
	
	public static String DEEP_LAYER;
	
	public static final int IMG_WIDTH = 227;
	public static final int IMG_HEIGHT = 227;
	
	//Image Source Folder
	public static final File SRC_FOLDER = new File("data/img");
	public static final File GROUNDTRUTH = new File("data/groundtruth");
	

	//Features Storage File
	public static File STORAGE_FILE = new File("data/deep.seq."+DEEP_LAYER+".dat");
	
	//k-Nearest Neighbors
	public int K;

	
	//Lucene Index
	public static final String  LUCENE_PATH = "out/"  + "Lucene_Deep";
	
	//HTML Output Parameters
	public static final  String BASE_URI = "file:///" + Parameters.SRC_FOLDER.getAbsolutePath() + "/";
	public static final File RESULTS_HTML = new File("out/deep.seq.html");
	public static final File RESULTS_HTML_LUCENE = new File("out/deep.lucene.html");
	public static final File RESULTS_HTML_REORDERED = new File("out/deep.reordered.html");
	
	public int QUANTIZATION_FACTOR;

}
