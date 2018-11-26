package Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class getMD5FromSampleDirectory {

	private static ArrayList<String> md5list;
	
	
	public static ArrayList<String> getAllMD5(String path) {
		md5list = new ArrayList<String>();
		File[] filelist = new File(path).listFiles();
		if(filelist == null) {
			return null;
		}
		for(int i=0; i<filelist.length; i++) {
			if(filelist[i].getName().length() > 32) {
				md5list.add(filelist[i].getName().substring(0, 32));
			}
		}
		//给md5列表去重
		md5list = new ArrayList<String>(new HashSet<String>(md5list));
		return md5list;
	}
}
