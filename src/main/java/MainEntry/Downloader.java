package MainEntry;

import org.openqa.selenium.chrome.ChromeDriver;
import Utils.Parser;



public class Downloader {

	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//选择插件、输入参数
		
		if(args.length < 2) {
			System.out.println("Usage: java -jar TCdownload.jar -n <需要下载的样本数量> -p <样本下载后的存储路径>");
		}else {
			int sampleNumber=0;
			String DefaultDownloadPath = "c:\\Users\\xiongchaochao\\Desktop\\WordFolder\\MachineLearning\\GreatAPK";
			for (int i=0; i<args.length;i++) {
				if (args[i].equals("-n") && (Integer.valueOf(args[i+1]) instanceof Integer)) {
					sampleNumber = Integer.valueOf(args[i+1]);
				}else if(args[i].equals("-p")){
					DefaultDownloadPath = args[i+1];
				}
			}
			Parser.parser("http://zhushou.360.cn/list/index/cid/1/?page=", sampleNumber, DefaultDownloadPath);
		}
			
	}

}
