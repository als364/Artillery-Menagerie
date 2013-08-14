package edu.cornell.slicktest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.json.JSONArray;
import org.json.JSONObject;

public class Compressor {
	public static byte[] compress(String data) throws UnsupportedEncodingException {
	    byte[] dataByte = data.getBytes();
	    //System.out.println("Compression Demo");
	    System.out.println("Actual Size of String : " + dataByte.length);
	    Deflater def = new Deflater();
	    def.setLevel(Deflater.BEST_COMPRESSION);
	    def.setInput(dataByte);
	    def.finish();
	    ByteArrayOutputStream byteArray = new ByteArrayOutputStream(dataByte.length);
	    byte[] buf = new byte[1024];
	    while (!def.finished()) {
	      int compByte = def.deflate(buf);
	      byteArray.write(buf, 0, compByte);
	    }
	    try {
	      byteArray.close();
	    }
	    catch (IOException ioe) {
	      System.out.println("When we will close straem error : " + ioe);
	    }

	    byte[] comData = byteArray.toByteArray();

	    System.out.println("Compressed  size of String : " + comData.length);
	    
	    return comData;
	  }
	
	 public static String decompress(byte[] compressedData) throws Exception {
		    Inflater decompressor = new Inflater();
		    decompressor.setInput(compressedData);
		    ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
		    byte[] buf = new byte[1024];
		    while (!decompressor.finished()) {
		      int count = decompressor.inflate(buf);
		      bos.write(buf, 0, count);

		    }
		    bos.close();
		    byte[] decompressedData = bos.toByteArray();
		    return new String(decompressedData);

		  }
	
	public static void main(String args[]) throws Exception {
		JSONArray array = new JSONArray();
		for (int i = 0; i < 1000; i++) {
		JSONObject object = new JSONObject();
		object.put("abc", "abc" + i);
		object.put("123", 123 + i);
		object.put("armies", new JSONArray());
		array.put(object);
		}
		String str = array.toString();
		byte[] compressed = compress(str);
		String data = decompress(compressed);
		
		JSONObject object2 = new JSONObject();
		object2.put("bytes", compressed);
		System.out.println(object2);
		System.out.println(data);
	}
}
