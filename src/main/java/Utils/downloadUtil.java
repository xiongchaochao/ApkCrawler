package Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.filechooser.FileNameExtensionFilter;


public class downloadUtil {
	private static int threadNumber;
	private static int runningThreadNumber;
	private static int fileSize;
	private static int blockSize;
	private static int ApkMD5;
	
	
	
	/*
	 * 1. 根据需要开启的线程数将需要下载的文件分块
	 * 
	 */
	public static void downloadAPK(String adress, String path, String APKmd5) {
		threadNumber = 5;
		runningThreadNumber = threadNumber;
		fileSize = getFileSize(adress);
		if(fileSize == 0) {
			System.out.println("获取文件大小失败："+ adress);
		}
		blockSize = fileSize / threadNumber; 
		for(int threadID=1; threadID<=threadNumber; threadID++) {
			long startPos = (threadID-1) * blockSize;  
			long endPos = threadID*blockSize - 1;
			if(threadID == threadNumber) {
				endPos = fileSize;
			}
			new Thread(new downloadClass(threadID, startPos, endPos, adress, path, APKmd5)).start();

		}
		
	}
	
	private static int getFileSize(String adress) {
		int filesize = 0;
		try {
			URL url = new URL(adress);
			URLConnection urlConnection = url.openConnection();
			filesize = urlConnection.getContentLength();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return filesize;
	}
	
	static class downloadClass implements Runnable{
		private int threadID;
		private long startPos;
		private long endPos;
		private String downloadUrl;
		private String fileCollectPath;
		//记录下载进度的临时文件
		private String tempFilePath;
		private String filename;
		
		
		
		public downloadClass(int threadid, long startpos, long endpos, String downloadURL, String path, String APKmd5) {
			threadID = threadid;
			startPos = startpos;
			endPos = endpos;
			downloadUrl = downloadURL;
			fileCollectPath = path;
			filename = fileCollectPath + "\\" + APKmd5 + ".apk";
			tempFilePath = path + "\\" + APKmd5 + "_" + threadID;
		}
		
		public void run() {
			// TODO Auto-generated method stub
			int currentIndex = 0;
			File tempfile = new File(tempFilePath);
			//如果存在断点文件，从断点处开始下载
			if(tempfile.exists() && tempfile.length()>0) {
				try {
					FileInputStream fileInputStream = new FileInputStream(tempfile);
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
					currentIndex = Integer.valueOf(bufferedReader.readLine());
					startPos += currentIndex;
					fileInputStream.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			try {
				HttpURLConnection  connection = (HttpURLConnection) new URL(downloadUrl).openConnection();
				connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
				InputStream inputStream = (InputStream) connection.getInputStream();
				RandomAccessFile randomAccessFile = new RandomAccessFile(new File(filename), "rw");
				randomAccessFile.seek(startPos);
				byte[] buffer = new byte[1024*1024];
				int len = 0;
				while((len=inputStream.read(buffer)) != -1) {
					randomAccessFile.write(buffer, 0, len);
					RandomAccessFile rsTempFile = new RandomAccessFile(tempFilePath, "rwd");
					currentIndex += len;
					rsTempFile.write(String.valueOf(currentIndex).getBytes());
					rsTempFile.close();
				}
				inputStream.close();
				randomAccessFile.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				synchronized(downloadUtil.class) {
					if(--threadNumber == 0) {
						for(int i=0; i<runningThreadNumber; i++) {
							new File(tempFilePath.substring(0, tempFilePath.length()-1) + String.valueOf(i+1)).delete();
						}
						System.out.println("完成下载，删除" + tempFilePath);
					}
				}
			}
		}
	}
}

