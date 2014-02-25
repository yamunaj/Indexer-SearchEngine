import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
public class Indexing 
{
	static int occurOnceCount =0;
	private static HashMap<ArrayList<String>,Integer> maxTF = new HashMap<ArrayList<String>,Integer>();
	private static ArrayList<Integer> doclen = new ArrayList<Integer>();
	private static TreeMap<String,Integer> docFreq = new TreeMap<String,Integer>();
	private static HashMap<String,Integer> hm = new HashMap<String,Integer>();
	private static TreeMap<String,Integer> stopWords = new TreeMap<String,Integer>();
	private static TreeMap<Integer,ArrayList<String>> sortedMap = new TreeMap<Integer,ArrayList<String>>();
	private static TreeMap<String,ArrayList<ArrayList<Integer>>> postings = new TreeMap<String,ArrayList<ArrayList<Integer>>>();
	private static TreeMap<String,ArrayList<ArrayList<String>>> compressed = new TreeMap<String,ArrayList<ArrayList<String>>>();
	static int totCountDocument =0;
	static int fileCount = 0;
	static int oldfileCount = 0;
	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	public static void main(String[] args) 
	{
		final long startTime = System.currentTimeMillis();

				try
		{

			FileReader rd = new FileReader(args[1]);
			BufferedReader bufRead = new BufferedReader(rd);
			String str;
			while((str = bufRead.readLine()) != null)
			{
				//System.out.println(str);
				stopWords.put(str.trim(), 1);
			}
			bufRead.close();
			/*System.out.println(stopWords.size());
			for(Entry e : stopWords.entrySet())
			{
				System.out.println(e.getKey());
			}
			System.out.println("-----------------------------------------------------------------------");*/
			File dir = new File(args[0]);
			File[] filesList = dir.listFiles();
			//int tokenCount =0;

			for(File file : filesList)
			{
				totCountDocument =0;
				String d = new String();
				if(file.isFile())
				{
					//System.out.println(file.getName());
					//File fl = new File(file.getName());
					FileReader r = new FileReader(file);
					BufferedReader bufReader = new BufferedReader(r);
					//System.out.println("hello");
					String s;

					//String splits = ""
					while((s = bufReader.readLine()) != null)
					{
						d = null;
						//System.out.println("hello");
						StringTokenizer tokens = new StringTokenizer(s,". ",false);
						while (tokens.hasMoreTokens()) 
						{
							String token;
							token = tokens.nextToken();
							token = token.trim();
							token = token.replaceAll("[0-9]", "");
							token = token.replaceAll("[^a-z|.|'s|-]", "");
							//checking for SGML tags
							if(!(token.matches("\\<.*?>")) )
							{
								
								Stemmer stem = new Stemmer();
								stem.add(token.toLowerCase().toCharArray(), token.length());
								stem.stem();
								//{
								String data;
								data = stem.toString();
								d = data;
								totCountDocument++;
								//System.out.println(stopWords.containsKey(data));
								if(!stopWords.containsKey(data))
								{
									if(postings.containsKey(data))
									{
										boolean alreadyexistsFlag = false;
										int index = 0;
										ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
										temp = postings.get(data);
										//System.out.println("Temp size"+temp.size()+", "+fileCount);
										ArrayList<Integer> t = new ArrayList<Integer>();
										for(int i =0;i<temp.size();i++)
										{
											if(temp.get(i).get(0) == fileCount+1)
											{
												alreadyexistsFlag = true;
												//System.out.println("alreadyexistsFlag "+temp.get(i).get(0)+", "+(fileCount+1)+", "+data);
												index = i;

											}
										}
										if(alreadyexistsFlag)
										{
											t = temp.get(index);
											int docid = t.get(0);
											int df = t.get(1);
											t.clear();
											t.add(docid);
											t.add(df+1);
											temp.remove(index);
											temp.add(index,t);
										}
										else
										{
											t.add(fileCount+1);
											t.add(1);
											temp.add(t);
										}
										postings.put(data, temp);

									}
									else
									{
										boolean alreadyexistsFlag = false;
										int index =0;
										ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
										//temp = postings.get(data);
										ArrayList<Integer> t = new ArrayList<Integer>();
										for(int i =0;i<temp.size();i++)
										{
											if(temp.get(i).get(0) == fileCount+1)
											{
												alreadyexistsFlag = true;
												index =i;
											}
										}
										if(alreadyexistsFlag)
										{
											t = temp.get(index);
											int docid = t.get(0);
											int df = t.get(1);
											t.clear();
											t.add(docid);
											t.add(df+1);
											temp.remove(index);
											temp.add(index,t);
										}
										else
										{
											t.add(fileCount+1);
											t.add(1);
											temp.add(t);
										}
										postings.put(data, temp);
									}
									//System.out.println(data);
								}
								//}
								//break;
							}
							oldfileCount = fileCount;
						}
					}
				}
				//System.out.println("d : "+d);
				if(docFreq.containsKey(d))
				{
					int value = docFreq.get(d);
					docFreq.put(d, value+1);
				}
				else
				{
					docFreq.put(d, 1);
				}
				d = null;
				Integer val = (Integer) hm.get(d);
				if(val == null)
				{
					hm.put(d,1);
					hm.get(d);
				}

				else
				{
					hm.put(d, val + 1);
				}
				
				for(String key : hm.keySet())
				{
					if(sortedMap.get(hm.get(key)) != null)
					{
						ArrayList<String> a = sortedMap.get(hm.get(key));
						a.add(key);
						sortedMap.put(hm.get(key),a);
						if(sortedMap.size()>30)
						{
							sortedMap.remove(sortedMap.firstKey());
						}

					}
					else
					{
						ArrayList<String> a = new ArrayList<String>();
						a.add(key);
						sortedMap.put(hm.get(key),a);
						if(sortedMap.size()>30)
						{
							sortedMap.remove(sortedMap.firstKey());
						}
					}
				}
				//sortedMap = (TreeMap<Integer, ArrayList<String>>) sortedMap.descendingMap();
				maxTF.put(sortedMap.get(sortedMap.lastKey()), sortedMap.lastKey());
				oldfileCount = fileCount;
				fileCount++;
				doclen.add(totCountDocument);
				totCountDocument = 0;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		int size =0;
		for(Entry e: postings.entrySet())
		{
			ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
			temp = (ArrayList<ArrayList<Integer>>) e.getValue();
			docFreq.put((String) e.getKey(), temp.size());
			//System.out.println(e.getKey() +", "+e.getValue());
			size = size + Integer.SIZE/2*temp.toArray().length + e.getKey().toString().getBytes().length;
			
		}
		
		/*for(Entry e: docFreq.entrySet())
		{
			System.out.println(e.getKey() +", "+e.getValue());
		}*/
		compress();
		int compressedSize =0;
		for(Entry e: compressed.entrySet())
		{
			//System.out.println(e.getKey() +", "+e.getValue());
			ArrayList<ArrayList<String>> temp = new ArrayList<ArrayList<String>>();
			temp = (ArrayList<ArrayList<String>>) e.getValue();
			for(int i=0;i<temp.size() ;i++)
			{
				ArrayList<String> t = temp.get(i);
				for(String s:t)
				{
				compressedSize += s.getBytes().length;
				}
			
			}
			compressedSize = compressedSize + e.getKey().toString().getBytes().length;
		}
		String[] input = {"Reynolds", "NASA", "Prandtl", "flow", "pressure", "boundary", "shock"};
		//System.out.println("size of the index uncompressed (in bytes) : "+getBytesize(postings).length);
		//System.out.println("size of the index compressed (in bytes) : "+getBytesize(compressed).length);
		
		System.out.println("----------------------------------------------------------------------------OUTPUT-----------------------------------------------------------------------------");
		//System.out.println("Integer size : "+Integer.SIZE/8);
		for(int i=0;i<input.length;i++)
		{
			Stemmer stem = new Stemmer();
			stem.add(input[i].toLowerCase().toCharArray(), input[i].length());
			stem.stem();
			//{	
			String data;
			data = stem.toString();
			//System.out.println("DAta : "+data);
			System.out.println(input[i]+" - DF: "+docFreq.get(data)+", Inverted list length: "+Integer.SIZE/2 *(postings.get(data).size())+" bytes");
			System.out.println("TF: "+postings.get(data));
			System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------------");

		}
		System.out.println("size of the index uncompressed (in bytes) : "+size);
		System.out.println("size of the index compressed (in bytes) : "+compressedSize);
		System.out.println("number of inverted lists in the index : "+postings.size());
		final long elapsedTimeMillis = System.currentTimeMillis() - startTime;
		System.out.println("the elapsed time required to build index " +(elapsedTimeMillis*0.001)+" secs");
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void compress()
	{


		for(Entry e : postings.entrySet())
		{
			ArrayList<ArrayList<String>> docs = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<Integer>> temp = new ArrayList<ArrayList<Integer>>();
			temp = (ArrayList<ArrayList<Integer>>) e.getValue();
			for(int i=0;i<temp.size();i++)
			{
				ArrayList<String> tfDoc = new ArrayList<String>();
				ArrayList<Integer> doc = new ArrayList<Integer>();
				doc = temp.get(i);
				String docid = getDelta(Integer.toBinaryString(doc.get(0)));
				String tf = getGamma(Integer.toBinaryString(doc.get(1)));
				tfDoc.add(docid);
				tfDoc.add(tf);
				docs.add(tfDoc);
			}
			compressed.put((String) e.getKey(), docs);
		}
	}
	public static String getGamma(String s)
	{
		if(s.equals("1"))
		{
			return "0";
		}
		else
		{
			String offset = s.substring(1);
			Integer len = offset.length();
			String length = new String();
			for(int i=0;i<len;i++)
			{
				length.concat("1"); 
			}
			length.concat("0"); 
			return length.concat(offset);
		}
	}
	public static String getDelta(String s)
	{
		if(s.equals("1"))
		{
			return "0";
		}
		else
		{
			String str = Integer.toBinaryString(s.length());
			String length = getGamma(str);
			String offset = s.substring(1);

			return length.concat(offset);
		}
	}
	public static byte[] getBytesize(Object data) throws IOException
	{
		ByteArrayOutputStream byteObject = new ByteArrayOutputStream();  
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteObject);  

		objectOutputStream.writeObject(data);

		objectOutputStream.flush();  
		objectOutputStream.close();  
		byteObject.close();
		return byteObject.toByteArray();
	}
}
