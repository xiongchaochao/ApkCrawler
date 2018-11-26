package Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Parser {
	private static ArrayList<String> AppSID;
	private static ChromeDriver chromeDriver;
	private static ArrayList<String> dlAddress;
	private static ArrayList<String> APKSmd5;
	private static ArrayList<String> AppName;
	
	
	
	public static void parser(String url, int Apknumber, String DefaultAPKCollectionPath) {
		AppSID = new ArrayList<String>();
		dlAddress = new ArrayList<String>();
		APKSmd5 = new ArrayList<String>();
		AppName = new ArrayList<String>();
		int page = 1;
		//chrome驱动位置
		String chromedriverpath = System.getProperty("user.dir") + "\\chromedriver.exe";
		System.setProperty("webdriver.chrome.driver", chromedriverpath);
		chromeDriver = new ChromeDriver();
		
		while(Apknumber>0) {
			chromeDriver.get(url + String.valueOf(page));
			WebElement ulElement = chromeDriver.findElement(By.id("iconList"));
			List<WebElement> liElements = ulElement.findElements(By.tagName("li"));
			for(int i=0; i<liElements.size(); i++) {
				String sid = liElements.get(i).findElement(By.tagName("a")).getAttribute("sid");
				AppSID.add(sid);
			}
			//根据应用的大小有选择的下载
			if(AppSID.size() > 0) {
				for(int i=0; i<AppSID.size(); i++) {
					String newUrl = "http://zhushou.360.cn/detail/index/soft_id/" + (String)AppSID.get(i);
					chromeDriver.get(newUrl);
					WebElement h1Element = chromeDriver.findElementByXPath("//h2[@id='app-name']/span[1]");
					AppName.add(h1Element.getText());
					WebElement spanElement = chromeDriver.findElementByXPath("//div[@class='pf']/span[4]");
					Double APKsize = Double.valueOf(spanElement.getText().replace("M", ""));
					if(APKsize <20) {
						String href = chromeDriver.findElementByXPath("//dl[@class='clearfix']/dd/a").getAttribute("href");
						String downloadAD = href.split("url=")[1];
						String md5 = downloadAD.substring(downloadAD.lastIndexOf("/")-32, downloadAD.lastIndexOf("/"));
						APKSmd5.add(md5);
						dlAddress.add(downloadAD);
						ArrayList<String> md5list = getMD5FromSampleDirectory.getAllMD5(DefaultAPKCollectionPath);
						if(md5list.contains(md5)) {
							continue;
						}
						Apknumber--;
						if(Apknumber==0) {
							break;
						}
					}
				}
			}
		}

		//去重
		dlAddress = new ArrayList<String>(new HashSet<String>(dlAddress));
		//下载样本
		for(int i=0; i<dlAddress.size(); i++) {
			System.out.println("创建下载任务：" + AppName.get(i));
			downloadUtil.downloadAPK(dlAddress.get(i), DefaultAPKCollectionPath, APKSmd5.get(i));
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}

}
